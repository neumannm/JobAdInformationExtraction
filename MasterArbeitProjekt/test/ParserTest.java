
import is2.data.SentenceData09;
import is2.io.CONLLWriter09;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.extraction.parsing.util.Relation;
import spinfo.tm.extraction.parsing.util.SentenceDataReader;
import spinfo.tm.preprocessing.TrainingDataGenerator;

public class ParserTest {

	private List<ClassifyUnit> paragraphs;
	private Map<UUID, ClassifyUnit> filteredClassifyUnits;

	@Before
	public void setUp() throws IOException {
		File trainingDataFile = new File(
				"data/SingleClassTrainingDataFiltered.csv");
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

	@Test
	public void testCompetenceFinder() throws IOException {
		ParagraphParser parser = new ParagraphParser();

		List<SentenceData09> parsed = new ArrayList<SentenceData09>();
		for (UUID cuID : filteredClassifyUnits.keySet()) {

			parsed.addAll(parser.parse(filteredClassifyUnits.get(cuID)));
		}

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

	/*
	 * Funktioniert nicht genauso wie wenn man die Sätze parst - kein <root>
	 * Token! Also erst parsen, in Datei schreiben und von da wieder auslesen??
	 * Oder das mit dem Einlesen einfach sein lassen...
	 */
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

}