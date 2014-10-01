package spinfo.tm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.parsing.ParagraphParser;

public class DataAccessor {

	private static final String BASE_PARAGRAPHS_FILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String IE_TRAINING_DATA_FILE = "data/trainingIE_140816.csv";
	private static final String TRAINING_SET_ML_FILE = "data/trainingsSet_ML";
	private static final String FILTERED_PARAGRAPHS_FILE = "data/filteredParagraphs.bin";
	private static final String PARSED_PARAGRAPHS_FILE = "data/parsedParagraphs.bin";
	private static final String PARSED_SENTENCES_FILE = "data/parsedSentences.bin";
	private static final String POTENTIAL_FILLERS_FILE = "data/potentialFillers.bin";
	private static final String POTENTIAL_ANCHORS_FILE = "data/potentialFillers.bin";

	private static List<Paragraph> allParagraphs;
	private static List<Paragraph> filteredCompetenceParagraphs;
	private static List<Paragraph> parsedCompetenceParagraphs;
	private static List<Sentence> parsedSentencesFromFilteredParagraphs;
	private static List<SlotFiller> potentialFillers;
	private static List<PotentialSlotFillingAnchor> potentialAnchors;

	private static Logger logger;

	static {
		logger = Logger.getLogger("DataAccessor");

		File filteredParagraphsFile = new File(FILTERED_PARAGRAPHS_FILE);
		if (!filteredParagraphsFile.exists()) {
			logger.info("Datei mit gefilterten Paragraphs nicht vorhanden. Erstelle...");
			createParagraphsFile(filteredParagraphsFile);
		} else {
			setFilteredCompetenceParagraphs(ReaderWriter
					.readParagraphsFromBinary(filteredParagraphsFile));
		}

		File parsedParagraphsFile = new File(PARSED_PARAGRAPHS_FILE);
		if (!parsedParagraphsFile.exists()) {
			logger.info("Datei mit geparsten Paragraphs nicht vorhanden. Erstelle...");
			createParsedParagraphsFile(parsedParagraphsFile);
		} else {
			setParsedCompetenceParagraphs(ReaderWriter
					.readParagraphsFromBinary(parsedParagraphsFile));
		}

		File parsedSentencesFile = new File(PARSED_SENTENCES_FILE);
		if (!parsedSentencesFile.exists()) {
			logger.info("Datei mit geparsten Sentences nicht vorhanden. Erstelle...");
			createParsedSentencesFile(parsedSentencesFile);
		} else {
			setParsedSentencesFromFilteredParagraphs(ReaderWriter
					.readSentencesFromBinary(parsedSentencesFile));
		}

		setPotentialAnchors(potentialAnchors); // ...

		setPotentialFillers(potentialFillers); // ...

	}

	private static void createParagraphsFile(File filteredParagraphsFile) {
		List<Paragraph> paragraphs;
		try {
			paragraphs = ReaderWriter
					.readParagraphsFromCSV(BASE_PARAGRAPHS_FILE);
			setAllParagraphs(paragraphs);

			logger.info("Anzahl Paragraphs insgesamt: " + paragraphs.size());

			Class[] classesToAnnotate = { Class.COMPETENCE,
					Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

			List<Paragraph> filteredParagraphs = ClassFilter.filter(paragraphs,
					classesToAnnotate);

			logger.info("Anzahl Paragraphs gefiltert: "
					+ filteredParagraphs.size());

			ReaderWriter.saveToBinaryFile(filteredParagraphs,
					filteredParagraphsFile);

			setFilteredCompetenceParagraphs(filteredParagraphs);

		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				System.err
						.println("No File containing pre-classified paragraphs available! Exiting...");
				System.exit(0);
			}
			e.printStackTrace();
		}
	}

	private static void createParsedSentencesFile(File parsedSentencesFile) {
		List<Sentence> savedSentences = new ArrayList<>();

		for (Paragraph paragraph : filteredCompetenceParagraphs) {
			Map<Integer, Sentence> sentenceData = paragraph.getSentenceData();
			for (Integer sentence : sentenceData.keySet()) {
				Sentence parsed = sentenceData.get(sentence);
				System.out.println(parsed);
			}
			savedSentences.addAll(paragraph.getSentenceData().values());
		}
		ReaderWriter.saveToBinaryFile(savedSentences, parsedSentencesFile);
		setParsedSentencesFromFilteredParagraphs(savedSentences);
	}

	private static void createParsedParagraphsFile(File parsedParagraphsFile) {
		ParagraphParser parser = new ParagraphParser();
		List<Paragraph> parsedParagraphs = parser
				.parse(getFilteredCompetenceParagraphs());

		ReaderWriter.saveToBinaryFile(parsedParagraphs, parsedParagraphsFile);

		setParsedCompetenceParagraphs(parsedParagraphs);
	}

	private static void setAllParagraphs(List<Paragraph> allParagraphs) {
		DataAccessor.allParagraphs = allParagraphs;
	}

	private static void setFilteredCompetenceParagraphs(
			List<Paragraph> filteredCompetenceParagraphs) {
		DataAccessor.filteredCompetenceParagraphs = filteredCompetenceParagraphs;
	}

	private static void setParsedCompetenceParagraphs(
			List<Paragraph> parsedCompetenceParagraphs) {
		DataAccessor.parsedCompetenceParagraphs = parsedCompetenceParagraphs;
	}

	private static void setParsedSentencesFromFilteredParagraphs(
			List<Sentence> parsedSentencesFromFilteredParagraphs) {
		DataAccessor.parsedSentencesFromFilteredParagraphs = parsedSentencesFromFilteredParagraphs;
	}

	private static void setPotentialAnchors(
			List<PotentialSlotFillingAnchor> potentialAnchors) {
		DataAccessor.potentialAnchors = potentialAnchors;
	}

	private static void setPotentialFillers(List<SlotFiller> potentialFillers) {
		DataAccessor.potentialFillers = potentialFillers;
	}

	public static List<Paragraph> getAllParagraphs() {
		return allParagraphs;
	}

	public static List<Paragraph> getFilteredCompetenceParagraphs() {
		return filteredCompetenceParagraphs;
	}

	public static List<Paragraph> getParsedCompetenceParagraphs() {
		return parsedCompetenceParagraphs;
	}

	public static List<Sentence> getParsedSentencesFromFilteredParagraphs() {
		return parsedSentencesFromFilteredParagraphs;
	}

	public static List<PotentialSlotFillingAnchor> getPotentialAnchors() {
		return potentialAnchors;
	}

	public static List<SlotFiller> getPotentialFillers() {
		return potentialFillers;
	}

	public static String getBaseparagraphsfile() {
		return BASE_PARAGRAPHS_FILE;
	}
}
