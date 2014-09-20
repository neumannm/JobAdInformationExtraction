package spinfo.tm.extraction.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.IETrainingDataGenerator;
import spinfo.tm.util.ReaderWriter;

public class DepCompetenceFinderWorkflow {

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String IE_TRAININGDATAFILE = "data/trainingIE_140816.csv";
	private static final String PARSEDPARAGRAPHSFILE = "data/parsedParagraphs.bin";
	private static final String VERBSOFINTERESTFILE = "models/verbsOfInterest.txt";
	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("DepCompetenceFinderWorkflow");

		File parsedParagraphsFile = new File(PARSEDPARAGRAPHSFILE);
		if (!parsedParagraphsFile.exists()) {
			logger.info("Datei mit gefilterten Paragraphs nicht vorhanden. Erstelle...");
			createParsedParagraphsFile(parsedParagraphsFile);
		}

		List<Paragraph> parsedParagraphs = ReaderWriter
				.readParagraphsFromBinary(parsedParagraphsFile);

		// TODO: work with verbsOfInterest-File
		List<String> verbsOfInterest = readVerbsOfInterest(VERBSOFINTERESTFILE);
		DepCompetenceFinder finder = new DepCompetenceFinder(verbsOfInterest);
		Map<Paragraph, List<SlotFiller>> allResults = new HashMap<Paragraph, List<SlotFiller>>();

		int count = 0;
		for (Paragraph par : parsedParagraphs) {
			List<SlotFiller> results = finder.findCompetences(par);
			count += results.size();
			for (SlotFiller slotFiller : results) {
				System.out.println(slotFiller);
			}
			if (!results.isEmpty())
				allResults.put(par, results);
		}

		System.out.println("Anzahl Ergebnisse: " + count);

		evaluate(allResults);
	}

	private static List<String> readVerbsOfInterest(String fileName) {
		List<String> verbs = null;
		
		File file = new File(fileName);
		BufferedReader r = null;
		try {
			verbs = new ArrayList<String>();
			r = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			String line;
			while ((line = r.readLine()) != null) {
				if(line.split("\\s").length != 1) {
					throw new IllegalArgumentException("File has wrong format");
				}
				verbs.add(line.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return verbs;
	}

	private static void evaluate(Map<Paragraph, List<SlotFiller>> allResults) {
		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				IE_TRAININGDATAFILE), Class.COMPETENCE);

		Map<Paragraph, List<SlotFiller>> manuallyLabeled;
		try {
			manuallyLabeled = gen.getTrainingData();
			for (Paragraph par : allResults.keySet()) {
				if (manuallyLabeled.containsKey(par)) {
					List<SlotFiller> result = allResults.get(par);
					List<SlotFiller> gold = manuallyLabeled.get(par);
					compare(result, gold);
					// ...
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void compare(List<SlotFiller> result, List<SlotFiller> gold) {
		for (SlotFiller goldFiller : gold) {
			for (SlotFiller resFiller : result) {
				if (resFiller.getContent().contains(goldFiller.getContent())) {
					// logger.info("Gold contained in result");
					System.out.println(resFiller.getContent() + "\t|\t"
							+ goldFiller.getContent());
				} else if (goldFiller.getContent().contains(
						resFiller.getContent())) {
					// logger.info("Result contained in gold");
					System.out.println(resFiller.getContent() + "\t|\t"
							+ goldFiller.getContent());
				}
			}
		}
	}

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
			filteredParagraphs = parser.parse(filteredParagraphs);

			ReaderWriter.saveToBinaryFile(filteredParagraphs,
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
