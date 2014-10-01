package spinfo.tm.extraction.pattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.IE_Evaluator;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.ReaderWriter;

public class PatternMatcherWorkflow {

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
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
		Map<Paragraph, Set<SlotFiller>> allResults = new HashMap<>();

		int count = 0;
		for (Paragraph p : filteredParagraphs) {
			Map<SlotFiller, Pattern> result = pm.getContentOfInterest(p); // TODO:
																			// for
																			// regex
																			// evaluation
			Set<SlotFiller> resultFiller = result.keySet();
			count += resultFiller.size();
			for (SlotFiller slotFiller : resultFiller) {
				System.out.println(slotFiller);
			}
			if (!resultFiller.isEmpty())
				allResults.put(p, resultFiller);
			System.out.println("\n***********************\n");
		}

		System.out.println("Anzahl Ergebnisse: " + count);

		IE_Evaluator.evaluate(allResults);

		// TODO: regex evaluate??
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

			ReaderWriter.saveToBinaryFile(filteredParagraphs,
					filteredParagraphsFile);

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
