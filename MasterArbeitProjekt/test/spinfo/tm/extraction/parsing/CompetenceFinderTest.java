package spinfo.tm.extraction.parsing;
import is2.data.SentenceData09;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import spinfo.tm.data.Section;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.parsing.CompetenceFinder;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.extraction.parsing.util.SentenceDataReader;
import spinfo.tm.preprocessing.TrainingDataReader;
import spinfo.tm.util.ClassFilter;

public class CompetenceFinderTest {

	private Map<UUID, Section> filteredClassifyUnits;
	private List<String> verbsOfInterest;

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";

	public void setUp() throws IOException {
		File trainingDataFile = new File(TRAININGDATAFILE);
		TrainingDataReader tdg = new TrainingDataReader(trainingDataFile);

		List<Section> paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());

		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		List<Section> filteredParagraphs = ClassFilter.filter(paragraphs,
				classesToAnnotate);

		System.out.println("Anzahl ClassifyUnits gefiltert: "
				+ filteredParagraphs.size());

		filteredClassifyUnits = new HashMap<>();

		ParagraphParser parser = new ParagraphParser();

		for (Section cu : filteredParagraphs) {
			parser.parse(cu);
			filteredClassifyUnits.put(cu.getID(), cu);
		}

		verbsOfInterest = new ArrayList<>();
		verbsOfInterest.add("haben");
		verbsOfInterest.add("sein");
		verbsOfInterest.add("verfügen");
		verbsOfInterest.add("suchen");
		verbsOfInterest.add("sollen"); // meistens 'sollte', also VMFIN (finites
										// Modalverb)
		verbsOfInterest.add("setzen"); // 'setzen voraus'
		verbsOfInterest.add("werden"); // 'wird vorausgesetzt'
		verbsOfInterest.add("wünschen"); // 'wir wünschen uns'
		verbsOfInterest.add("müssen"); // 'Sie müssen'
		verbsOfInterest.add("erwarten"); // 'wir erwarten'

		System.out
				.println("####################DONE SETUP######################");
	}

	@Test
	public void testCompetenceFinder() throws IOException {
		setUp();

		CompetenceFinder finder = new CompetenceFinder(verbsOfInterest);

		for (UUID id : filteredClassifyUnits.keySet()) {
			List<SlotFiller> competences = finder
					.findCompetences(filteredClassifyUnits.get(id));
			System.out.println(id);
			System.out.println(competences);
			System.out.println("****************\n");
		}
	}

	@Test
	public void testWithDummyData() {
		verbsOfInterest = new ArrayList<>();
		verbsOfInterest.add("haben");
		verbsOfInterest.add("sein");
		verbsOfInterest.add("verfügen");
		verbsOfInterest.add("suchen");
		verbsOfInterest.add("sollen"); // meistens 'sollte', also VMFIN (finites
										// Modalverb)
		verbsOfInterest.add("setzen"); // 'setzen voraus'
		verbsOfInterest.add("werden"); // 'wird vorausgesetzt'
		verbsOfInterest.add("wünschen"); // 'wir wünschen uns'
		verbsOfInterest.add("müssen"); // 'Sie müssen'
		verbsOfInterest.add("erwarten"); // 'wir erwarten'
		verbsOfInterest.add("benötigen"); // 'Sie benötigen'

		String paragraph = "Ideal ist, wenn Sie viel positive Energie haben, gerne Verantwortung übernehmen "
				+ "und gerne Mitarbeiter führen. Gebraucht werden auch gute Englischkenntnisse und Führerschein"
				+ " Klasse B (3). Außerdem sollten Sie idealerweise ausgelernt haben und mindestens schon einmal "
				+ "in einer Klinik gearbeitet haben und die Abläufe kennen.";

		Section cu = new Section(paragraph, 0);
		ParagraphParser parser = new ParagraphParser();
		parser.parse(cu);

		CompetenceFinder finder = new CompetenceFinder(verbsOfInterest);

		List<SlotFiller> competences = finder.findCompetences(cu);
		System.out.println(competences);
		System.out.println("****************\n");
	}

	/*
	 * Funktioniert nicht genauso wie wenn man die Sätze parst - kein <root>
	 * Token! Dadurch verschieben sich die Indizes! Also erst parsen, in Datei
	 * schreiben und von da wieder auslesen?? Oder das mit dem Einlesen einfach
	 * sein lassen...
	 */
	@Test
	public void testReadFromFile() throws IOException {
		setUp();

		List<SentenceData09> data = SentenceDataReader
				.readFromFile("data/parsedGoodSentences.csv");

		CompetenceFinder finder = new CompetenceFinder(verbsOfInterest);

		// List<SlotFiller> competences = finder.findCompetences(cu);
		// System.out.println(competences);
		// System.out.println("****************\n");
	}
}
