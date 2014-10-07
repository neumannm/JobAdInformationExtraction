package spinfo.tm;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.DataAccessor;

public class DataAccessorTest {

	@Test
	public void testAllParagraphs() {
		List<Paragraph> allParagraphs = DataAccessor.getAllParagraphs();
		Assert.assertEquals(376, allParagraphs.size());
		for (Paragraph paragraph : allParagraphs) {
			Assert.assertTrue(paragraph.getSentenceData() == null);
		}
	}

	@Test
	public void testFilteredParagraphs() {
		List<Paragraph> filteredCompetenceParagraphs = DataAccessor
				.getFilteredCompetenceParagraphs();
		Assert.assertEquals(110, filteredCompetenceParagraphs.size());
		for (Paragraph paragraph : filteredCompetenceParagraphs) {
			Assert.assertTrue(paragraph.getSentenceData() == null);
		}
	}

	@Test
	public void testSentences() {
		List<Sentence> parsedSentencesFromFilteredParagraphs = DataAccessor
				.getParsedSentencesFromFilteredParagraphs();
		Assert.assertEquals(211, parsedSentencesFromFilteredParagraphs.size());
	}

	@Test
	public void testParsedParagraphs() {
		List<Paragraph> parsedCompetenceParagraphs = DataAccessor
				.getParsedCompetenceParagraphs();
		Assert.assertEquals(110, parsedCompetenceParagraphs.size());
		for (Paragraph paragraph : parsedCompetenceParagraphs) {
			Assert.assertTrue(paragraph.getSentenceData() != null);
		}
	}

	@Test
	public void testAnnotatedFillers() {
		List<SlotFiller> annotatedFillers = DataAccessor.getAnnotatedFillers();
		Assert.assertEquals(483, annotatedFillers.size());
	}

	@Test
	public void testAnnotatedAnchors() {
		List<PotentialSlotFillingAnchor> annotatedAnchors = DataAccessor
				.getAnnotatedAnchors();
		Assert.assertEquals(483, annotatedAnchors.size());
	}

	@Test
	public void testPotentialAnchors() {
		List<PotentialSlotFillingAnchor> potentialAnchors = DataAccessor
				.getPotentialAnchors();
		Assert.assertEquals(4920, potentialAnchors.size());
	}
}
