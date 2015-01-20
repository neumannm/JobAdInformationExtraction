package spinfo.tm.extraction.learning;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.util.DataAccessor;

public class NaiveBayesClassificationTest {

	private TokenClassifier tokenClassifier;

	@Test
	public void test() {

		List<PotentialSlotFillingAnchor> trainingSet;
		trainingSet = DataAccessor.getAnnotatedAnchors();
		tokenClassifier = new TokenClassifier(new NaiveBayes(), trainingSet);

		Map<PotentialSlotFillingAnchor, Boolean> classified = tokenClassifier
				.classify(trainingSet);
		for (PotentialSlotFillingAnchor c : classified.keySet()) {
			System.out.println(classified.get(c) + ":\t" + c);
		}

	}
}