package spinfo.tm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import spinfo.tm.data.Section;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.learning.NaiveBayes;
import spinfo.tm.extraction.learning.TokenClassifier;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.ReaderWriter;
import spinfo.tm.util.SlotFillingAnchorGenerator;

public class Workflow {

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String PARSEDSECTIONSFILE = "data/parsedSections.bin";
	private static final String POTENTIALFILLERSFILE = "data/potentialFillers.bin";
	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("Workflow");

		File parsedSectionsFile = new File(PARSEDSECTIONSFILE);
		if (!parsedSectionsFile.exists()) {
			logger.info("Datei mit bereits geparsten Sections nicht vorhanden. Erstelle...");
			createParsedSectionsFile(parsedSectionsFile);
		}

		List<Section> parsedSections = ReaderWriter
				.readSectionsFromBinary(parsedSectionsFile);

		File potentialFillersFile = new File(POTENTIALFILLERSFILE);
		if (!potentialFillersFile.exists()) {
			logger.info("Datei mit potentiellen Filler-Ankern nicht vorhanden. Erstelle...");
			createPotentialFillersFile(parsedSections, potentialFillersFile);
		}

		List<PotentialSlotFillingAnchor> potentialFillers = ReaderWriter
				.readPotentialAnchorsFromBinary(potentialFillersFile);

		List<PotentialSlotFillingAnchor> trainingSetForNB = createTrainingSet(potentialFillers);
		TokenClassifier tokenClassifier = new TokenClassifier(new NaiveBayes(), trainingSetForNB);
		
	}

	private static List<PotentialSlotFillingAnchor> createTrainingSet(
			List<PotentialSlotFillingAnchor> potentialFillers) {
		try {
			List<PotentialSlotFillingAnchor> manuallyExtracted = readFromFile("data/trainingsSet_ML.csv");
			
			for (PotentialSlotFillingAnchor anchor : potentialFillers) {
				if(manuallyExtracted.contains(anchor)){
//					System.out.println("Anker vorhanden (" + anchor + ")");
					anchor.setCompetence(true);
				}
			}
//			for (PotentialSlotFillingAnchor anchor : potentialFillers) {
//				System.out.println(anchor.isCompetence());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return potentialFillers;
	}

	private static List<PotentialSlotFillingAnchor> readFromFile(String fileName)
			throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = in.readLine();// 1st line contains headings

		Class classID = null;
		int parentID = 0;
		UUID classifyUnitID = null;

		List<PotentialSlotFillingAnchor> trainedData = new ArrayList<>();
		while ((line = in.readLine()) != null) {

			String[] splits = line.split("\t");
			if (splits.length == 5) {
				if (splits[0].length() > 0 && splits[1].length() > 0
						&& splits[2].length() > 0) {
					// new classifyUnit
					parentID = Integer.parseInt(splits[0]);
					classifyUnitID = UUID.fromString(splits[1]);
					classID = Class.valueOf(splits[2]);
				}

				String token = splits[3];
				int position = Integer.parseInt(splits[4]);
				trainedData.add(new PotentialSlotFillingAnchor(token, position,
						true, classifyUnitID));

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
			List<Section> parsedSections, File potentialFillersFile) {
		Set<PotentialSlotFillingAnchor> potentialFillers = SlotFillingAnchorGenerator
				.generateAsSet(parsedSections);

		ReaderWriter.saveToBinaryFile(potentialFillers, potentialFillersFile);
	}

	private static void createParsedSectionsFile(File parsedSectionsFile) {
		List<Section> paragraphs;
		try {
			paragraphs = ReaderWriter.readSectionsFromCSV(TRAININGDATAFILE);
			logger.info("Anzahl Sections insgesamt: " + paragraphs.size());

			Class[] classesToAnnotate = { Class.COMPETENCE,
					Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

			List<Section> filteredParagraphs = ClassFilter.filter(paragraphs,
					classesToAnnotate);

			logger.info("Anzahl Sections gefiltert: "
					+ filteredParagraphs.size());

			ParagraphParser parser = new ParagraphParser();
			filteredParagraphs = parser.parse(filteredParagraphs);

			ReaderWriter.saveToBinaryFile(filteredParagraphs,
					parsedSectionsFile);

		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				System.err
						.println("No File containing pre-classified sections available! Exiting...");
				System.exit(0);
			}
			e.printStackTrace();
		}
	}
}