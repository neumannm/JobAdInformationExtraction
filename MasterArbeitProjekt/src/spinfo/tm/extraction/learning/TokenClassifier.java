package spinfo.tm.extraction.learning;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFillingAnchor;

public class TokenClassifier {

	private ClassifierStrategy classifier;

	/**
	 * @param classifier
	 *            The classifier strategy to use for text classification
	 * @param trainingSet
	 *            The training set for this classifier
	 */
	public TokenClassifier(final ClassifierStrategy classifier,
			final Set<SlotFillingAnchor> trainingSet) {
		this.classifier = classifier;
		train(trainingSet);
	}

	private void train(Set<SlotFillingAnchor> trainingTokens) {
		/* Wir trainieren mit jedem Dokument: */
		for (SlotFillingAnchor filler : trainingTokens) {
			/* Delegieren das eigentliche Training an unsere Strategie: */
			this.classifier = classifier.train(filler, filler.getC());
		}
	}

	/**
	 * @param documents
	 *            The documents to classify
	 * @return A mapping of documents to their class labels
	 */
	public Map<SlotFillingAnchor, Class> classify(
			final Set<SlotFillingAnchor> documents) {
		Map<SlotFillingAnchor, Class> resultClasses = new HashMap<>();
		for (SlotFillingAnchor document : documents) {
			/* Wie beim Training delegieren wir an die Strategie: */
			Class classLabel = classifier.classify(document);
			/*
			 * Und speichern wie im Seminar vorgeschlagen wurde, die Ergebnisse
			 * in einer Map, um die fehleranfälligen korrespondierenden Listen
			 * zu vermeiden:
			 */
			resultClasses.put(document, classLabel);
		}
		return resultClasses;
	}

	// /**
	// * @param resultClasses The classification result
	// * @param gold The gold standard
	// * @return The ration of correct labels in classified, according to the
	// gold
	// */
	// public Float evaluate(final Map<Document, String> resultClasses,
	// final ArrayList<Document> gold) {
	// /* Wir zählen die Anzahl der Übereinstimmungen: */
	// int same = 0;
	// for (Document document : gold) {
	// String classLabel = resultClasses.get(document);
	// if (classLabel.equalsIgnoreCase(document.getTopic())) {
	// same++;
	// }
	// }
	// /* Und berechnen daraus den Anteil korrekter Werte: */
	// return same / (float) gold.size();
	// /*
	// * Eigentlich mit Annotationen evaluieren (als generisches
	// * Austauschformat), aber für die Übersichtlichkeit hier so.
	// */
	// }

}
