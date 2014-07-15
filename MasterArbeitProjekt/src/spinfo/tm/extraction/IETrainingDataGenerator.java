package spinfo.tm.extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.preprocessing.FeatureUnitTokenizer;

/**
 * Class to annotate tokens of ClassifyUnits manually, if they are Information
 * Extraction Units or not.
 * 
 * @author neumannm
 * 
 */
public class IETrainingDataGenerator {

	private Class classToAnnotate;
	private File tdFile;
	private Map<ClassifyUnit, Map<String, Integer>> trainedData;

	public IETrainingDataGenerator(File trainingDataFile, Class classToAnnotate) {
		setClassToAnnotate(classToAnnotate);
		try {
			setTdFile(trainingDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		trainedData = new TreeMap<ClassifyUnit, Map<String, Integer>>();
	}

	private void setTdFile(File trainingDataFile) throws IOException {
		this.tdFile = trainingDataFile;
		if (!tdFile.exists())
			tdFile.createNewFile();
	}

	public void annotate(List<ClassifyUnit> paragraphsOfInterest)
			throws IOException {
		int start = 0;
		/*
		 * // needed for resuming (doesn't work the same way as with
		 * classification!!) getTrainingData();
		 * System.out.println("Training Data Size: " + trainedData.size()); int
		 * start = trainedData.size(); System.out.println("Starting with item "
		 * + start);
		 */

		getTrainingData();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		System.out
				.println("\nBitte geben Sie alle Tokens an, die ein/e "
						+ classToAnnotate.getDesc()
						+ " bezeichnen."
						+ "\nFür einzelne Tokens geben Sie die Zahl ein, für Mehr-Wort-Ausdrücke geben Sie den Range an, etwa '5-8'."
						+ "\nTrennen Sie die einzelnen Angaben durch Kommata."
						+ "\nDrücken Sie 'x', wenn kein passendes Token vorhanden ist.\n");
		String answer;

		FeatureUnitTokenizer tokenizer = new FeatureUnitTokenizer();

		paragraphLoop: for (int i = start; i < paragraphsOfInterest.size(); i++) {
			ClassifyUnit item = paragraphsOfInterest.get(i);
			String itemText = item.getContent().trim();
			System.out.println("ITEM " + (i + 1) + " von "
					+ paragraphsOfInterest.size() + ": " + itemText);

			List<String> tokens = tokenizer.tokenize(itemText);

			for (int j = 0; j < tokens.size(); j++) {
				System.out.println("TOKEN\t" + (j + 1) + ": " + tokens.get(j));
			}

			answer = in.readLine();

			if (answer.equals("stop"))
				break paragraphLoop;
			if (answer.equals("x")) {
				continue paragraphLoop;
			} else {
				trainedData.put(item, new HashMap<String, Integer>());

				String[] answers = answer.split(",\\s?");
				for (String ans : answers) {
					try {
						Object[] result = processAnswer(ans, tokens, item);

						String token = (String) result[0];
						int position = (int) result[1];

						/*
						 * geändert damit das funktioniert: ClassifyUnit
						 * implements Comparable
						 */
						trainedData.get(item).put(token, position);

					} catch (IllegalArgumentException e) {
						System.out
								.println("Keine gültige Eingabe! Bitte versuchen Sie es erneut.");
						i--;
					}
				}
			}
		}
		writeToFile(trainedData);
	}

	private Object[] processAnswer(String ans, List<String> tokens,
			ClassifyUnit item) throws IllegalArgumentException {

		Object[] toReturn = new Object[2]; // contains token and its position
											// (workaround for multiple return
											// values)

		// String jobAdContent = item.getContent();

		String token;
		int tokenPosition = -1; // TODO: doesn't work very well - use token
								// position instead (maybe like '3:10' token 10
								// in paragraph 3 - not possible because
								// paragraph has no information about its
								// position in job ad)

		if (!ans.contains("-")) {
			try {
				int tokenPos = Integer.parseInt(ans);
				token = tokens.get(tokenPos - 1);

				tokenPosition = tokenPos - 1;

			} catch (NumberFormatException e) {
				throw new IllegalArgumentException();
			}
		} else {
			String[] tokenPositions = ans.split("-");
			if (tokenPositions.length != 2) {
				throw new IllegalArgumentException();
			}
			try {
				int start = Integer.parseInt(tokenPositions[0]);
				int end = Integer.parseInt(tokenPositions[1]);

				token = accumulateTokens(tokens, start - 1, end - 1);
				tokenPosition = start - 1;

			} catch (NumberFormatException e) {
				throw new IllegalArgumentException();
			}
		}

		toReturn[0] = token;
		toReturn[1] = tokenPosition;

		return toReturn;
	}

	private String accumulateTokens(List<String> tokens, int start, int end) {
		StringBuffer sb = new StringBuffer();
		for (int i = start; i <= end; i++) {
			sb.append(tokens.get(i) + " ");
		}
		return sb.toString().trim();
	}

	private void writeToFile(Map<ClassifyUnit, Map<String, Integer>> data)
			throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(tdFile));

		out.println("Parent ID\tUnit ID\tClass\tToken\tPosition");
		for (ClassifyUnit cu : data.keySet()) {
			out.print(cu.getParentID() + "\t"); // id of job ad
			out.print(cu.getID() + "\t"); // own id
			out.print(classToAnnotate + "\t"); // which class
			Map<String, Integer> unitInfo = data.get(cu);
			for (String token : unitInfo.keySet()) {
				out.print(token);
				out.print("\t");
				out.print(unitInfo.get(token)); // position in tokens
				out.print("\n\t\t\t");
			}
			out.println();
		}
		out.flush();
		out.close();
	}

	public void setClassToAnnotate(Class classToAnnotate) {
		this.classToAnnotate = classToAnnotate;
	}

	/**
	 * Returns trained (manually annotated) data from training data file.
	 * 
	 * @return List of manually annotated IETemplates
	 * @throws IOException
	 */
	public Map<ClassifyUnit, Map<String, Integer>> getTrainingData()
			throws IOException {

		if (trainedData.isEmpty()) {
			trainedData = new TreeMap<ClassifyUnit, Map<String, Integer>>();

			BufferedReader in = new BufferedReader(new FileReader(tdFile));
			String line = in.readLine();// 1st line contains headings

			Map<String, Integer> contents = null;

			Class classID = null;
			int parentID = 0;
			UUID classifyUnitID = null;

			while ((line = in.readLine()) != null) {

				String[] splits = line.split("\t");
				if (splits.length == 5) {

					if (splits[0].length() > 0 && splits[1].length() > 0
							&& splits[2].length() > 0) {
						// new classifyUnit
						parentID = Integer.parseInt(splits[0]);
						classifyUnitID = UUID.fromString(splits[1]);
						classID = Class.valueOf(splits[2]);

						contents = new HashMap<String, Integer>();
					}

					String token = splits[3];
					int position = Integer.parseInt(splits[4]);

					contents.put(token, position);

				} else if (splits.length == 0 && line.trim().isEmpty()) {
					// TODO: how to correctly add data? how to get content of
					// cu?
					if (classID.equals(classToAnnotate)) {
						trainedData.put(new ClassifyUnit("", parentID,
								classifyUnitID), contents);
					}
				} else {
					in.close();
					throw new IOException("File seems to have wrong format");
				}
			}
			in.close();
		}
		return trainedData;
	}

	// Tweak to get ClassifyUnit by ID
	private Map<UUID, ClassifyUnit> mapUUIDsToUnits(List<ClassifyUnit> cus) {
		Map<UUID, ClassifyUnit> map = new HashMap<UUID, ClassifyUnit>();

		for (ClassifyUnit cu : cus) {
			map.put(cu.getID(), cu);
		}
		return map;
	}
}