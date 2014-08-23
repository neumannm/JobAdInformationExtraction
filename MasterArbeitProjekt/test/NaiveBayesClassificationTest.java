import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFillingAnchor;
import spinfo.tm.extraction.learning.NaiveBayes;
import spinfo.tm.extraction.learning.TokenClassifier;

public class NaiveBayesClassificationTest {

	private TokenClassifier tokenClassifier;

	@Test
	public void test() {

		Set<SlotFillingAnchor> trainingSet;
		try {
			trainingSet = readFromFile("data/trainingsSet_ML.csv",
					Class.COMPETENCE);
			tokenClassifier = new TokenClassifier(new NaiveBayes(), trainingSet);

			Map<SlotFillingAnchor, Class> classified = tokenClassifier
					.classify(trainingSet);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Set<SlotFillingAnchor> readFromFile(String fileName,
			Class classToAnnotate) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = in.readLine();// 1st line contains headings

		Class classID = null;
		int parentID = 0;
		UUID classifyUnitID = null;

		Set<SlotFillingAnchor> trainedData = new HashSet<>();

		while ((line = in.readLine()) != null) {

			String[] splits = line.split("\t");
			if (splits.length == 5) {
				if (splits[0].length() > 0 && splits[1].length() > 0
						&& splits[2].length() > 0) {
					// new classifyUnit
					parentID = Integer.parseInt(splits[0]);
					classifyUnitID = UUID.fromString(splits[1]);
					classID = Class.valueOf(splits[2]);
				}

				String token = splits[3];
				int position = Integer.parseInt(splits[4]);

				trainedData.add(new SlotFillingAnchor(token, position, classID, classifyUnitID));
				
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