package spinfo.tm.extraction.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.PhraseCleaner;

/**
 * Use parsed data to find Competences by inspecting dependency relations
 * between sentence constituents.
 * 
 * @author neumannm
 * 
 */
public class DepCompetenceFinder {

	private Map<String, String> verbsOfInterest; // key: verb; value:
													// restrictions

	// TODO: make use of restrictions!
	// TODO: split conjunctions in conjuncts

	public DepCompetenceFinder() {
		if (verbsOfInterest == null) {
			verbsOfInterest = new HashMap<String, String>();
		}
	}

	public DepCompetenceFinder(Map<String, String> verbsOfInterest) {
		this();
		this.verbsOfInterest = verbsOfInterest;
	}

	public List<SlotFiller> findCompetences(Paragraph cu) {
		List<SlotFiller> results = new ArrayList<SlotFiller>();

		Map<Integer, Sentence> parsedCU = cu.getSentenceData();
		Sentence sd;
		for (Integer sentence : parsedCU.keySet()) {
			sd = parsedCU.get(sentence);

			System.out.println("\n" + sd.toString());

			String[] lemmas = sd.getLemmas();

			for (int i = 0; i < lemmas.length; i++) {
				if (verbsOfInterest.containsKey(lemmas[i])) {
					// lemma i is verb of interest - this sentence may contain
					// slotfillers
					List<SlotFiller> filler = new ArrayList<>();

					String restriction = verbsOfInterest.get(lemmas[i]);
					// are there any restrictions like required appositions or
					// particles?

					if ("PTKVZ".equals(restriction)) {
						// particle needed in SVP position
						if (particleExists(sd, i + 1)) {
							// get dependants of particle
						}
					} else if ("V".equals(restriction)) {
						// second verb needed in OC position
						if (secondVerbExists(sd, i + 1)) {
							// second verb should be in verbsOfInterest
							// subject of first verb must be checked
						}
					} else if ("AP".equals(restriction)) {
						// apposition needed
						if (appositionExists(sd, i + 1)) {

						}
					} else {
						// there are no restrictions
						// problem: when we have 2 verbs....
						filler.addAll(lookForCompetences(lemmas[i], i + 1, sd,
								cu));
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

	private boolean appositionExists(Sentence sd, int id) {
		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == id && sd.getDepLabels()[i].equals("OP")) {
				return sd.getPOSTags()[i].startsWith("AP");
			}
		}
		return false;
	}

	private boolean secondVerbExists(Sentence sd, int id) {
		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == id && sd.getDepLabels()[i].equals("OC")) {
				return sd.getPOSTags()[i].startsWith("V");
			}
		}
		return false;
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

	private List<SlotFiller> lookForCompetences(String lemma, int verbID,
			Sentence sd, Paragraph par) {
		List<SlotFiller> filler = new ArrayList<>();

		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.getTokens()[i];
				String dependantLemma = sd.getLemmas()[i];
				String dependency = sd.getDepLabels()[i];

				if ("SB".equals(dependency)) {
					String sbPOS = sd.getPOSTags()[i];
					/*
					 * check subject: is it a noun phrase or pronoun?
					 */
					if ("PPER".equals(sbPOS)
							&& ("wir".equals(dependantLemma) || "Sie"
									.equals(dependant))) {
						System.out.println("Subject is a Pronoun");
						/*
						 * Process, i.e. look for competences in verb's objects
						 */
						filler.addAll(getObjects(lemma, verbID, sd, par));
					} else if ("NN".equals(sbPOS)) {
						System.out.println(String.format(
								"Subject is a Noun (%s)", dependant));
						/*
						 * process, i.e. Subject and Dependants seem to be the
						 * competences
						 */
						String argument = getPhrase(i, sd,
								new TreeSet<Integer>());

						filler.add(new SlotFiller(argument, Class.forID(par
								.getActualClassID())));
					}
					break;
				}

				/*
				 * tried to make use of modifiers but they are not connected to
				 * the object but to the verb... is that a problem??
				 */
				else if ("PD".equals(dependency)) {
					System.out
							.println(String
									.format("\tDependant '%s' is possibly modifier for verb %s (REL: %s, POS: %s)",
											dependant, lemma, dependency,
											sd.getPOSTags()[i]));
					if ("ADJD".equals(sd.getPOSTags()[i])) {
						// TODO predicate modifier for competence
					}
					if ("VVPP".equals(sd.getPOSTags()[i])) {
						// TODO modifier for competence
					}
					if ("NN".equals(sd.getPOSTags()[i])) {
						// TODO predicate is possibly also competence (like in
						// 'erforderlich sind Sprachkenntnisse' etc.)
					}
				} else if ("MO".equals(dependency)) {
					System.out
							.println(String
									.format("\tDependant '%s' is possibly modifier for verb %s (REL: %s, POS: %s)",
											dependant, lemma, dependency,
											sd.getPOSTags()[i]));
					// TODO use modifier for competence
				}
			}
		}
		return filler;
	}

	private List<SlotFiller> getObjects(String lemma, int verbID, Sentence sd,
			Paragraph par) {
		List<SlotFiller> filler = new ArrayList<>();

		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.getTokens()[i];
				// String dependantLemma = sd.plemmas[i];
				String dependency = sd.getDepLabels()[i];

				if ("OA".equals(dependency) || "OA2".equals(dependency)
						|| "OC".equals(dependency) || "OP".equals(dependency)) {
					System.out.println(String.format(
							"Found object for verb '%s':\t %s (DEP: %s )",
							lemma.toUpperCase(), dependant, dependency));
					String argument = getPhrase(i, sd, new TreeSet<Integer>());

					filler.add(new SlotFiller(argument, Class.forID(par
							.getActualClassID())));
				}
			}
		}
		return filler;
	}

	private String getPhrase(int index, Sentence sd, Set<Integer> components) {

		int id = index + 1; // ID am Anfang der Zeile

		// String lemma = sd.plemmas[index];
		// String form = sd.forms[index];

		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == id) {
				// String dependant = sd.forms[i];
				// String dependency = sd.plabels[i];

				// System.out.println(String.format(
				// "Found dependant for lemma '%s':\t %s (DEP: %s )",
				// lemma.toUpperCase(), dependant, dependency));

				getPhrase(i, sd, components);
			}
		}

		components.add(index);

		/*
		 * Phrase zusammensetzen
		 */
		StringBuffer sb = new StringBuffer();
		for (Integer i : components) {
			sb.append(sd.getTokens()[i]).append(" ");
		}

		/*
		 * Entferne Leerzeichen vor Satzzeichen
		 */
		return PhraseCleaner.removeUnneccessaryWhitespace(sb.toString());
	}
}
