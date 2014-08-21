import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.ClassFilter;
import spinfo.tm.extraction.IETrainingDataGenerator;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.preprocessing.TrainingDataGenerator;

/**
 * Test Class for Information Extraction Training.
 * 
 * @author neumannm
 * 
 */
/*
 * TODO: map UUIDs to ClassifyUnits; map jobAdIDs to jobAds
 * 
 * pattern matching over whole text of job ad! (classification is carried out
 * before IE)
 */
public class IE_TDTester {
	private List<ClassifyUnit> paragraphs = new ArrayList<ClassifyUnit>();
	private Map<UUID, ClassifyUnit> classifyUnits = new HashMap<UUID, ClassifyUnit>();

	@Before
	public void setUp() throws IOException {
		File trainingDataFile = new File("data/SingleClassTrainingDataFiltered.csv");
		/* Training data generation */
		TrainingDataGenerator tdg = new TrainingDataGenerator(trainingDataFile);

		paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());
		
		for (ClassifyUnit cu : paragraphs) {
			classifyUnits.put(cu.getID(), cu);
		}
		
	}

	@Test
	public void testAnnotate() throws IOException {
		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		List<ClassifyUnit> filteredParagraphs = ClassFilter.filter(paragraphs,
				classesToAnnotate);

		System.out.println("Anzahl ClassifyUnits gefiltert: "
				+ filteredParagraphs.size());

		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				"trainingIE_140816.csv"), Class.COMPETENCE, classifyUnits);

		gen.annotate(filteredParagraphs);

		Map<ClassifyUnit, Map<String, Integer>> templates = gen
				.getTrainingData();

		Assert.assertNotNull(templates);
		Assert.assertFalse(templates.isEmpty());

		for (ClassifyUnit cu : templates.keySet()) {
			System.out.println(cu);
			for (String token : templates.get(cu).keySet()) {
				System.out.println(token);
			}
		}
	}

	@Test
	public void testFilter() throws IOException {
		System.out.println("********************\nPARAGRAPHS:\n");

		for (ClassifyUnit classifyUnit : paragraphs) {
			System.out.println(classifyUnit);
			System.out.println("********************");
		}

		System.out.println("********************");
		System.out.println("********************");
		System.out.println("********************\nFILTERED:\n");

		List<ClassifyUnit> filtered = ClassFilter.filter(paragraphs,
				Class.COMPETENCE, Class.COMPANY_COMPETENCE,
				Class.JOB_COMPETENCE);
		for (ClassifyUnit classifyUnit : filtered) {
			System.out.println(classifyUnit);
			System.out.println("********************");
		}
	}

	// TODO: anpassen in getTrainingData()
	@Test
	public void testGetTrainingData() throws IOException {
		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				"data/trainingIE_140816.csv"), Class.COMPETENCE, classifyUnits);
		Map<ClassifyUnit, Map<String, Integer>> templates;
		try {
			templates = gen.getTrainingData();
			for (ClassifyUnit cu : templates.keySet()) {
				System.out.println(cu);
				System.out.println(templates.get(cu));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}