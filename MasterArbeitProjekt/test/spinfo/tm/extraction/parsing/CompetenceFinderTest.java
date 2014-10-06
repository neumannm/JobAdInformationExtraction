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

import org.junit.Test;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.DataAccessor;

public class CompetenceFinderTest {

	private List<Paragraph> parsedParagraphs;
	private Map<String, String> verbsOfInterest;

	private static final String VERBSFILE = "models/verbsOfInterest.txt";

	public void setUp() throws IOException {
		parsedParagraphs = DataAccessor.getParsedCompetenceParagraphs();

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
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile)));) {

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
		}
		return toReturn;
	}

	@Test
	public void testCompetenceFinder() throws IOException {
		setUp();

		DepCompetenceFinder finder = new DepCompetenceFinder(verbsOfInterest);

		for (Paragraph par : parsedParagraphs) {
			Set<SlotFiller> competences = finder.findCompetences(par);
			System.out.println(par);
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