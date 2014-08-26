import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.ClassFilter;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.preprocessing.TrainingDataGenerator;
import spinfo.tm.util.UniversalMapper;

public class Preparation {

	private static List<ClassifyUnit> filteredParagraphs;
	private static List<Sentence> savedSentences = new ArrayList<>();

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String ALLCLASSIFYUNITSFILE = "data/allClassifyUnits.bin";
	private static final String OUTPUTFILE = "parsedSentences.bin";

	/*
	 * Nur ausführen, wenn Datei nicht vorhanden! (parsen dauert)
	 */
	public static void main(String[] args) {

		try {
			readAndFilterParagraphs();
			parseFilteredParagraphs();

			for (ClassifyUnit paragraph : filteredParagraphs) {
				Map<Integer, Sentence> sentenceData = paragraph
						.getSentenceData();
				// for (Integer sentence : sentenceData.keySet()) {
				// Sentence parsed = sentenceData.get(sentence);
				// System.out.println(parsed);
				// }
				savedSentences.addAll(paragraph.getSentenceData().values());
			}
			saveToFile(savedSentences, OUTPUTFILE);

			System.out.println("Number of sentences: " + savedSentences.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readAndFilterParagraphs() throws IOException {
		File trainingDataFile = new File(TRAININGDATAFILE);
		TrainingDataGenerator tdg = new TrainingDataGenerator(trainingDataFile);

		List<ClassifyUnit> paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());
		saveToFile(paragraphs, ALLCLASSIFYUNITSFILE);
		
		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		filteredParagraphs = ClassFilter.filter(paragraphs, classesToAnnotate);

		System.out.println("Anzahl ClassifyUnits gefiltert: "
				+ filteredParagraphs.size());
	}

	private static void parseFilteredParagraphs() {
		ParagraphParser parser = new ParagraphParser();
		filteredParagraphs = parser.parse(filteredParagraphs);
	}

	private static void saveToFile(Collection<?> data, String fileName) {
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(new FileOutputStream(fileName));
			for (Object o : data) {
				os.writeObject(o);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Before
	public void setUp() throws IOException {
		readAndFilterParagraphs();
	}

	@Test
	public void testReadFromFile() {
		List<Sentence> readSentences = readFromFile(OUTPUTFILE);

		Assert.assertEquals(210, readSentences.size());

		ClassifyUnit cu;
		for (Sentence sentence : readSentences) {
			// System.out.println(sentence);
			cu = UniversalMapper.getCUforID(sentence.getClassifyUnitID());
			System.out.println(cu);
		}
	}

	private static List<Sentence> readFromFile(String file) {
		List<Sentence> toReturn = new ArrayList<>();
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(file));
			Object readObject;
			while ((readObject = is.readObject()) != null) {
				if (readObject instanceof Sentence) {
					toReturn.add((Sentence) readObject);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}
}