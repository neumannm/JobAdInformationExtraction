package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;

import java.util.HashMap;
import java.util.Map;

import spinfo.tm.extraction.parsing.util.Relation;

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

	public boolean findCompetences(SentenceData09 sd) {
		System.out.println("\n" + sd.toString());

		String[] lemmas = sd.plemmas;

		for (int i = 0; i < lemmas.length; i++) {
			if (verbsOfInterest.containsKey(lemmas[i])) {
				process(lemmas[i], i + 1, sd); // i oder i+1?
				return true;
			}
		}
		return false;
	}

	private void process(String lemma, int verbID, SentenceData09 sd) {
		int[] heads = sd.pheads;
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.forms[i];// String dependant =
												// sd.plemmas[i];
				String dependency = sd.plabels[i];

				if ("SB".equals(dependency)) {
					String sbPOS = sd.ppos[i];
					// prüfe Subjekt: "wir", "Sie" oder was anderes?
					if ("PPER".equals(sbPOS) /*
											 * && ("wir".equals(dependant) ||
											 * "sie".equals(dependant))
											 */) {
						System.out.println("Subject is a Pronoun");
						// TODO: Process, i.e. look for competences in verbs
						// objects
					} else if ("NN".equals(sbPOS)) {
						System.out.println("Subject is a Noun");
						// TODO: process, i.e. Subject and Dependants seem to be
						// the competences
					}
				}

				if ("PD".equals(dependency)) {
					System.out.println("Dependant '" + dependant
							+ "' is possibly modifier for verb");
				}

				System.out.println(String.format(
						"Found dependant for verb '%s':\t %s (DEP: %s )",
						lemma.toUpperCase(), dependant, dependency));

				// TODO: suche nach Objekt
				String argument = getPhrase(i, sd, 1);
			}
		}

	}

	private String getPhrase(int index, SentenceData09 sd, int indents) {
		int id = index + 1; // ID am Anfang der Zeile
		String phrase = "";

		String lemma = sd.plemmas[index];
		String form = sd.forms[index];

		int[] heads = sd.pheads;
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == id) {
				String dependant = sd.forms[i];
				String dependency = sd.plabels[i];
				for (int j = 0; j < indents; j++) {
					System.out.print("\t");
				}

				System.out.println(String.format(
						"Found dependant for lemma '%s':\t %s (DEP: %s )",
						lemma.toUpperCase(), dependant, dependency));

				getPhrase(i, sd, indents++);
			}
		}

		//TODO: Phrase zusammensetzen
		return phrase;
	}
}
