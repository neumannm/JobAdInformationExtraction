package spinfo.tm.extraction.learning;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

public class NaiveBayes implements ClassifierStrategy {

	/** Number of documents for each class */
	private Map<Boolean, Integer> classFrequencies = new HashMap<Boolean, Integer>();
	/**
	 * For each class, we map a mapping of all the terms of that class to their
	 * term frequencies:
	 */
	private Map<Boolean, Map<String, Integer>> precedingTokenFrequenciesPerClass = new HashMap<Boolean, Map<String, Integer>>();

	private int tokenCount = 0;

	@Override
	public ClassifierStrategy train(final PotentialSlotFillingAnchor anchor) {
		/*
		 * use as features?
		 */
		boolean competence = anchor.isCompetence(); // <-- am wichtigsten
		String precedingToken = anchor.getPrecedingToken();
		String precedingPOS = anchor.getPrecedingPOS();
		String followingPOS = anchor.getFollowingPOS();
		String followingToken = anchor.getFollowingToken();
		int tokenPos = anchor.getTokenPos();
		/*
		 * 
		 * ***********************
		 */

		/*
		 * Wir zählen mit, wie viele SlotFiller wir insgesamt haben, für die
		 * Berechnung der A-Priori-Wahrscheinlichkeit ('prior probability')
		 */
		tokenCount++;

		/*
		 * pro Klasse wird gespeichert, wie viele SlotFiller auftreten
		 */
		Integer classCount = classFrequencies.get(competence);
		if (classCount == null) {
			/* Erstes Vorkommen der Klasse: */
			classCount = 0;
		}
		classFrequencies.put(competence, classCount + 1);

		/*
		 * Für die Evidenz: Häufigkeit eines Terms in den Dokumenten einer
		 * Klasse.
		 */
		Map<String, Integer> termCount = precedingTokenFrequenciesPerClass
				.get(competence);
		if (termCount == null) {
			/* Erstes Vorkommen der Klasse: */
			termCount = new HashMap<String, Integer>();
		}

		/* Jetzt für jeden Term hochzählen: */
		Integer count = termCount.get(precedingToken);
		if (count == null) {
			/* Erstes Vorkommen des Tokens: */
			count = 0;
		}
		/*
		 * Wir addieren hier die Häufigkeit des Tokens
		 */
		termCount.put(precedingToken, count + 1);

		precedingTokenFrequenciesPerClass.put(competence, termCount);
		return this;
	}

	@Override
	public Boolean classify(final PotentialSlotFillingAnchor doc) {
		/* Das Maximum... */
		float max = Float.NEGATIVE_INFINITY;
		Set<Boolean> classes = precedingTokenFrequenciesPerClass.keySet();
		Boolean best = classes.iterator().next();
		/* ...der möglichen Klassen... */
		for (Boolean c : classes) {
			/*
			 * Das Produkt oder die Summe der Termwahrscheinlichkeiten ist
			 * unsere Evidenz...
			 */
			float evidence = 0f; // TODO: correct?

			String term = doc.getToken();
			float e = evidence(term, c);
			evidence = (float) (evidence * Math.log(e)); // 0 * x = 0 ??

			float prior = prior(c);
			/* Die eigentliche Naive-Bayes Berechnung: */
			float probability = (float) (prior + evidence);
			/* Und davon das Maximum: */
			if (probability >= max) {
				max = probability;
				best = c;
			}
		}
		return best;
	}

	private float prior(final Boolean c) {
		/* The relative frequency of the class: */
		Integer classCount = classFrequencies.get(c);
		float prior = (float) Math.log(classCount / (float) tokenCount);
		return prior;
	}

	private float evidence(final String term, final Boolean c) {
		Map<String, Integer> termFreqsForClass = precedingTokenFrequenciesPerClass
				.get(c);
		Integer termFrequency = termFreqsForClass.get(term);
		float evidence;
		/*
		 * Dieser Test fehlte auch noch: wenn ein Term in den
		 * Trainingsdokumenten für die Klasse nicht vorkommt, ist die Evidenz
		 * unendlich klein (weil wir damit vergleiche bei der Suche nach dem
		 * Maximum):
		 */
		if (termFrequency != null) {
			evidence = termFrequency / (float) sum(termFreqsForClass);
		} else {
			evidence = Float.NEGATIVE_INFINITY;
		}
		return evidence;
	}

	/* Die Summe der Häufigkeiten der Termfrequenzen für die Klasse: */
	private float sum(final Map<String, Integer> termFreqsForClass) {
		int sum = 0;
		for (Integer i : termFreqsForClass.values()) {
			sum += i;
		}
		return sum;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
