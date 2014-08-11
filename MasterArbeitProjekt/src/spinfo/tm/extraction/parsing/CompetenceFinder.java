package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.parsing.util.Relation;

/**
 * Use parsed data to find Competences by inspecting dependency relations
 * between sentence constituents.
 * 
 * @author neumannm
 * 
 */
/*
 * TODO: add positions to SlotFillers
 */
public class CompetenceFinder {

	private Map<String, Relation> verbsOfInterest;

	public CompetenceFinder() {
		if (verbsOfInterest == null) {
			verbsOfInterest = new HashMap<String, Relation>();
		}
	}

	public CompetenceFinder(Map<String, Relation> verbsOfInterest) {
		this();
		this.verbsOfInterest = verbsOfInterest;
	}

	public List<SlotFiller> findCompetences(ClassifyUnit cu) {
		List<SlotFiller> results = new ArrayList<SlotFiller>();

		List<SentenceData09> parsedCU = cu.getSentenceData();

		for (SentenceData09 sd : parsedCU) {
			System.out.println("\n" + sd.toString());

			String[] lemmas = sd.plemmas;

			for (int i = 0; i < lemmas.length; i++) {
				if (verbsOfInterest.containsKey(lemmas[i])) {
					List<SlotFiller> filler = new ArrayList<>();
					switch (verbsOfInterest.get(lemmas[i])) {
					case OBJECT: {
						filler.addAll(getObjects(lemmas[i], i + 1, sd));
						break;
					}
					case SUBJECT: {
						filler.add(getSubject(lemmas[i], i + 1, sd));
						break;
					}
					case BOTH: {
						filler.addAll(getSubjAndObj(lemmas[i], i + 1, sd));
						break;
					}
					default:
						break;
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

	private List<SlotFiller> getSubjAndObj(String lemma, int verbID,
			SentenceData09 sd) {
		List<SlotFiller> filler = new ArrayList<>();

		int[] heads = sd.pheads;
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.forms[i];
				String dependantLemma = sd.plemmas[i];
				String dependency = sd.plabels[i];

				if ("SB".equals(dependency)) {
					String sbPOS = sd.ppos[i];
					/*
					 * check subject: is it a noun phrase or pronoun?
					 */
					if ("PPER".equals(sbPOS)
							&& ("wir".equals(dependant) || "sie"
									.equals(dependant))) {
						System.out.println("Subject is a Pronoun");
						/*
						 * Process, i.e. look for competences in verb's objects
						 */
						filler.addAll(getObjects(lemma, verbID, sd));
					} else if ("NN".equals(sbPOS)) {
						System.out.println(String.format(
								"Subject is a Noun (%s)", dependant));
						/*
						 * process, i.e. Subject and Dependants seem to be the
						 * competences
						 */
						String argument = getPhrase(i, sd,
								new TreeSet<Integer>());
						// ...
						// einzige Möglichkeit für position ist Token-Nr.! Die
						// sich aber definitiv von der Token-Nr. aus dem
						// Training
						// unterscheiden wird, da hier Interpunktionen
						// mitgezählt werden!
						filler.add(new SlotFiller(argument, -1));
					}
				}

				if ("PD".equals(dependency)) {
					System.out.println("Dependant '" + dependant
							+ "' is possibly modifier for verb");
					// TODO ?
				}
			}
		}
		return filler;
	}

	private SlotFiller getSubject(String lemma, int verbID, SentenceData09 sd) {
		SlotFiller filler = null;
		int[] heads = sd.pheads;
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.forms[i];
				String dependantLemma = sd.plemmas[i];
				String dependency = sd.plabels[i];

				if ("SB".equals(dependency)) {
					String sbPOS = sd.ppos[i];
					// prüfe Subjekt: "wir", "Sie" oder was anderes?
					if ("PPER".equals(sbPOS)
							&& ("wir".equals(dependant) || "sie"
									.equals(dependant))) {
						System.out.println("Subject is a Pronoun");

					} else if ("NN".equals(sbPOS)) {
						System.out.println("Subject is a Noun");
						String argument = getPhrase(i, sd,
								new TreeSet<Integer>());
						return new SlotFiller(argument, -1);
					}
				}

				if ("PD".equals(dependency)) {
					System.out.println("Dependant '" + dependant
							+ "' is possibly modifier for verb");
				}
			}
		}
		return filler;
	}

	private List<SlotFiller> getObjects(String lemma, int verbID,
			SentenceData09 sd) {
		List<SlotFiller> filler = new ArrayList<>();

		int[] heads = sd.pheads;
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.forms[i];
				String dependantLemma = sd.plemmas[i];
				String dependency = sd.plabels[i];

				if ("OA".equals(dependency) || "OA2".equals(dependency)
						|| "OC".equals(dependency)) {
					System.out.println(String.format(
							"Found object for verb '%s':\t %s (DEP: %s )",
							lemma.toUpperCase(), dependant, dependency));
					String argument = getPhrase(i, sd, new TreeSet<Integer>());
					// ...
					// einzige Möglichkeit für position ist Token-Nr.! Die sich
					// aber definitiv von der Token-Nr. aus dem Training
					// unterscheiden wird, da hier Interpunktionen mitgezählt
					// werden!
					filler.add(new SlotFiller(argument, -1));
				}
			}
		}
		return filler;
	}

	private String getPhrase(int index, SentenceData09 sd,
			Set<Integer> components) {

		int id = index + 1; // ID am Anfang der Zeile

		// String lemma = sd.plemmas[index];
		// String form = sd.forms[index];

		int[] heads = sd.pheads;
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
			sb.append(sd.forms[i]).append(" ");
		}

		return sb.toString();
	}
}
