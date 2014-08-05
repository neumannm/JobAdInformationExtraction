import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.IETrainingDataGenerator;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.JobAd;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.pattern.PatternMatcher;
import spinfo.tm.preprocessing.TrainingDataGenerator;
import spinfo.tm.util.UniversalMapper;

public class PatternMatchingTest {
	private List<ClassifyUnit> paragraphs = new ArrayList<ClassifyUnit>();
	private Map<UUID, ClassifyUnit> classifyUnits = new HashMap<UUID, ClassifyUnit>();
	private Map<ClassifyUnit, JobAd> map;

	@Before
	public void setUp() throws IOException {
		File trainingDataFile = new File(
				"data/SingleClassTrainingDataFiltered.csv");
		/* Training data generation */
		TrainingDataGenerator tdg = new TrainingDataGenerator(trainingDataFile);

		paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());

		for (ClassifyUnit cu : paragraphs) {
			classifyUnits.put(cu.getID(), cu);
		}
		
		map = UniversalMapper.map(paragraphs);
	}

	@Test
	public void testPatternMatching() throws IOException {
		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				"data/trainingIE_140623.csv"), Class.COMPETENCE, classifyUnits);

		Map<ClassifyUnit, Map<String, Integer>> trainingData = gen
				.getTrainingData();
		PatternMatcher pm = new PatternMatcher();

		int count = 0;
		for (ClassifyUnit cu : trainingData.keySet()) {
//			System.out.println(cu);
			
			JobAd parent = map.get(cu);
			List<SlotFiller> sf = pm.getContentOfInterest(cu, parent.getTemplate()); 
			count += sf.size();
			for (SlotFiller slotFiller : sf) {
				System.out.println(slotFiller);
			}
			
			System.out.println("\n***********************\n");
		}
		
		System.out.println("Anzahl Ergebnisse: " + count);
	}
}