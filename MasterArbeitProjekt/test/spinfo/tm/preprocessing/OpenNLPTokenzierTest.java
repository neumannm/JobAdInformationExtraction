package spinfo.tm.preprocessing;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class OpenNLPTokenzierTest {

	private static OpenNLPTokenizer splitter;

	@BeforeClass
	public static void setUpBeforeClass() {
		splitter = new OpenNLPTokenizer();
	}

	@Test
	public void testSentenceSplit() {
		String paragraph = "Die kommunale Kindertagesstätte liegt idyllisch am Dorfrand von _______, " +
				"umgeben von Wiesen und Wäldern. In der Einrichtung werden ca. 34 Kinder im Alter " +
				"von 1 bis 6 Jahren von insgesamt 5 pädagogischen Beschäftigten betreut. Wir bieten " +
				"Ihnen die Möglichkeit in unserer schönen und altersgerecht eingerichteten Kindertagesstätte " +
				"bei der Betreuung und Erziehung der Kinder mitzuwirken.";

		String[] sentences = splitter.splitIntoSentences(paragraph);

		Assert.assertEquals(3, sentences.length);
		
		for (String sentence : sentences) {
			System.out.println("SENTENCE:\t" + sentence);
		}
		System.out.println("**************************");

		
		paragraph = "Die Eingruppierung erfolgt auf Grundlage der Durchschnittsbelegung der Einrichtung in die Entgeltgruppe S7 TVöD VKA.";
		sentences = splitter.splitIntoSentences(paragraph);

		Assert.assertEquals(1, sentences.length);

		for (String sentence : sentences) {
			System.out.println("SENTENCE:\t" + sentence);
		}
	}

	@Test
	public void testTokenize() {
		String sentence = "Die kommunale Kindertagesstätte liegt idyllisch am Dorfrand von _______, " +
				"umgeben von Wiesen und Wäldern. In der Einrichtung werden ca. 34 Kinder im Alter " +
				"von 1 bis 6 Jahren von insgesamt 5 pädagogischen Beschäftigten betreut. Wir bieten " +
				"Ihnen die Möglichkeit in unserer schönen und altersgerecht eingerichteten Kindertagesstätte " +
				"bei der Betreuung und Erziehung der Kinder mitzuwirken.";
		
		String[] tokens = splitter.tokenizeSentence(sentence);

		Assert.assertEquals(58, tokens.length);

		for (String token : tokens) {
			System.out.println(token);
		}
		
		System.out.println("**************************");
		
		sentence = "Die Eingruppierung erfolgt auf Grundlage der Durchschnittsbelegung der Einrichtung in die Entgeltgruppe S7 TVöD VKA.";
		
		tokens = splitter.tokenizeSentence(sentence);
		Assert.assertEquals(15, tokens.length);

		for (String token : tokens) {
			System.out.println(token);
		}
	}
}