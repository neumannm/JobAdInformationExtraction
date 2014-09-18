package spinfo.tm.extraction.pattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class PatternMatcherWorkflow {

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String IE_TRAININGDATAFILE = "data/trainingIE_140816.csv";
	private static final String FILTEREDPARAGRAPHSFILE = "data/filteredParagraphs.bin";
	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("PatternMatcherWorkflow");

		File allParagraphsFile = new File(FILTEREDPARAGRAPHSFILE);
		if (!allParagraphsFile.exists()) {
			logger.info("Datei mit gefilterten Paragraphs nicht vorhanden. Erstelle...");
			createParagraphsFile(allParagraphsFile);
		}

		List<Paragraph> filteredParagraphs = ReaderWriter
				.readParagraphsFromBinary(allParagraphsFile);

		PatternMatcher pm = new PatternMatcher();
		Map<Paragraph, List<SlotFiller>> allResults = new HashMap<Paragraph, List<SlotFiller>>();

		int count = 0;
		for (Paragraph p : filteredParagraphs) {
			List<SlotFiller> result = pm.getContentOfInterest(p,
					new ArrayList<SlotFiller>());
			count += result.size();
			for (SlotFiller slotFiller : result) {
				System.out.println(slotFiller);
			}
			if (!result.isEmpty())
				allResults.put(p, result);
			System.out.println("\n***********************\n");
		}

		System.out.println("Anzahl Ergebnisse: " + count);

		evaluate(allResults);
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
//					logger.info("Gold contained in result");
					System.out.println(resFiller.getContent() + "\t|\t" + goldFiller.getContent());
				} else if (goldFiller.getContent().contains(
						resFiller.getContent())) {
//					logger.info("Result contained in gold");
					System.out.println(resFiller.getContent() + "\t|\t" + goldFiller.getContent());
				}
			}
		}
	}

	private static void createParagraphsFile(File filteredParagraphsFile) {
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

			ReaderWriter
					.saveToBinaryFile(filteredParagraphs, filteredParagraphsFile);

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
