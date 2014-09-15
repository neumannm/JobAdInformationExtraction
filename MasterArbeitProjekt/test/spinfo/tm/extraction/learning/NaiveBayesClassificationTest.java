package spinfo.tm.extraction.learning;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

public class NaiveBayesClassificationTest {

	private TokenClassifier tokenClassifier;

	@Test
	public void test() {

		Set<PotentialSlotFillingAnchor> trainingSet;
		try {
			trainingSet = readFromFile("data/trainingsSet_ML.csv",
					Class.COMPETENCE);
			tokenClassifier = new TokenClassifier(new NaiveBayes(), trainingSet);

			Map<PotentialSlotFillingAnchor, Boolean> classified = tokenClassifier
					.classify(trainingSet);
			for (PotentialSlotFillingAnchor c : classified.keySet()) {
				System.out.println(classified.get(c) + ":\t" + c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Set<PotentialSlotFillingAnchor> readFromFile(String fileName,
			Class classToAnnotate) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = in.readLine();// 1st line contains headings

		Class classID = null;
		int parentID = 0;
		UUID paragraphID = null;

		Set<PotentialSlotFillingAnchor> trainedData = new HashSet<>();

		while ((line = in.readLine()) != null) {

			String[] splits = line.split("\t");
			if (splits.length == 5) {
				if (splits[0].length() > 0 && splits[1].length() > 0
						&& splits[2].length() > 0) {
					// new paragraph
					parentID = Integer.parseInt(splits[0]);
					paragraphID = UUID.fromString(splits[1]);
					classID = Class.valueOf(splits[2]);
				}

				String token = splits[3];
				int position = Integer.parseInt(splits[4]);

				trainedData.add(new PotentialSlotFillingAnchor(token, position, true, paragraphID));
				
			} else if (splits.length == 0 && line.trim().isEmpty()) {
				//new line in file
			} else {
				in.close();
				throw new IOException("File seems to have wrong format");
			}
		}
		in.close();
		return trainedData;
	}
}