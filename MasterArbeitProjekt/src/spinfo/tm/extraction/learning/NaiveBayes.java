package spinfo.tm.extraction.learning;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

public class NaiveBayes implements ClassifierStrategy {

	/* Number of documents for each class */
	private Map<Boolean, Integer> classFrequencies = new HashMap<Boolean, Integer>();
	/*
	 * For each class (Boolean inClass / notInClass), we map a mapping of the
	 * features of that class to their frequencies:
	 */
	private Map<Boolean, Map<String, Integer>> precedingTokenFrequenciesPerClass = new HashMap<Boolean, Map<String, Integer>>();
	private Map<Boolean, Map<String, Integer>> precedingPOSFrequenciesPerClass = new HashMap<Boolean, Map<String, Integer>>();
	private Map<Boolean, Map<String, Integer>> followingTokenFrequenciesPerClass = new HashMap<Boolean, Map<String, Integer>>();
	private Map<Boolean, Map<String, Integer>> followingPOSFrequenciesPerClass = new HashMap<Boolean, Map<String, Integer>>();
	private Map<Boolean, Map<String, Integer>> POSFrequenciesPerClass = new HashMap<Boolean, Map<String, Integer>>();
	private Map<Boolean, Map<String, Integer>> tokenFrequenciesPerClass = new HashMap<Boolean, Map<String, Integer>>();

	private Map<Boolean, Integer> upperCaseStartFrequenciesPerClass = new HashMap<Boolean, Integer>();
	private Map<Boolean, Integer> suffixFrequenciesPerClass = new HashMap<Boolean, Integer>();
	private Map<Boolean, Integer> punctFollowingFrequenciesPerClass = new HashMap<Boolean, Integer>();

	private int tokenCount = 0;

	@Override
	public ClassifierStrategy train(final PotentialSlotFillingAnchor anchor) {
		boolean inClass = anchor.isCompetence();

		String token = anchor.getToken();
		String POS = anchor.getPOS();
		String precedingToken = anchor.getPrecedingToken();
		String precedingPOS = anchor.getPrecedingPOS();
		String followingPOS = anchor.getFollowingPOS();
		String followingToken = anchor.getFollowingToken();

		boolean upperCaseStart = anchor.startsWithUpperCase();
		boolean hasSuffixOfInterest = anchor.hasSuffixOfInterest();
		boolean punctuationFollowing = anchor.isPunctuationFollowing();

		/*
		 * Wir zählen mit, wie viele SlotFiller wir insgesamt haben, für die
		 * Berechnung der A-Priori-Wahrscheinlichkeit ('prior probability')
		 */
		tokenCount++;

		/*
		 * pro Klasse wird gespeichert, wie viele SlotFiller auftreten
		 */
		Integer count = classFrequencies.get(inClass);
		if (count == null) {
			/* Erstes Vorkommen der Klasse: */
			count = 0;
		}
		classFrequencies.put(inClass, count + 1);

		/*
		 * Für die Evidenz: Häufigkeit, mit der Token mit Großbuchstaben beginnt
		 */
		if (upperCaseStart) {
			count = upperCaseStartFrequenciesPerClass.get(inClass);
			if (count == null)
				count = 0;
			upperCaseStartFrequenciesPerClass.put(inClass, count + 1);
		}

		/*
		 * Für die Evidenz: Häufigkeit, mit der Token auf ein bestimmtes Suffix
		 * endet
		 */
		if (hasSuffixOfInterest) {
			count = suffixFrequenciesPerClass.get(inClass);
			if (count == null)
				count = 0;
			suffixFrequenciesPerClass.put(inClass, count + 1);
		}

		/*
		 * Für die Evidenz: Häufigkeit, mit der nach dem Token ein
		 * Interpunktionszeichen kommt
		 */
		if (punctuationFollowing) {
			count = punctFollowingFrequenciesPerClass.get(inClass);
			if (count == null)
				count = 0;
			punctFollowingFrequenciesPerClass.put(inClass, count + 1);
		}

		/*
		 * Für die Evidenz: Häufigkeit vorangehender Tokens
		 */
		Map<String, Integer> pTokenCount = precedingTokenFrequenciesPerClass
				.get(inClass);
		if (pTokenCount == null)
			pTokenCount = new HashMap<String, Integer>();
		/* Jetzt für jedes Token hochzählen: */
		count = 0;
		if (pTokenCount.containsKey(precedingToken))
			count = pTokenCount.get(precedingToken);
		pTokenCount.put(precedingToken, count + 1);
		precedingTokenFrequenciesPerClass.put(inClass, pTokenCount);

		/*
		 * Für die Evidenz: Häufigkeit vorangehender POS Tags
		 */
		Map<String, Integer> pPOSCount = precedingPOSFrequenciesPerClass
				.get(inClass);
		if (pPOSCount == null)
			pPOSCount = new HashMap<String, Integer>();
		count = 0;
		if (pPOSCount.containsKey(precedingPOS))
			count = pPOSCount.get(precedingPOS);
		pPOSCount.put(precedingPOS, count + 1);
		precedingTokenFrequenciesPerClass.put(inClass, pPOSCount);

		/*
		 * Für die Evidenz: Häufigkeit folgender Tokens
		 */
		Map<String, Integer> fTokenCount = followingTokenFrequenciesPerClass
				.get(inClass);
		if (fTokenCount == null)
			fTokenCount = new HashMap<String, Integer>();
		count = 0;
		if (fTokenCount.containsKey(followingToken))
			count = fTokenCount.get(followingToken);
		fTokenCount.put(followingToken, count + 1);

		/*
		 * Für die Evidenz: Häufigkeit folgender POS Tags
		 */
		Map<String, Integer> fPOSCount = followingPOSFrequenciesPerClass
				.get(inClass);
		if (fPOSCount == null)
			fPOSCount = new HashMap<String, Integer>();
		count = 0;
		if (fPOSCount.containsKey(followingPOS))
			count = fPOSCount.get(followingPOS);
		fPOSCount.put(followingPOS, count + 1);
		followingPOSFrequenciesPerClass.put(inClass, fPOSCount);

		/*
		 * Für die Evidenz: Häufigkeit des Tokens
		 */
		Map<String, Integer> tokenCount = tokenFrequenciesPerClass.get(inClass);
		if (tokenCount == null)
			tokenCount = new HashMap<String, Integer>();
		count = 0;
		if (tokenCount.containsKey(token))
			count = tokenCount.get(token);
		tokenCount.put(token, count + 1);
		tokenFrequenciesPerClass.put(inClass, tokenCount);

		Map<String, Integer> POSCount = POSFrequenciesPerClass.get(inClass);
		if (POSCount == null)
			POSCount = new HashMap<String, Integer>();
		count = 0;
		if (POSCount.containsKey(POS))
			count = POSCount.get(POS);
		POSCount.put(POS, count + 1);
		POSFrequenciesPerClass.put(inClass, POSCount);

		return this;
	}

	@Override
	public Boolean classify(final PotentialSlotFillingAnchor anchor) {
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
			float evidence = evidence(anchor, c); // TODO change - evidence is
													// sum of probabilities for
													// each feature

			float prior = prior(c); // prior probability of class

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

	private float evidence(final PotentialSlotFillingAnchor anchor,
			final Boolean c) {
		float evidence = 0;

		Map<String, Integer> pTokenFreqsForClass = precedingTokenFrequenciesPerClass
				.get(c);
		Integer pTokenFreq = pTokenFreqsForClass
				.get(anchor.getPrecedingToken());

		evidence += partialEvidence(pTokenFreqsForClass, pTokenFreq);

		Map<String, Integer> fTokenFreqsForClass = followingTokenFrequenciesPerClass
				.get(c);
		Integer fTokenFreq = fTokenFreqsForClass
				.get(anchor.getFollowingToken());

		evidence += partialEvidence(fTokenFreqsForClass, fTokenFreq);

		Map<String, Integer> pPOSFreqsForClass = precedingPOSFrequenciesPerClass
				.get(c);
		Integer pPOSFreq = pPOSFreqsForClass.get(anchor.getPrecedingPOS());

		evidence += partialEvidence(pPOSFreqsForClass, pPOSFreq);

		Map<String, Integer> fPOSFreqsForClass = followingPOSFrequenciesPerClass
				.get(c);
		Integer fPOSFreq = fPOSFreqsForClass.get(anchor.getFollowingPOS());

		evidence += partialEvidence(fPOSFreqsForClass, fPOSFreq);

		Map<String, Integer> tokenFreqsForClass = tokenFrequenciesPerClass
				.get(c);
		Integer tokenFreq = tokenFreqsForClass.get(anchor.getToken());

		evidence += partialEvidence(tokenFreqsForClass, tokenFreq);

		Map<String, Integer> POSFreqForClass = POSFrequenciesPerClass.get(c);
		Integer POSFreq = POSFreqForClass.get(anchor.getPOS());

		evidence += partialEvidence(POSFreqForClass, POSFreq);

		Integer sufFreq = suffixFrequenciesPerClass.get(c);

		evidence += sufFreq;

		Integer punctFreq = punctFollowingFrequenciesPerClass.get(c);

		evidence += punctFreq;

		Integer upperCaseFreq = upperCaseStartFrequenciesPerClass.get(c);

		evidence += upperCaseFreq;

		return evidence;
	}

	private float partialEvidence(Map<String, Integer> fPOSFreqsForClass,
			Integer frequency) {
		float evidence = 0f;
		/*
		 * Wenn ein Term in den Trainingsdokumenten für die Klasse nicht
		 * vorkommt, ist die Evidenz unendlich klein (weil wir damit vergleichen
		 * bei der Suche nach dem Maximum):
		 */
		if (frequency != null) {
			evidence += frequency / (float) sum(fPOSFreqsForClass);
		} else {
			evidence += Float.NEGATIVE_INFINITY;
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
