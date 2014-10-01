package spinfo.tm.extraction.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.IE_Evaluator;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.ReaderWriter;

public class DepCompetenceFinderWorkflow {

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String PARSEDPARAGRAPHSFILE = "data/parsedParagraphs.bin";
	private static final String VERBSOFINTERESTFILE = "models/verbsOfInterest.txt";
	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("DepCompetenceFinderWorkflow");

		File parsedParagraphsFile = new File(PARSEDPARAGRAPHSFILE);
		if (!parsedParagraphsFile.exists()) {
			logger.info("Datei mit geparsten Paragraphs nicht vorhanden. Erstelle...");
			createParsedParagraphsFile(parsedParagraphsFile);
		}

		List<Paragraph> parsedParagraphs = ReaderWriter
				.readParagraphsFromBinary(parsedParagraphsFile);

		Map<String, String> verbsOfInterest = readVerbsOfInterest(VERBSOFINTERESTFILE);
		DepCompetenceFinder finder = new DepCompetenceFinder(verbsOfInterest);
		Map<Paragraph, Set<SlotFiller>> allResults = new HashMap<Paragraph, Set<SlotFiller>>();

		int count = 0;
		for (Paragraph par : parsedParagraphs) {
			Set<SlotFiller> results = finder.findCompetences(par);
			count += results.size();
			for (SlotFiller slotFiller : results) {
				System.out.println(slotFiller);
			}
			if (!results.isEmpty())
				allResults.put(par, results);
		}

		System.out.println("Anzahl Ergebnisse: " + count);

		IE_Evaluator.evaluate(allResults);
	}

	private static Map<String, String> readVerbsOfInterest(String file) {
		File inputFile = new File(file);
		if (!inputFile.getName().endsWith(".txt")) {
			System.err.println("Wrong file format");
			return null;
		}

		Map<String, String> toReturn = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inputFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(":");
				if (split.length == 1) {
					toReturn.put(split[0], null);
					continue;
				}
				if (split.length == 2)
					toReturn.put(split[0], split[1]);
				else {
					System.err.println("Wrong format");
					return null;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	// private static List<String> readVerbsOfInterest(String fileName) {
	// List<String> verbs = null;
	//
	// File file = new File(fileName);
	// BufferedReader r = null;
	// try {
	// verbs = new ArrayList<String>();
	// r = new BufferedReader(new InputStreamReader(new FileInputStream(
	// file)));
	// String line;
	// while ((line = r.readLine()) != null) {
	// if(line.split("\\s").length != 1) {
	// throw new IllegalArgumentException("File has wrong format");
	// }
	// verbs.add(line.trim());
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// r.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return verbs;
	// }

	private static void createParsedParagraphsFile(File parsedParagraphsFile) {
		List<Paragraph> paragraphs;
		try {
			paragraphs = ReaderWriter.readParagraphsFromCSV(TRAININGDATAFILE);
			logger.info("Anzahl Paragraphs insgesamt: " + paragraphs.size());

			Class[] classesToAnnotate = { Class.COMPETENCE,
					Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

			List<Paragraph> filteredParagraphs = ClassFilter.filter(paragraphs,
					classesToAnnotate);

			logger.info("Anzahl Paragraphs gefiltert: "
					+ filteredParagraphs.size());

			ParagraphParser parser = new ParagraphParser();
			List<Paragraph> parsedParagraphs = parser.parse(filteredParagraphs);

			ReaderWriter.saveToBinaryFile(parsedParagraphs,
					parsedParagraphsFile);

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
