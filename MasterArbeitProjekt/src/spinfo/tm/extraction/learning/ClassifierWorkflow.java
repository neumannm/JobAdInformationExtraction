package spinfo.tm.extraction.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.util.CrossvalidationGroupBuilder;
import spinfo.tm.evaluation.util.TrainingTestSets;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.util.DataAccessor;
import spinfo.tm.util.ResultWriter;
import spinfo.tm.util.UniversalMapper;

/**
 * Workflow zur Klassifizierung von Tokens eines Paragraphen als
 * Kompetenz-Anker.
 * 
 * @author neumannm
 * 
 */
public class ClassifierWorkflow {

	private static Logger logger;

	private static Float sumOfAccuracies = 0f;
	private static Float sumOfPrecisions = 0f;
	private static Float sumOfF1 = 0f;
	private static Float sumOfRecalls = 0f;

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
		Map<Paragraph, List<PotentialSlotFillingAnchor>> classifiedAsAnchors = new HashMap<>();

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
					logger.info("Is potential anchor: " + potAnchor.getToken());
					Paragraph par = UniversalMapper.getParagraphforID(potAnchor
							.getParentUUID());
					if (!classifiedAsAnchors.containsKey(par))
						classifiedAsAnchors.put(par,
								new ArrayList<PotentialSlotFillingAnchor>());
					classifiedAsAnchors.get(par).add(potAnchor);
				} else {
					falseCount++;
				}
			}
			logger.info("\nNumber of potential SlotFillers classified as TRUE: "
					+ trueCount);
			logger.info("Number of potential SlotFillers classified as FALSE: "
					+ falseCount);
			logger.info("Number of all classified potential SlotFillers: "
					+ classified.size());

			evaluate(tokenClassifier, classified, wholeTrainingSet);

			logger.info("************************");

		}

		float accuracy = sumOfAccuracies / (float) numberOfCrossValidGroups;
		float precision = sumOfPrecisions / (float) numberOfCrossValidGroups;
		float recall = sumOfRecalls / (float) numberOfCrossValidGroups;
		float f1 = sumOfF1 / (float) numberOfCrossValidGroups;
		
		logger.info("Overall accuracy: " + accuracy);
		logger.info("Overall precision: " + precision);
		logger.info("Overall recall: " + recall);
		logger.info("Overall f1: " + f1);
		
		ResultWriter.writeClassificationExtractionResults(classifiedAsAnchors,
				ClassifierWorkflow.class.getSimpleName());

		ResultWriter.writeEvaluationResults(precision, recall, f1, ClassifierWorkflow.class.getSimpleName());
	}

	private static void evaluate(TokenClassifier tokenClassifier,
			Map<PotentialSlotFillingAnchor, Boolean> classified,
			List<PotentialSlotFillingAnchor> trainingSet) {

		Float accuracy = tokenClassifier.accuracy(classified, trainingSet);
		logger.info("\nAccuracy: " + accuracy);
		sumOfAccuracies += accuracy;

		Float precision = tokenClassifier.precision(classified, trainingSet);
		logger.info("Precision: " + precision);
		sumOfPrecisions += precision;

		Float recall = tokenClassifier.recall(classified, trainingSet);
		logger.info("Recall: " + recall);
		sumOfRecalls += recall;

		Float f = tokenClassifier.fMeasure(classified, trainingSet);
		logger.info("F1: " + f);
		sumOfF1 += f;

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