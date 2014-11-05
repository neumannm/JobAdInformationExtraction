package spinfo.tm.extraction.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.TextCleaner;

/**
 * Use parsed data to find Competences by inspecting dependency relations
 * between sentence constituents.
 * 
 * @author neumannm
 * 
 */
// TODO: split conjunctions in conjuncts
public class DepCompetenceFinder {
	private static Logger logger = Logger.getLogger("DepCompetenceFinder");

	// key: verb; value: restrictions
	private Map<String, String> verbsOfInterest;

	/**
	 * Constructor.
	 * 
	 * @param verbsOfInterest
	 *            verbs that trigger competence extraction, mapped to a
	 *            restriction (or null if there is none)
	 */
	public DepCompetenceFinder(Map<String, String> verbsOfInterest) {
		if (!(verbsOfInterest == null)) {
			this.verbsOfInterest = verbsOfInterest;
		} else
			this.verbsOfInterest = new HashMap<String, String>();
	}

	/**
	 * Return a set of phrases that are likely to denote competences for a given
	 * paragraph.
	 * 
	 * @param par
	 *            the paragraph to be examined
	 * @return set of phrases ({@link SlotFiller})
	 */
	public Set<SlotFiller> findCompetences(Paragraph par) {
		Set<SlotFiller> results = new HashSet<SlotFiller>();

		Map<Integer, Sentence> parsedCU = par.getSentenceData();
		Sentence sd;
		for (Integer sentence : parsedCU.keySet()) {
			sd = parsedCU.get(sentence);

			logger.info("\n" + sd.toString());

			boolean subjectIsNoun = subjectIsNoun(sd);
			int subjectID = getSubjectID(sd);

			String[] lemmas = sd.getLemmas();

			for (int arrayIndex = 0; arrayIndex < lemmas.length; arrayIndex++) {
				if (verbsOfInterest.containsKey(lemmas[arrayIndex])) {

					int verbID = arrayIndex + 1;

					// lemma i is verb of interest - this sentence may contain
					// slotfillers
					List<SlotFiller> filler = new ArrayList<>();
					// are there any restrictions like required appositions or
					// particles?
					String restriction = verbsOfInterest
							.get(lemmas[arrayIndex]);

					if (restriction == null) {
						// there are no restrictions
						// must still check if there's a second verb
						int secondVerbID = getSecondVerbID(sd, verbID);

						if (secondVerbID != -1) {
							// there is a second verb
							List<SlotFiller> objectsV2 = getObjects(
									lemmas[secondVerbID - 1], secondVerbID, sd,
									par);
							if (objectsV2.isEmpty()) {
								filler.addAll(getObjects(lemmas[verbID - 1],
										verbID, sd, par));
							} else
								filler.addAll(objectsV2);
						}

						else if (subjectIsNoun) {
							filler.add(getSubjectNP(sd, par, subjectID));
						} else
							filler.addAll(getObjects(lemmas[arrayIndex],
									verbID, sd, par));
					} else {
						switch (restriction) {
						case "PTKVZ":
							// particle needed in SVP position
							if (particleExists(sd, verbID)) {
								// okay, get competence
								if (subjectIsNoun) {
									filler.add(getSubjectNP(sd, par, subjectID));
								} else
									// TODO: particle not part of result!
									filler.addAll(getObjects(
											lemmas[arrayIndex], verbID, sd, par));
							} else {
								// no particle found - continue looking for
								// other verbs
								continue;
							}
							break;
						case "V":
							// second verb (that is of interest) needed in OC
							// position --> if subj == PPER|null, then obj of
							// second verb must be competence
							int secondVerbID = getSecondVerbID(sd, verbID);

							if (secondVerbID != -1) {

								List<SlotFiller> objectsV2 = getObjects(
										lemmas[secondVerbID - 1], secondVerbID,
										sd, par);
								if (objectsV2.isEmpty()) {
									filler.addAll(getObjects(
											lemmas[verbID - 1], verbID, sd, par));
									// break;
								} else
									filler.addAll(objectsV2);
							} else
								break; // requirement not fulfilled
							break;
						case "AP":
							// aposition needed
							int apoID = getAppositionID(sd, arrayIndex + 1);
							if (apoID >= 0) {
								filler.add(new SlotFiller(getPhrase(apoID, sd,
										new TreeSet<Integer>()), par.getID()));
							}
							break;
						default:
							// there are other restrictions
							logger.severe("Restriction " + restriction
									+ " not defined!");
							break;
						}
					}

					/*
					 * add result to list of results
					 */
					if (!filler.isEmpty())
						results.addAll(filler);
				}

			}
		}
		return results;
	}

	private int getSubjectID(Sentence sd) {
		String[] depLabels = sd.getDepLabels();
		for (int i = 0; i < depLabels.length; i++) {
			if ("SB".equals(depLabels[i])) {
				return i + 1;
			}
		}
		return -1;
	}

	private boolean subjectIsNoun(Sentence sd) {
		String subjectPOS = null;
		subjectPOS = getSubjectPOS(sd);

		if (subjectPOS == null) {
			// no subject
			return false;
		} else {
			switch (subjectPOS) {
			case "PPER":
				return false;
			case "NN":
				return true;
			default:
				// other (should not happen)
				logger.warning("Subject has other POS than PPER or NN or null");
				break;
			}
		}
		return false;
	}

	private SlotFiller getSubjectNP(Sentence sd, Paragraph par, int subjectID) {
		// arrayIndex = subjectID - 1
		String subject = sd.getTokens()[subjectID - 1];
		logger.info(String.format("Subject is a Noun (%s)", subject));
		/*
		 * process, i.e. Subject and Dependants seem to be the competences
		 */
		String argument = getPhrase(subjectID, sd, new TreeSet<Integer>());

		SlotFiller filler = new SlotFiller(argument, par.getID());
		return filler;
	}

	private String getSubjectPOS(Sentence sd) {
		String[] depLabels = sd.getDepLabels();
		for (int i = 0; i < depLabels.length; i++) {
			if ("SB".equals(depLabels[i])) {
				return sd.getPOSTags()[i];
			}
		}
		return null;
	}

	private int getAppositionID(Sentence sd, int verbID) {
		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID && sd.getDepLabels()[i].equals("OP")
					&& sd.getPOSTags()[i].startsWith("AP")) {
				return i + 1;
			}
		}
		return -1;
	}

	private int getSecondVerbID(Sentence sd, int firstVerbID) {
		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == firstVerbID && sd.getDepLabels()[i].equals("OC")
					&& sd.getPOSTags()[i].startsWith("V")
					&& verbsOfInterest.containsKey(sd.getLemmas()[i])) {
				return i + 1;
			}
		}
		return -1;
	}

	private boolean particleExists(Sentence sd, int id) {
		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == id && sd.getDepLabels()[i].equals("SVP")) {
				return sd.getPOSTags()[i].equals("PTKVZ");
			}
		}
		return false;
	}

	private List<SlotFiller> getObjects(String head, int headID, Sentence sd,
			Paragraph par) {
		List<SlotFiller> filler = new ArrayList<>();

		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == headID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.getTokens()[i];
				// String dependantLemma = sd.plemmas[i];
				String dependency = sd.getDepLabels()[i];

				if ("OA".equals(dependency) || "OA2".equals(dependency)
						|| "OC".equals(dependency) || "OP".equals(dependency)
						|| "PD".equals(dependency)) {
					logger.info(String.format(
							"Found object for verb '%s':\t %s (DEP: %s )",
							head.toUpperCase(), dependant, dependency));
					String argument = getPhrase(i + 1, sd,
							new TreeSet<Integer>());

					filler.add(new SlotFiller(argument, par.getID()));
				}
			}
		}
		return filler;
	}

	private String getPhrase(int headID, Sentence sd, Set<Integer> components) {
		String lemma = sd.getLemmas()[headID - 1];

		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == headID) {
				String dependant = sd.getTokens()[i];
				String dependency = sd.getDepLabels()[i];

				logger.info(String.format(
						"Found dependant for lemma '%s':\t %s (DEP: %s )",
						lemma.toUpperCase(), dependant, dependency));

				getPhrase(i + 1, sd, components);
			}
		}
		components.add(headID);

		/*
		 * Phrase zusammensetzen
		 */
		StringBuffer sb = new StringBuffer();
		for (Integer i : components) {
			sb.append(sd.getTokens()[i - 1]).append(" ");
		}

		/*
		 * Entferne Leerzeichen vor Satzzeichen
		 */
		return TextCleaner.removeUnneccessaryWhitespace(sb.toString());
	}
}
