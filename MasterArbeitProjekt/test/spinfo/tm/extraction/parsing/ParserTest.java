package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;
import is2.io.CONLLReader09;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.Paragraph;
import spinfo.tm.util.DataAccessor;

public class ParserTest {

	private List<Paragraph> paragraphs;
	private Map<UUID, Paragraph> filteredClassifyUnits;

	@Before
	public void setUp() throws IOException {

		paragraphs = DataAccessor.getAllParagraphs();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());

		List<Paragraph> filteredParagraphs = DataAccessor
				.getFilteredCompetenceParagraphs();
		System.out.println("Anzahl ClassifyUnits gefiltert: "
				+ filteredParagraphs.size());

		filteredClassifyUnits = new HashMap<>();

		for (Paragraph cu : filteredParagraphs) {
			filteredClassifyUnits.put(cu.getID(), cu);
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

		Map<Integer, SentenceData09> sentenceData = conv.convert(paragraph);

		int i = 0;
		for (SentenceData09 sentenceData09 : sentenceData.values()) {
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
		}
	}
}