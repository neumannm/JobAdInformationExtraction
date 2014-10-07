package spinfo.tm.extraction.learning;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import spinfo.tm.evaluation.util.CrossvalidationGroupBuilder;
import spinfo.tm.evaluation.util.TrainingTestSets;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.util.DataAccessor;

public class ClassifierWorkflow {

	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("ClassifierWorkflow");

		List<PotentialSlotFillingAnchor> potentialFillers = DataAccessor
				.getPotentialAnchors();
		List<PotentialSlotFillingAnchor> wholeTrainingSet = createTrainingSet(potentialFillers);

		crossValidate(wholeTrainingSet);
	}

	private static void crossValidate(
			List<PotentialSlotFillingAnchor> wholeTrainingSet) {
		TokenClassifier tokenClassifier;

		/* build cross validation groups */
		int numberOfCrossValidGroups = 10;
		CrossvalidationGroupBuilder<PotentialSlotFillingAnchor> cvgb = new CrossvalidationGroupBuilder<>(
				wholeTrainingSet, numberOfCrossValidGroups);
		Iterator<TrainingTestSets<PotentialSlotFillingAnchor>> iterator = cvgb
				.iterator();
		/**/

		while (iterator.hasNext()) {
			TrainingTestSets<PotentialSlotFillingAnchor> testSets = iterator
					.next();
			List<PotentialSlotFillingAnchor> trainingSet = testSets
					.getTrainingSet();

			List<PotentialSlotFillingAnchor> testSet = testSets.getTestSet();

			tokenClassifier = new TokenClassifier(new NaiveBayes(), trainingSet); // includes
																					// training

			Map<PotentialSlotFillingAnchor, Boolean> classified = tokenClassifier
					.classify(testSet);
			int trueCount = 0, falseCount = 0;
			for (PotentialSlotFillingAnchor potAnchor : classified.keySet()) {
				if (classified.get(potAnchor)) {
					trueCount++;
					System.out.println("Is potential anchor: "
							+ potAnchor.getToken());
				} else {
					falseCount++;
				}
			}
			System.out
					.println("\nNumber of potential SlotFillers classified as TRUE: "
							+ trueCount);
			System.out
					.println("Number of potential SlotFillers classified as FALSE: "
							+ falseCount);
			System.out
					.println("Number of all classified potential SlotFillers: "
							+ classified.size());

			evaluate(tokenClassifier, classified, wholeTrainingSet);

			System.out.println("************************");

		}

	}

	private static void evaluate(TokenClassifier tokenClassifier,
			Map<PotentialSlotFillingAnchor, Boolean> classified,
			List<PotentialSlotFillingAnchor> trainingSet) {

		Float accuracy = tokenClassifier.accuracy(classified, trainingSet);
		System.out.println("\nAccuracy: " + accuracy);

		Float precision = tokenClassifier.precision(classified, trainingSet);
		System.out.println("Precision: " + precision);

		Float recall = tokenClassifier.recall(classified, trainingSet);
		System.out.println("Recall: " + recall);

		Float f = tokenClassifier.fMeasure(classified, trainingSet);
		System.out.println("F1: " + f);

	}

	private static List<PotentialSlotFillingAnchor> createTrainingSet(
			List<PotentialSlotFillingAnchor> potentialFillers) {
		List<PotentialSlotFillingAnchor> manuallyAnnotated = DataAccessor
				.getAnnotatedAnchors();

		logger.info("Anzahl manuell ausgezeichneter SlotFilling Anker: "
				+ manuallyAnnotated.size());

		for (PotentialSlotFillingAnchor anchor : potentialFillers) {
			if (manuallyAnnotated.contains(anchor)) {
				// System.out.println("Anker vorhanden (" + anchor + ")");
				anchor.setCompetence(true);
			}
		}
		// for (PotentialSlotFillingAnchor anchor : potentialFillers) {
		// System.out.println(anchor.isCompetence());
		// }
		logger.info("Anzahl potentieller SlotFilling Anker: "
				+ potentialFillers.size());

		return potentialFillers;
	}
}