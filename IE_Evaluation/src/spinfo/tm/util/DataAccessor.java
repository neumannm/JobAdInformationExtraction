package spinfo.tm.util;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.data.SlotFiller;

/**
 * Helper class to access all data that is used by the various components.
 * 
 * @author neumannm
 * 
 */
public class DataAccessor {

	private static final String ALL_PARAGRAPHS_FILE = "data/allParagraphs.bin";
	private static final String FILTERED_PARAGRAPHS_FILE = "data/filteredParagraphs.bin";
	private static final String PARSED_PARAGRAPHS_FILE = "data/parsedParagraphs.bin";
	private static final String PARSED_SENTENCES_FILE = "data/parsedSentences.bin";
	private static final String POTENTIAL_ANCHORS_FILE = "data/potentialSlotFillingAnchors.bin";
	private static final String ANNOTATED_FILLERS_FILE = "data/annotatedSlotFillers.bin";
	private static final String ANNOTATED_ANCHORS_FILE = "data/annotatedSlotFillingAnchors.bin";

	private static List<Paragraph> allParagraphs;
	private static List<Paragraph> filteredCompetenceParagraphs;
	private static List<Paragraph> parsedCompetenceParagraphs;
	private static List<Sentence> parsedSentencesFromFilteredParagraphs;
	private static List<PotentialSlotFillingAnchor> potentialAnchors;
	private static List<SlotFiller> annotatedFillers;
	private static List<PotentialSlotFillingAnchor> annotatedAnchors;

	private static Logger logger;

	static {
		logger = Logger.getLogger("DataAccessor");
		logger.setLevel(Level.ALL);

		File allParagraphsFile = new File(ALL_PARAGRAPHS_FILE);
		if (!allParagraphsFile.exists()) {
			logger.warning("Datei mit allen Paragraphen nicht vorhanden! Bitte zuerst Preparation.main auführen.");
			System.exit(0);
		}

		setAllParagraphs(ReaderWriter
				.readParagraphsFromBinary(allParagraphsFile));

		File filteredParagraphsFile = new File(FILTERED_PARAGRAPHS_FILE);
		if (!filteredParagraphsFile.exists()) {
			logger.warning("Datei mit gefilterten Paragraphs nicht vorhanden!");
			System.exit(0);
		}

		setFilteredCompetenceParagraphs(ReaderWriter
				.readParagraphsFromBinary(filteredParagraphsFile));

		File parsedParagraphsFile = new File(PARSED_PARAGRAPHS_FILE);
		if (!parsedParagraphsFile.exists()) {
			logger.warning("Datei mit geparsten Paragraphs nicht vorhanden!");
			System.exit(0);
		}

		setParsedCompetenceParagraphs(ReaderWriter
				.readParagraphsFromBinary(parsedParagraphsFile));

		File parsedSentencesFile = new File(PARSED_SENTENCES_FILE);
		if (!parsedSentencesFile.exists()) {
			logger.warning("Datei mit geparsten Sentences nicht vorhanden!");
			System.exit(0);
		}

		setParsedSentencesFromFilteredParagraphs(ReaderWriter
				.readSentencesFromBinary(parsedSentencesFile));

		File potentialAnchorsFile = new File(POTENTIAL_ANCHORS_FILE);
		if (!potentialAnchorsFile.exists()) {
			logger.warning("Datei mit potentiellen SlotFiller-Ankern nicht vorhanden!");
			System.exit(0);
		}

		setPotentialAnchors(ReaderWriter
				.readSlotFillingAnchorsFromBinary(potentialAnchorsFile));

		File annotatedFillersFile = new File(ANNOTATED_FILLERS_FILE);
		if (!annotatedFillersFile.exists()) {
			logger.warning("Datei mit manuell annotierten SlotFillern nicht vorhanden!");
			System.exit(0);
		}

		setAnnotatedFillers(ReaderWriter
				.readPotentialFillersFromBinary(annotatedFillersFile));

		File annotatedAnchorsFile = new File(ANNOTATED_ANCHORS_FILE);
		if (!annotatedAnchorsFile.exists()) {
			logger.warning("Datei mit manuell annotierten SlotFillerAnkern nicht vorhanden!");
			System.exit(0);
		}

		setAnnotatedAnchors(ReaderWriter
				.readSlotFillingAnchorsFromBinary(annotatedAnchorsFile));

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

	private static void setAnnotatedFillers(List<SlotFiller> annotatedFillers) {
		DataAccessor.annotatedFillers = annotatedFillers;
	}

	private static void setAnnotatedAnchors(
			List<PotentialSlotFillingAnchor> annotatedAnchors) {
		DataAccessor.annotatedAnchors = annotatedAnchors;
	}

	/**
	 * Get all paragraphs.
	 * 
	 * @return paragraphs
	 */
	public static List<Paragraph> getAllParagraphs() {
		return allParagraphs;
	}

	/**
	 * Get paragraphs that are already filtered according to class 'competence'
	 * 
	 * @return filtered paragraphs
	 */
	public static List<Paragraph> getFilteredCompetenceParagraphs() {
		return filteredCompetenceParagraphs;
	}

	/**
	 * Get paragraphs that have already been processed by the dependency parser.
	 * 
	 * @return parsed paragraphs
	 */
	public static List<Paragraph> getParsedCompetenceParagraphs() {
		return parsedCompetenceParagraphs;
	}

	/**
	 * Get all sentences from the parsed paragraphs.
	 * 
	 * @return parsed sentences
	 */
	public static List<Sentence> getParsedSentencesFromFilteredParagraphs() {
		return parsedSentencesFromFilteredParagraphs;
	}

	/**
	 * Get manually annotated competence anchors.
	 * 
	 * @return list of {@link PotentialSlotFillingAnchor} objects that are
	 *         manually annotated
	 */
	public static List<PotentialSlotFillingAnchor> getAnnotatedAnchors() {
		return annotatedAnchors;
	}

	/**
	 * Get manually annotated slot fillers.
	 * 
	 * @return List of {@link SlotFiller} objects that represent manually
	 *         annotated competence phrases.
	 */
	public static List<SlotFiller> getAnnotatedFillers() {
		return annotatedFillers;
	}

	/**
	 * Get a list of potential competence anchors, i.e. all tokens of all
	 * paragraphs.
	 * 
	 * @return list of potential competence anchors
	 */
	public static List<PotentialSlotFillingAnchor> getPotentialAnchors() {
		return potentialAnchors;
	}
}
