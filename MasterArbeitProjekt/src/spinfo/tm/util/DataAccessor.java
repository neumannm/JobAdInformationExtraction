package spinfo.tm.util;

import java.util.List;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.data.SlotFiller;

public class DataAccessor {

	private static final String baseParagraphsFile = "data/SingleClassTrainingDataFiltered.csv";

	private static List<Paragraph> allParagraphs;
	private static List<Paragraph> filteredCompetenceParagraphs;
	private static List<Paragraph> parsedCompetenceParagraphs;
	private static List<Sentence> parsedSentencesFromFilteredParagraphs;
	private static List<SlotFiller> potentialFillers;
	private static List<PotentialSlotFillingAnchor> potentialAnchors;

	static {
		
	}

	public static void setAllParagraphs(List<Paragraph> allParagraphs) {
		DataAccessor.allParagraphs = allParagraphs;
	}

	public static void setFilteredCompetenceParagraphs(
			List<Paragraph> filteredCompetenceParagraphs) {
		DataAccessor.filteredCompetenceParagraphs = filteredCompetenceParagraphs;
	}

	public static void setParsedCompetenceParagraphs(
			List<Paragraph> parsedCompetenceParagraphs) {
		DataAccessor.parsedCompetenceParagraphs = parsedCompetenceParagraphs;
	}

	public static void setParsedSentencesFromFilteredParagraphs(
			List<Sentence> parsedSentencesFromFilteredParagraphs) {
		DataAccessor.parsedSentencesFromFilteredParagraphs = parsedSentencesFromFilteredParagraphs;
	}

	public static void setPotentialAnchors(
			List<PotentialSlotFillingAnchor> potentialAnchors) {
		DataAccessor.potentialAnchors = potentialAnchors;
	}

	public static void setPotentialFillers(List<SlotFiller> potentialFillers) {
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
		return baseParagraphsFile;
	}
}
