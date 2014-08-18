import java.util.Map;

import is2.data.SentenceData09;

import org.junit.BeforeClass;
import org.junit.Test;

import spinfo.tm.extraction.parsing.util.POSAnnotator;
import spinfo.tm.preprocessing.OpenNLPTokenizer;

public class POSAnnotationTest {

	private static POSAnnotator annotator;
	private static String[] tokens;

	@BeforeClass
	public static void setUpBeforeClass() {
		annotator = new POSAnnotator();

		OpenNLPTokenizer tokenizer = new OpenNLPTokenizer();
		String sentence = "Ideal ist, wenn Sie viel positive Energie haben, gerne Verantwortung übernehmen "
				+ "und gerne Mitarbeiter führen";
		tokens = tokenizer.tokenizeSentence(sentence);
	}

	@Test
	public void testMateAnnotation() {
		SentenceData09 sentence = new SentenceData09();
		sentence.init(tokens);
		Map<String, String> tagged = annotator.tagWithMate(sentence);
		System.out.println("\nResult with Mate:");
		System.out.println(tagged);
	}

	@Test
	public void testOpenNLPAnnotation() {
		String[] sentence = tokens;
		Map<String, String> tagged = annotator.tagWithOpenNLP(sentence);
		System.out.println("\nResult with OpenNLP:");
		System.out.println(tagged);
	}
}
