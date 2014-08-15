package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.SlotFiller;

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

	private List<String> verbsOfInterest;

	public CompetenceFinder() {
		if (verbsOfInterest == null) {
			verbsOfInterest = new ArrayList<String>();
		}
	}

	public CompetenceFinder(List<String> verbsOfInterest) {
		this();
		this.verbsOfInterest = verbsOfInterest;
	}

	public List<SlotFiller> findCompetences(ClassifyUnit cu) {
		List<SlotFiller> results = new ArrayList<SlotFiller>();

		Map<String, SentenceData09> parsedCU = cu.getSentenceData();
		SentenceData09 sd;
		for (String sentence : parsedCU.keySet()) {
			sd = parsedCU.get(sentence);

			System.out.println("\n" + sd.toString());

			String[] lemmas = sd.plemmas;

			for (int i = 0; i < lemmas.length; i++) {
				if (verbsOfInterest.contains(lemmas[i])) {
					List<SlotFiller> filler = new ArrayList<>();

					filler.addAll(lookForCompetences(lemmas[i], i + 1, sd, cu));

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

	private List<SlotFiller> lookForCompetences(String lemma, int verbID,
			SentenceData09 sd, ClassifyUnit cu) {
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
							&& ("wir".equals(dependantLemma) || "Sie"
									.equals(dependant))) {
						System.out.println("Subject is a Pronoun");
						/*
						 * Process, i.e. look for competences in verb's objects
						 */
						filler.addAll(getObjects(lemma, verbID, sd, cu));
					} else if ("NN".equals(sbPOS)) {
						System.out.println(String.format(
								"Subject is a Noun (%s)", dependant));
						/*
						 * process, i.e. Subject and Dependants seem to be the
						 * competences
						 */
						String argument = getPhrase(i, sd,
								new TreeSet<Integer>());

						/*
						 * einzige Möglichkeit für position ist Token-Nr.! Die
						 * sich aber definitiv von der Token-Nr. aus dem
						 * Training unterscheiden wird, da hier Interpunktionen
						 * mitgezählt werden! Außerdem wird pro Satz neu
						 * nummeriert.
						 * 
						 * --> TODO: Trainingsdaten ändern? (neu trainieren mit
						 * anderem Tokenizer)
						 */
						filler.add(new SlotFiller(argument, cu.getID()));
					}
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
											sd.ppos[i]));
					if ("ADJD".equals(sd.ppos[i])) {
						// TODO predicate modifier for competence
					}
					if ("NN".equals(sd.ppos[i])) {
						// TODO predicate is possibly also competence (like in
						// 'erforderlich sind Sprachkenntnisse' etc.)
					}
				} else if ("MO".equals(dependency)) {
					System.out
							.println(String
									.format("\tDependant '%s' is possibly modifier for verb %s (REL: %s, POS: %s)",
											dependant, lemma, dependency,
											sd.ppos[i]));
					// TODO use modifier for competence
				}
			}
		}
		return filler;
	}

	private List<SlotFiller> getObjects(String lemma, int verbID,
			SentenceData09 sd, ClassifyUnit cu) {
		List<SlotFiller> filler = new ArrayList<>();

		int[] heads = sd.pheads;
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == verbID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.forms[i];
				// String dependantLemma = sd.plemmas[i];
				String dependency = sd.plabels[i];

				if ("OA".equals(dependency) || "OA2".equals(dependency)
						|| "OC".equals(dependency) || "OP".equals(dependency)) {
					System.out.println(String.format(
							"Found object for verb '%s':\t %s (DEP: %s )",
							lemma.toUpperCase(), dependant, dependency));
					String argument = getPhrase(i, sd, new TreeSet<Integer>());
					
					filler.add(new SlotFiller(argument, cu.getID()));
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

		/*
		 * Entferne Leerzeichen vor Satzzeichen
		 */
		return sb.toString().replaceAll("\\s(?=[.,:?!\"'*\\-\\(\\)])", "");
	}
}
