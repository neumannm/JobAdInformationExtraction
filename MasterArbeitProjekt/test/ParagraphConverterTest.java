import is2.data.SentenceData09;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import spinfo.tm.extraction.parsing.Paragraph2SentenceDataConverter;

public class ParagraphConverterTest {

	@Test
	public void test() {
		Paragraph2SentenceDataConverter conv = new Paragraph2SentenceDataConverter();

		String paragraph = "Ideal ist, wenn Sie viel positive Energie haben, gerne Verantwortung übernehmen "
				+ "und gerne Mitarbeiter führen. Gebraucht werden auch gute Englischkenntnisse und Führerschein"
				+ " Klasse B (3). Außerdem sollten Sie idealerweise ausgelernt haben und mindestens schon einmal "
				+ "in einer Klinik gearbeitet haben und die Abläufe kennen.";

		Map<Integer, SentenceData09> converted = conv.convert(paragraph);

		for (Integer sentence : converted.keySet()) {
			SentenceData09 sentenceData09 = converted.get(sentence);
			String[] forms = sentenceData09.forms;
			System.out.print("[");
			for (String form : forms) {
				System.out.print(form + " ");
			}
			System.out.println("]");
			System.out.println(sentenceData09.oneLine());

			System.out.println(sentenceData09.toString());
		}
	}
}