package spinfo.tm;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.Section;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.preprocessing.TrainingDataReader;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.UniversalMapper;

public class Preparation {

	private static List<Section> filteredParagraphs;
	private static List<Sentence> savedSentences = new ArrayList<>();

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String ALLSECTIONSFILE = "data/allClassifyUnits.bin";
	private static final String OUTPUTFILE = "data/parsedSentences.bin";

	/*
	 * Nur ausführen, wenn Datei nicht vorhanden! (parsen dauert)
	 */
	public static void main(String[] args) {

		try {
			readAndFilterParagraphs();
			parseFilteredParagraphs();

			for (Section paragraph : filteredParagraphs) {
				Map<Integer, Sentence> sentenceData = paragraph
						.getSentenceData();
				for (Integer sentence : sentenceData.keySet()) {
					Sentence parsed = sentenceData.get(sentence);
					System.out.println(parsed);
				}
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
		TrainingDataReader tdg = new TrainingDataReader(trainingDataFile);

		List<Section> paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl Sections insgesamt: "
				+ paragraphs.size());
		saveToFile(paragraphs, ALLSECTIONSFILE);

		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		filteredParagraphs = ClassFilter.filter(paragraphs, classesToAnnotate);

		System.out.println("Anzahl Sections gefiltert: "
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
	public void testReadParsedSentencesFromFile() {
		List<Sentence> readSentences = readSentencesFromFile(OUTPUTFILE);

		Assert.assertEquals(210, readSentences.size());

		Map<UUID, Map<Integer, Sentence>> sentenceDatas = new HashMap<UUID, Map<Integer, Sentence>>();
		
		Section cu;
		for (Sentence sentence : readSentences) {
			UUID unitID = sentence.getClassifyUnitID();
			if(sentenceDatas.get(unitID) == null){
				sentenceDatas.put(unitID, new TreeMap<Integer, Sentence>());
			}
			sentenceDatas.get(unitID).put(sentence.getPositionInParagraph(), sentence);
			
		}
		
		for (UUID unitID : sentenceDatas.keySet()) {
			cu = UniversalMapper.getCUforID(unitID);
			cu.setSentenceData(sentenceDatas.get(unitID));
			System.out.println(cu);
			System.out.println(cu.getSentenceData());
		}
	}

	@Test
	public void testReadClassifyUnitsFromFile() {
		List<Section> cUsFromFile = readCUsFromFile(ALLSECTIONSFILE);

		Assert.assertEquals(376, cUsFromFile.size());

		for (Section classifyUnit : cUsFromFile) {
			System.out.println(classifyUnit);
			System.out.println(classifyUnit.getSentenceData());
		}
	}

	private static List<Section> readCUsFromFile(String file) {
		List<Section> toReturn = new ArrayList<>();
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(file));
			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof Section) {
					toReturn.add((Section) readObject);
				}
			}
		} catch (EOFException e) {
			return toReturn;
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

	private static List<Sentence> readSentencesFromFile(String file) {
		List<Sentence> toReturn = new ArrayList<>();
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(file));
			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof Sentence) {
					toReturn.add((Sentence) readObject);
				}
			}
		} catch (EOFException e) {
			return toReturn;
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