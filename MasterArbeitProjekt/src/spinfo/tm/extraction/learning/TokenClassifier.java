package spinfo.tm.extraction.learning;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

public class TokenClassifier {

	private ClassifierStrategy classifier;

	/**
	 * @param classifier
	 *            The classifier strategy to use for text classification
	 * @param trainingSet
	 *            The training set for this classifier
	 */
	public TokenClassifier(final ClassifierStrategy classifier,
			final Collection<PotentialSlotFillingAnchor> trainingSet) {
		this.classifier = classifier;
		train(trainingSet);
	}

	private void train(Collection<PotentialSlotFillingAnchor> trainingTokens) {
		/* Wir trainieren mit jedem Token: */
		for (PotentialSlotFillingAnchor filler : trainingTokens) {
			/* Delegieren das eigentliche Training an unsere Strategie: */
			this.classifier = classifier.train(filler);
		}
	}

	/**
	 * @param toClassify
	 *            The objects to classify
	 * @return A mapping of documents to their class labels
	 */
	public Map<PotentialSlotFillingAnchor, Boolean> classify(
			final Collection<PotentialSlotFillingAnchor> toClassify) {
		Map<PotentialSlotFillingAnchor, Boolean> resultClasses = new HashMap<>();
		for (PotentialSlotFillingAnchor document : toClassify) {
			/* Wie beim Training delegieren wir an die Strategie: */
			Boolean classLabel = classifier.classify(document);
			/*
			 * Und speichern die Ergebnisse in einer Map, um die
			 * fehleranfälligen korrespondierenden Listen zu vermeiden:
			 */
			resultClasses.put(document, classLabel);
		}
		return resultClasses;
	}

	/**
	 * @param resultClasses
	 *            The classification result
	 * @param gold
	 *            The gold standard
	 * @return The ration of correct labels in classified, according to the gold
	 */
	//TODO: Precision oder Recall? oder Accuracy? (wahrscheinlich letzteres)
	public Float evaluate(
			final Map<PotentialSlotFillingAnchor, Boolean> resultClasses,
			final List<PotentialSlotFillingAnchor> gold) {
		/* Wir zählen die Anzahl der Übereinstimmungen: */
		int same = 0;
		for (PotentialSlotFillingAnchor anchor : gold) {
			Boolean isCompetence = resultClasses.get(anchor);
			if (isCompetence.equals(anchor.isCompetence())) {
				same++;
			}
		}
		/* Und berechnen daraus den Anteil korrekter Werte: */
		return same / (float) gold.size();
	}
}