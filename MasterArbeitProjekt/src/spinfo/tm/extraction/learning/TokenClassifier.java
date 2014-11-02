package spinfo.tm.extraction.learning;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

/**
 * Classifier for tokens. Classifies a given token as either belonging to the
 * predefined class or not.
 * 
 * @author neumannm
 * 
 */
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
	 * Calculates the accuracy of the classification result.
	 * 
	 * @param classified
	 *            The classification result
	 * @param gold
	 *            The gold standard
	 * @return The ration of correct labels in classified, according to the gold
	 */
	public Float accuracy(
			final Map<PotentialSlotFillingAnchor, Boolean> classified,
			final List<PotentialSlotFillingAnchor> gold) {
		/* Wir zählen die Anzahl der Übereinstimmungen: */
		int same = 0;
		for (PotentialSlotFillingAnchor anchor : gold) {
			if (classified.containsKey(anchor)) {
				Boolean isCompetence = classified.get(anchor);
				if (isCompetence.equals(anchor.isCompetence())) {
					same++;
				}
			} else {
				// System.out.println("...");
			}
		}
		/* Und berechnen daraus den Anteil korrekter Werte: */
		return same / (float) classified.size();
	}

	/**
	 * Calculates the precision of the classification result.
	 * 
	 * @param classified
	 *            The classification result
	 * @param gold
	 *            The gold standard
	 * @return the precision, i.e. (#True Positives) / (#True Positives + #False
	 *         Positives)
	 */
	public Float precision(
			final Map<PotentialSlotFillingAnchor, Boolean> classified,
			final List<PotentialSlotFillingAnchor> gold) {
		int tp = 0, fp = 0;
		for (PotentialSlotFillingAnchor anchor : gold) {
			if (classified.containsKey(anchor)) {
				Boolean isCompetence = classified.get(anchor);
				if (isCompetence && anchor.isCompetence()) {
					tp++;
				} else if (isCompetence && !anchor.isCompetence()) {
					fp++;
				}
			}
		}
//		System.out.println("True positives: " + tp);
//		System.out.println("False positives: " + fp);
		/* Und berechnen daraus die Precision: P = (TP / (TP + FP)) */
		return tp / ((float) tp + fp);
	}

	/**
	 * Calculates the recall of the classification result.
	 * 
	 * @param classified
	 *            The classification result
	 * @param gold
	 *            The gold standard
	 * @return the recall, i.e. (#True Positives) / (#True Positives + #False
	 *         Negatives)
	 */
	public Float recall(
			final Map<PotentialSlotFillingAnchor, Boolean> classified,
			final List<PotentialSlotFillingAnchor> gold) {
		int tp = 0, fn = 0;
		for (PotentialSlotFillingAnchor anchor : gold) {
			if (classified.containsKey(anchor)) {
				Boolean isCompetence = classified.get(anchor);
				if (isCompetence && anchor.isCompetence()) {
					tp++;
				} else if (!isCompetence && anchor.isCompetence()) {
					fn++;
				}
			}
		}
//		System.out.println("True positives: " + tp);
//		System.out.println("False negatives: " + fn);
		/* Und berechnen daraus den Recall: R = (TP / (TP + FN)) */
		return tp / ((float) tp + fn);
	}

	/**
	 * Calculates the f-measure of the classification result.
	 * 
	 * @param classified
	 *            The classification result
	 * @param gold
	 *            The gold standard
	 * @return the f-measure, i.e. combined precision/recall-measure
	 */
	public Float fMeasure(
			final Map<PotentialSlotFillingAnchor, Boolean> classified,
			final List<PotentialSlotFillingAnchor> gold) {
		float p = precision(classified, gold);
		float r = recall(classified, gold);
		return (2 * p * r) / (float) (p + r);
	}
}