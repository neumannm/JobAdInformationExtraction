package spinfo.tm.extraction.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.ReaderWriter;

public class DepCompetenceFinderWorkflow {

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String IE_TRAININGDATAFILE = "data/trainingIE_140816.csv";
	private static final String PARSEDPARAGRAPHSFILE = "data/parsedParagraphs.bin";
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
		List<String> verbsOfInterest = null;
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

	private static void evaluate(Map<Paragraph, List<SlotFiller>> allResults) {

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
