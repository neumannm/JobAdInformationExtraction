package spinfo.tm.extraction.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.preprocessing.TrainingDataReader;
import spinfo.tm.util.ClassFilter;

public class CompetenceFinderTest {

	private Map<UUID, Paragraph> filteredClassifyUnits;
	private Map<String, String> verbsOfInterest;

	private static final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
	private static final String VERBSFILE = "models/verbsOfInterest.txt";

	public void setUp() throws IOException {
		File trainingDataFile = new File(TRAININGDATAFILE);
		TrainingDataReader tdg = new TrainingDataReader(trainingDataFile);

		List<Paragraph> paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());

		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		List<Paragraph> filteredParagraphs = ClassFilter.filter(paragraphs,
				classesToAnnotate);

		System.out.println("Anzahl ClassifyUnits gefiltert: "
				+ filteredParagraphs.size());

		filteredClassifyUnits = new HashMap<>();

		ParagraphParser parser = new ParagraphParser();

		for (Paragraph cu : filteredParagraphs) {
			parser.parse(cu);
			filteredClassifyUnits.put(cu.getID(), cu);
		}

		verbsOfInterest = readVerbsOfInterest(VERBSFILE);

		System.out
				.println("####################DONE SETUP######################");
	}

	private Map<String, String> readVerbsOfInterest(String file) {
		File inputFile = new File(file);
		if (!inputFile.getName().endsWith(".txt")) {
			System.err.println("Wrong file format");
			return null;
		}

		Map<String, String> toReturn = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inputFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(":");
				if (split.length == 1) {
					toReturn.put(split[0], null);
					continue;
				}
				if (split.length == 2)
					toReturn.put(split[0], split[1]);
				else {
					System.err.println("Wrong format");
					return null;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	@Test
	public void testCompetenceFinder() throws IOException {
		setUp();

		DepCompetenceFinder finder = new DepCompetenceFinder(verbsOfInterest);

		for (UUID id : filteredClassifyUnits.keySet()) {
			Set<SlotFiller> competences = finder
					.findCompetences(filteredClassifyUnits.get(id));
			System.out.println(id);
			System.out.println(competences);
			System.out.println("****************\n");
		}
	}

	@Test
	public void testWithDummyData() {
		verbsOfInterest = readVerbsOfInterest(VERBSFILE);

		String paragraph = "Ideal ist, wenn Sie viel positive Energie haben, gerne Verantwortung übernehmen "
				+ "und gerne Mitarbeiter führen. Gebraucht werden auch gute Englischkenntnisse und Führerschein"
				+ " Klasse B (3). Außerdem sollten Sie idealerweise ausgelernt haben und mindestens schon einmal "
				+ "in einer Klinik gearbeitet haben und die Abläufe kennen.";

		Paragraph cu = new Paragraph(paragraph, 0);
		ParagraphParser parser = new ParagraphParser();
		parser.parse(cu);

		DepCompetenceFinder finder = new DepCompetenceFinder(verbsOfInterest);

		Set<SlotFiller> competences = finder.findCompetences(cu);
		System.out.println(competences);
		System.out.println("****************\n");
	}
}