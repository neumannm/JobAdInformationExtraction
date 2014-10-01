package spinfo.tm;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.ReaderWriter;
import spinfo.tm.util.UniversalMapper;

public class Preparation {

	private static List<Paragraph> filteredParagraphs;
	private static List<Sentence> savedSentences = new ArrayList<>();

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String ALLPARAGRAPHSFILE = "data/allParagraphs.bin";
	private static final String OUTPUTFILE = "data/parsedSentences.bin";
	private static final String PARSEDPARAGRAPHSFILE = "data/parsedParagraphs.bin";

	/*
	 * Nur ausführen, wenn Datei nicht vorhanden! (parsen dauert)
	 */
	public static void main(String[] args) {

		try {
			readAndFilterParagraphs();
			parseFilteredParagraphs();

			for (Paragraph paragraph : filteredParagraphs) {
				Map<Integer, Sentence> sentenceData = paragraph
						.getSentenceData();
				for (Integer sentence : sentenceData.keySet()) {
					Sentence parsed = sentenceData.get(sentence);
					System.out.println(parsed);
				}
				savedSentences.addAll(paragraph.getSentenceData().values());
			}
			saveToBinaryFile(savedSentences, OUTPUTFILE);

			System.out.println("Number of sentences: " + savedSentences.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readAndFilterParagraphs() throws IOException {
		List<Paragraph> paragraphs = ReaderWriter.readParagraphsFromCSV(TRAININGDATAFILE);
		System.out.println("Anzahl Paragraphs insgesamt: "
				+ paragraphs.size());
		
		saveToBinaryFile(paragraphs, ALLPARAGRAPHSFILE);

		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		filteredParagraphs = ClassFilter.filter(paragraphs, classesToAnnotate);

		System.out.println("Anzahl Paragraphs gefiltert: "
				+ filteredParagraphs.size());
	}

	private static void parseFilteredParagraphs() {
		ParagraphParser parser = new ParagraphParser();
		filteredParagraphs = parser.parse(filteredParagraphs);
		
		saveToBinaryFile(filteredParagraphs, PARSEDPARAGRAPHSFILE);
	}

	private static void saveToBinaryFile(Collection<?> data, String fileName) {
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
		List<Sentence> readSentences = ReaderWriter.readSentencesFromBinary(new File(OUTPUTFILE));

		Assert.assertEquals(210, readSentences.size());

		Map<UUID, Map<Integer, Sentence>> sentenceDatas = new HashMap<UUID, Map<Integer, Sentence>>();
		
		Paragraph p;
		for (Sentence sentence : readSentences) {
			UUID unitID = sentence.getParagraphID();
			if(sentenceDatas.get(unitID) == null){
				sentenceDatas.put(unitID, new TreeMap<Integer, Sentence>());
			}
			sentenceDatas.get(unitID).put(sentence.getPositionInParagraph(), sentence);
			
		}
		
		for (UUID unitID : sentenceDatas.keySet()) {
			p = UniversalMapper.getParagraphforID(unitID);
			p.setSentenceData(sentenceDatas.get(unitID));
			System.out.println(p);
			System.out.println(p.getSentenceData());
		}
	}

	@Test
	public void testReadParagraphsFromFile() {
		List<Paragraph> paragraphsFromFile = ReaderWriter.readParagraphsFromBinary(new File(ALLPARAGRAPHSFILE));

		Assert.assertEquals(376, paragraphsFromFile.size());

		for (Paragraph paragraph : paragraphsFromFile) {
			System.out.println(paragraph);
			System.out.println(paragraph.getSentenceData());
		}
	}
	
	
}