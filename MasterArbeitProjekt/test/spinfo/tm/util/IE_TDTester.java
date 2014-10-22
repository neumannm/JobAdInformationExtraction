package spinfo.tm.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;

/**
 * Test Class for Information Extraction Training.
 * 
 * @author neumannm
 * 
 */
/*
 * TODO: map UUIDs to paragraphs; map jobAdIDs to jobAds
 * 
 * pattern matching over whole text of job ad! (classification is carried out
 * before IE)
 */
public class IE_TDTester {
	private List<Paragraph> paragraphs = new ArrayList<Paragraph>();

	@Before
	public void setUp() throws IOException {
		paragraphs = DataAccessor.getAllParagraphs();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());
	}

	@Test
	public void testAnnotate() throws IOException {
		List<Paragraph> filteredParagraphs = DataAccessor
				.getFilteredCompetenceParagraphs();
		System.out.println("Anzahl ClassifyUnits gefiltert: "
				+ filteredParagraphs.size());

		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				"trainingIE_140816.csv"), Class.COMPETENCE);

		gen.annotate(filteredParagraphs);

		Map<Paragraph, Set<SlotFiller>> templates = gen.getTrainingData();

		Assert.assertNotNull(templates);
		Assert.assertFalse(templates.isEmpty());

		for (Paragraph cu : templates.keySet()) {
			System.out.println(cu);
			for (SlotFiller content : templates.get(cu)) {
				System.out.println(content);
			}
		}
	}

	@Test
	public void testFilter() throws IOException {
		System.out.println("********************\nPARAGRAPHS:\n");

		for (Paragraph classifyUnit : paragraphs) {
			System.out.println(classifyUnit);
			System.out.println("********************");
		}

		System.out.println("********************");
		System.out.println("********************");
		System.out.println("********************\nFILTERED:\n");

		List<Paragraph> filtered = ClassFilter.filter(paragraphs,
				Class.COMPETENCE, Class.COMPANY_COMPETENCE,
				Class.JOB_COMPETENCE);
		for (Paragraph paragraph : filtered) {
			System.out.println(paragraph.getID());
			System.out.println(paragraph.getContent());
			System.out.println("********************");
		}
	}

	// TODO: anpassen in getTrainingData()
	@Test
	public void testGetTrainingData() throws IOException {
		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				"data/trainingIE_140816.csv"), Class.COMPETENCE);
		Map<Paragraph, Set<SlotFiller>> templates;
		try {
			templates = gen.getTrainingData();
			int pcount = 0;
			int fcount = 0;
			for (Paragraph paragraph : templates.keySet()) {
				pcount++;
				System.out.println(paragraph);
				System.out.println(templates.get(paragraph));
				fcount += templates.get(paragraph).size();
			}

			System.out.println(pcount);
			System.out.println(fcount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}