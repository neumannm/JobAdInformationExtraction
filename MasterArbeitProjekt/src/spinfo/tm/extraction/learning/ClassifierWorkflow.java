package spinfo.tm.extraction.learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.util.CrossvalidationGroupBuilder;
import spinfo.tm.evaluation.util.TrainingTestSets;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.ReaderWriter;
import spinfo.tm.util.SlotFillingAnchorGenerator;

public class ClassifierWorkflow {

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String PARSEDPARAGRAPHSSFILE = "data/parsedParagraphs.bin";
	private static final String POTENTIALFILLERSFILE = "data/potentialFillers.bin";
	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("Workflow");

		File parsedParagraphsFile = new File(PARSEDPARAGRAPHSSFILE);
		if (!parsedParagraphsFile.exists()) {
			logger.info("Datei mit bereits geparsten Paragraphs nicht vorhanden. Erstelle...");
			createParsedSectionsFile(parsedParagraphsFile);
		}

		List<Paragraph> parsedSections = ReaderWriter
				.readSectionsFromBinary(parsedParagraphsFile);

		File potentialFillersFile = new File(POTENTIALFILLERSFILE);
		if (!potentialFillersFile.exists()) {
			logger.info("Datei mit potentiellen Filler-Ankern nicht vorhanden. Erstelle...");
			createPotentialFillersFile(parsedSections, potentialFillersFile);
		}

		List<PotentialSlotFillingAnchor> potentialFillers = ReaderWriter
				.readPotentialAnchorsFromBinary(potentialFillersFile);

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
		System.out.println("\nEvaluation result (accuracy): " + accuracy);

		Float precision = tokenClassifier.precision(classified, trainingSet);
		System.out.println("\nEvaluation result (precision): " + precision);

		Float recall = tokenClassifier.recall(classified, trainingSet);
		System.out.println("\nEvaluation result (recall): " + recall);

		Float f = tokenClassifier.fMeasure(classified, trainingSet);
		System.out.println("\nEvaluation result (f1-measure): " + f);

	}

	private static List<PotentialSlotFillingAnchor> createTrainingSet(
			List<PotentialSlotFillingAnchor> potentialFillers) {
		try {
			List<PotentialSlotFillingAnchor> manuallyExtracted = readFromFile("data/trainingsSet_ML.csv");
			logger.info("Anzahl manuell ausgezeichneter SlotFilling Anker: "
					+ manuallyExtracted.size());

			for (PotentialSlotFillingAnchor anchor : potentialFillers) {
				if (manuallyExtracted.contains(anchor)) {
					// System.out.println("Anker vorhanden (" + anchor + ")");
					anchor.setCompetence(true);
				}
			}
			// for (PotentialSlotFillingAnchor anchor : potentialFillers) {
			// System.out.println(anchor.isCompetence());
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Anzahl potentieller SlotFilling Anker: "
				+ potentialFillers.size());

		return potentialFillers;
	}

	private static List<PotentialSlotFillingAnchor> readFromFile(String fileName)
			throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = in.readLine();// 1st line contains headings

		Class classID = null;
		int parentID = 0;
		UUID paragraphID = null;

		List<PotentialSlotFillingAnchor> trainedData = new ArrayList<>();
		while ((line = in.readLine()) != null) {

			String[] splits = line.split("\t");
			if (splits.length == 5) {
				if (splits[0].length() > 0 && splits[1].length() > 0
						&& splits[2].length() > 0) {
					// new paragraph
					parentID = Integer.parseInt(splits[0]);
					paragraphID = UUID.fromString(splits[1]);
					classID = Class.valueOf(splits[2]);
				}

				String token = splits[3];
				int position = Integer.parseInt(splits[4]);
				trainedData.add(new PotentialSlotFillingAnchor(token, position,
						true, paragraphID));

			} else if (splits.length == 0 && line.trim().isEmpty()) {
				// new line in file
			} else {
				in.close();
				throw new IOException("File seems to have wrong format");
			}
		}
		in.close();
		return trainedData;
	}

	private static void createPotentialFillersFile(
			List<Paragraph> parsedSections, File potentialFillersFile) {
		Set<PotentialSlotFillingAnchor> potentialFillers = SlotFillingAnchorGenerator
				.generateAsSet(parsedSections);

		ReaderWriter.saveToBinaryFile(potentialFillers, potentialFillersFile);
	}

	private static void createParsedSectionsFile(File parsedSectionsFile) {
		List<Paragraph> paragraphs;
		try {
			paragraphs = ReaderWriter.readSectionsFromCSV(TRAININGDATAFILE);
			logger.info("Anzahl Paragraphs insgesamt: " + paragraphs.size());

			Class[] classesToAnnotate = { Class.COMPETENCE,
					Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

			List<Paragraph> filteredParagraphs = ClassFilter.filter(paragraphs,
					classesToAnnotate);

			logger.info("Anzahl Paragraphs gefiltert: "
					+ filteredParagraphs.size());

			ParagraphParser parser = new ParagraphParser();
			filteredParagraphs = parser.parse(filteredParagraphs);

			ReaderWriter.saveToBinaryFile(filteredParagraphs,
					parsedSectionsFile);

		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				System.err
						.println("No File containing pre-classified paragraphs available! Exiting...");
				System.exit(0);
			}
			e.printStackTrace();
		}
	}
}