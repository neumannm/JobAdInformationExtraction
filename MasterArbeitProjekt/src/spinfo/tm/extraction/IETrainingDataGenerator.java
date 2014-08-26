package spinfo.tm.extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.data.Template;
import spinfo.tm.preprocessing.OpenNLPTokenizer;
import spinfo.tm.util.UniversalMapper;

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
	private Map<ClassifyUnit, List<SlotFiller>> trainedData;

	public IETrainingDataGenerator(File trainingDataFile,
			Class classToAnnotate, Map<UUID, ClassifyUnit> classifyUnits) {
		setClassToAnnotate(classToAnnotate);
		try {
			setTdFile(trainingDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		trainedData = new TreeMap<ClassifyUnit, List<SlotFiller>>();
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

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		System.out
				.println("\nBitte geben Sie alle Tokens an, die ein/e "
						+ classToAnnotate.getDesc()
						+ " bezeichnen."
						+ "\nFür einzelne Tokens geben Sie die Zahl ein, für Mehr-Wort-Ausdrücke geben Sie den Range an, etwa '5-8'."
						+ "\nTrennen Sie die einzelnen Angaben durch Kommata."
						+ "\nDrücken Sie 'x', wenn kein passendes Token vorhanden ist.\n");
		String answer;

		OpenNLPTokenizer tokenizer = new OpenNLPTokenizer();
		Template template;
		paragraphLoop: for (int i = start; i < paragraphsOfInterest.size(); i++) {
			ClassifyUnit item = paragraphsOfInterest.get(i);

			String itemText = item.getContent().trim();
			System.out.println("ITEM " + (i + 1) + " von "
					+ paragraphsOfInterest.size() + ": " + itemText);

			String[] sentences = tokenizer.splitIntoSentences(itemText);

			List<String> tokens = new ArrayList<>();
			for (String sentence : sentences) {
				tokens.addAll(Arrays.asList(tokenizer
						.tokenizeSentence(sentence)));
			}

			for (int j = 0; j < tokens.size(); j++) {
				System.out.println("TOKEN\t" + (j + 1) + ": " + tokens.get(j));
			}

			answer = in.readLine();

			if (answer.equals("stop"))
				break paragraphLoop;
			if (answer.equals("x")) {
				continue paragraphLoop;
			} else {
				String[] answers = answer.split(",\\s?");
				for (String ans : answers) {
					try {
						Object[] result = processAnswer(ans, tokens, item);

						String token = (String) result[0];
						int position = (int) result[1];

						SlotFiller filler = new SlotFiller(token, position);
						trainedData.get(item).add(filler);

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

				if (end > tokens.size() || start < 1)
					throw new IllegalArgumentException();

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
		/*
		 * Phrase zusammensetzen
		 */
		StringBuffer sb = new StringBuffer();
		for (int i = start; i <= end; i++) {
			sb.append(tokens.get(i)).append(" ");
		}
		/*
		 * Entferne Leerzeichen vor Satzzeichen
		 */
		return sb.toString().replaceAll("\\s(?=[.,:?!\"'*\\-\\(\\)])", "")
				.trim();
	}

	private void writeToFile(Map<ClassifyUnit, List<SlotFiller>> trainedData2)
			throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(tdFile));

		out.println("JobAd ID\tUnit ID\tClass\tToken\tPosition");
		for (ClassifyUnit cu : trainedData2.keySet()) {
			out.print(cu.getParentID() + "\t"); // id of job ad
			out.print(cu.getID() + "\t"); // CU ID
			out.print(classToAnnotate + "\t"); // which class
			for (SlotFiller sf : trainedData2.get(cu)) {
				out.print(sf.getContent() + "\t");
				out.print(sf.getTokenPosition() + "\t");
				out.print("\n\t\t");
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
	public Map<ClassifyUnit, List<SlotFiller>> getTrainingData()
			throws IOException {

		if (trainedData.isEmpty()) {
			trainedData = new TreeMap<ClassifyUnit, List<SlotFiller>>();

			BufferedReader in = new BufferedReader(new FileReader(tdFile));
			String line = in.readLine();// 1st line contains headings

			Class classID = null;
			int jobAdID = 0;
			UUID cuID = null;

			while ((line = in.readLine()) != null) {

				String[] splits = line.split("\t");
				List<SlotFiller> content = null;
				if (splits.length == 4) {
					content = new ArrayList<SlotFiller>();

					if (splits[0].length() > 0 && splits[1].length() > 0
							&& splits[2].length() > 0) {
						// new SlotFiller
						jobAdID = Integer.parseInt(splits[0]);
						cuID = UUID.fromString(splits[1]);
						classID = Class.valueOf(splits[2]);
					}

					String token = splits[2];
					int position = Integer.parseInt(splits[3]);

					content.add(new SlotFiller(token, position));

				} else if (splits.length == 0 && line.trim().isEmpty()) {
					if (classID.equals(classToAnnotate)) {
						trainedData.put(UniversalMapper.getCUforID(cuID),
								content);
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
}