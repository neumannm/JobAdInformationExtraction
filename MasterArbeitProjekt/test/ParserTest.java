import is2.data.SentenceData09;
import is2.io.CONLLReader09;
import is2.io.CONLLWriter09;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.ClassFilter;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.parsing.CompetenceFinder;
import spinfo.tm.extraction.parsing.Paragraph2SentenceDataConverter;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.extraction.parsing.util.Relation;
import spinfo.tm.extraction.parsing.util.SentenceDataReader;
import spinfo.tm.preprocessing.TrainingDataGenerator;

public class ParserTest {

	private List<ClassifyUnit> paragraphs;
	private Map<UUID, ClassifyUnit> filteredClassifyUnits;

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String TRAININGDATAPARSEDFILE = "data/SingleClassTrainingDataParsed.txt";

	@Before
	public void setUp() throws IOException {
		File trainingDataFile = new File(TRAININGDATAFILE);
		/* Training data generation */
		TrainingDataGenerator tdg = new TrainingDataGenerator(trainingDataFile);

		paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());

		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		List<ClassifyUnit> filteredParagraphs = ClassFilter.filter(paragraphs,
				classesToAnnotate);

		System.out.println("Anzahl ClassifyUnits gefiltert: "
				+ filteredParagraphs.size());

		filteredClassifyUnits = new HashMap<>();

		for (ClassifyUnit cu : filteredParagraphs) {
			filteredClassifyUnits.put(cu.getID(), cu);
		}
	}

	@Test
	public void test() throws IOException {
		ParagraphParser parser = new ParagraphParser();
		Writer w = new PrintWriter("output.csv");
		CONLLWriter09 writer = new is2.io.CONLLWriter09(w);

		List<SentenceData09> parsed;
		for (UUID cuID : filteredClassifyUnits.keySet()) {

			parsed = parser.parse(filteredClassifyUnits.get(cuID));
			for (SentenceData09 sentenceData : parsed) {
				writer.write(sentenceData, CONLLWriter09.NO_ROOT);
			}
		}
		writer.finishWriting();
	}

	//TODO: rewrite test
	@Test
	public void testCompetenceFinder() throws IOException {

		List<SentenceData09> parsed = getParsedSentencesFromClassifyUnits(filteredClassifyUnits);

		Map<String, Relation> verbsOfInterest = new HashMap<>();
		verbsOfInterest.put("haben", Relation.OBJECT);
		verbsOfInterest.put("sein", Relation.BOTH);
		verbsOfInterest.put("verfügen", Relation.OBJECT);
		verbsOfInterest.put("suchen", Relation.OBJECT);
		verbsOfInterest.put("sollen", Relation.BOTH); // meistens 'sollte', also
														// VMFIN (finites
														// Modalverb)
		verbsOfInterest.put("setzen", Relation.OBJECT); // 'setzen voraus'
		verbsOfInterest.put("werden", Relation.SUBJECT); // 'wird vorausgesetzt'

		CompetenceFinder finder = new CompetenceFinder(verbsOfInterest);

		for (SentenceData09 sentenceData : parsed) {
			finder.findCompetences(sentenceData);
		}
	}

	private List<SentenceData09> getParsedSentencesFromClassifyUnits(
			Map<UUID, ClassifyUnit> filteredClassifyUnits2) {
		List<SentenceData09> parsed = null;

		File parsedFile = new File(TRAININGDATAPARSEDFILE);
		if (!parsedFile.exists()) {
			ParagraphParser parser = new ParagraphParser();

			parsed = new ArrayList<SentenceData09>();
			for (UUID cuID : filteredClassifyUnits.keySet()) {
				parsed.addAll(parser.parse(filteredClassifyUnits.get(cuID)));
			}
		}

		else {
			parsed = readParsedSentencesFromFile();
		}

		return parsed;
	}

	private List<SentenceData09> readParsedSentencesFromFile() {
		return null;
	}

	/*
	 * Funktioniert nicht genauso wie wenn man die Sätze parst - kein <root>
	 * Token! Also erst parsen, in Datei schreiben und von da wieder auslesen??
	 * Oder das mit dem Einlesen einfach sein lassen...
	 */
	//TODO: rewrite test
	@Test
	public void testReadFromFile() {
		List<SentenceData09> data = SentenceDataReader
				.readFromFile("data/parsedGoodSentences.csv");

		Map<String, Relation> verbsOfInterest = new HashMap<>();
		verbsOfInterest.put("haben", Relation.OBJECT);
		verbsOfInterest.put("sein", null); // kann beides sein
		verbsOfInterest.put("verfügen", Relation.OBJECT);
		verbsOfInterest.put("suchen", Relation.OBJECT);
		verbsOfInterest.put("sollen", null); // meistens 'sollte', also VMFIN
												// (finites Modalverb) //kann
												// beides sein
		verbsOfInterest.put("setzen", Relation.OBJECT); // 'setzen voraus'
		verbsOfInterest.put("werden", Relation.SUBJECT); // 'wird vorausgesetzt'

		CompetenceFinder finder = new CompetenceFinder(verbsOfInterest);

		for (SentenceData09 sentenceData : data) {
			// System.out.println(sentenceData);
			finder.findCompetences(sentenceData);
		}
	}

	@Test
	public void testWritingAndReadingParsedData() {
		ParagraphParser parser = new ParagraphParser();

		Paragraph2SentenceDataConverter conv = new Paragraph2SentenceDataConverter();

		String paragraph = "Ideal ist, wenn Sie viel positive Energie haben, gerne Verantwortung übernehmen "
				+ "und gerne Mitarbeiter führen. Gebraucht werden auch gute Englischkenntnisse und Führerschein"
				+ " Klasse B (3). Außerdem sollten Sie idealerweise ausgelernt haben und mindestens schon einmal "
				+ "in einer Klinik gearbeitet haben und die Abläufe kennen.";

		List<SentenceData09> sentenceData = conv.convert(paragraph);

		int i = 0;
		for (SentenceData09 sentenceData09 : sentenceData) {
			String[] forms = sentenceData09.forms;
			System.out.println(sentenceData09.toString());

			saveToFile(sentenceData09, i);

			System.out
					.println("**************\nRead from File:\n*******************");
			sentenceData09 = readFromFile(i);
			System.out.println(sentenceData09.toString());

			System.out.println("**************\nParsed:\n*******************");
			SentenceData09 parsed = parser.applyTools(sentenceData09);
			System.out.println(parsed);
			
			i++;
			
			System.out.println("#########################");
		}

	}

	private SentenceData09 readFromFile(int i) {
		SentenceData09 toReturn = null;

		CONLLReader09 reader = new CONLLReader09("sentenceData" + i + ".txt");
		SentenceData09 nextCONLL09;
		if ((nextCONLL09 = reader.getNextCoNLL09()) != null) {
			toReturn = nextCONLL09;
		}

		return toReturn;
	}

	private void saveToFile(SentenceData09 sd, int i) {
		File dest = new File("sentenceData" + i + ".txt");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(dest);
			pw.write(sd.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}

	}

}