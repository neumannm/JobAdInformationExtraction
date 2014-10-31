import is2.data.SentenceData09;
import is2.io.CONLLReader09;
import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

import org.junit.Test;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.parsing.util.SentenceDataConverter;
import spinfo.tm.util.TextCleaner;

public class PlayTests {
	static int a = 0, b = 0;

	@Test
	public void test() {

		calculate();

		System.out.println(a);
		System.out.println(b);

		String abc = "abc";
		String[] split = abc.split("d");
		for (int i = 0; i < split.length; i++) {
			System.out.println(">" + split[i]);
		}
	}

	private void calculate() {
		for (int i = 0; i < 10; i++) {
			a += i;
			b++;
		}
	}

	@Test
	public void testWithSets() {
		Set<Integer> a = new TreeSet<>(Arrays.asList(new Integer[] { 1, 2, 3 }));
		Set<Integer> b = new TreeSet<>(Arrays.asList(new Integer[] { 3, 4, 5 }));

		System.out.println(a);
		System.out.println(b);

		method(a, b);

		System.out.println(a);
		System.out.println(b);

	}

	private void method(Set<Integer> x, Set<Integer> y) {
		System.out.println("In Method:");
		System.out.println(x);
		x.removeAll(y);
		System.out.println("Leave method...");
	}

	@Test
	public void testSwap() {
		String var1 = "A"; // value "A"
		String var2 = "B"; // value "B"
		System.out.println("var1: " + var1);
		System.out.println("var2: " + var2);
		System.out.println("SWAP!");
		swap(var1, var2); // swaps their values!
		// now var1 has value "B" and var2 has value "A"
		System.out.println("var1: " + var1);
		System.out.println("var2: " + var2);
	}

	void swap(String arg1, String arg2) {
		String temp = arg1;
		arg1 = arg2;
		arg2 = temp;
	}

	// for Snippet
	@Test
	public void testMatePipeline() throws InvalidFormatException,
			FileNotFoundException, IOException {
		Paragraph paragraph = new Paragraph(
				"Dies ist ein Test. Er ist sehr wichtig.", 0);
		parseParagraph(paragraph);
	}

	private void parseParagraph(Paragraph paragraph)
			throws InvalidFormatException, FileNotFoundException, IOException {
		String paragraphText = paragraph.getContent();

		File sentenceModelFile = new File("models/openNLPmodels/de-sent.bin");
		File tokenizerModelFile = new File("models/openNLPmodels/de-token.bin");

		String lemmatizerModelPath = "models/ger-tagger+lemmatizer+morphology+graph-based-3.6/lemma-ger-3.6.model";
		String taggerModelPath = "models/ger-tagger+lemmatizer+morphology+graph-based-3.6/morphology-ger-3.6.model";
		String posTaggerModelPath = "models/ger-tagger+lemmatizer+morphology+graph-based-3.6/tag-ger-3.6.model";
		String parserModelPath = "models/ger-tagger+lemmatizer+morphology+graph-based-3.6/parser-ger-3.6.model";

		SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel(
				sentenceModelFile));
		Tokenizer tokenizer = new TokenizerME(new TokenizerModel(
				tokenizerModelFile));

		Lemmatizer lemmatizer = new Lemmatizer(lemmatizerModelPath);
		is2.mtag.Tagger morphTagger = new is2.mtag.Tagger(taggerModelPath);
		Tagger posTagger = new Tagger(posTaggerModelPath);
		Parser depParser = new Parser(parserModelPath);

		String[] sentences = detector.sentDetect(paragraphText);

		SentenceData09 sentenceData;
		Map<Integer, SentenceData09> allSentencesData = new HashMap<>();

		for (int i = 0; i < sentences.length; i++) {

			sentenceData = new SentenceData09();

			ArrayList<String> forms = new ArrayList<String>();
			String[] tokens = tokenizer.tokenize(sentences[i]);

			forms.add(CONLLReader09.ROOT);

			for (String token : tokens)
				forms.add(token);

			sentenceData.init(forms.toArray(new String[0]));

			sentenceData = lemmatizer.apply(sentenceData);
			sentenceData = morphTagger.apply(sentenceData);
			sentenceData = posTagger.apply(sentenceData);
			sentenceData = depParser.apply(sentenceData);

			// kurz:
			// sentenceData =
			// depParser.apply(posTagger.apply(morphTagger.apply(lemmatizer.apply(sentenceData))));

			allSentencesData.put(i, sentenceData);

			paragraph.setSentenceData(SentenceDataConverter.convert(
					allSentencesData, paragraph.getID()));
		}

		for (Sentence sentence : paragraph.getSentenceData().values()) {
			System.out.println(sentence);
		}
	}

	// for Snippet
	@Test
	public void testCompetenceFinder() throws InvalidFormatException,
			FileNotFoundException, IOException {
		Paragraph paragraph = new Paragraph(
				"Sie verfügen über Durchhaltevermögen. Wenn Sie auch noch Geduld haben, ist das gut.",
				0);

		parseParagraph(paragraph);

		for (Integer i : paragraph.getSentenceData().keySet()) {
			Sentence sentence = paragraph.getSentenceData().get(i);

			findCompetences(sentence);
		}
	}

	private void findCompetences(Sentence sentence) {
		List<SlotFiller> fillers = new ArrayList<>();

		SlotFiller subject;

		Set<String> verbsOfInterest = new HashSet<String>();
		verbsOfInterest.add("verfügen");
		verbsOfInterest.add("haben");
		verbsOfInterest.add("bringen");

		String[] lemmas = sentence.getLemmas();

		boolean subjectIsNoun = subjectIsNoun(sentence);
		int subjectID = getSubjectID(sentence);

		for (int arrayIndex = 0; arrayIndex < lemmas.length; arrayIndex++) {
			if (verbsOfInterest.contains(lemmas[arrayIndex])) {
				int verbID = arrayIndex + 1;

				if (subjectIsNoun) {
					subject = getSubjectNP(sentence, subjectID);
					fillers.add(subject);
				} else {
					List<SlotFiller> objects = getObjects(lemmas[arrayIndex],
							verbID, sentence);
					fillers.addAll(objects);
				}
			}
		}
	}

	private List<SlotFiller> getObjects(String head, int headID, Sentence sd) {
		List<SlotFiller> filler = new ArrayList<>();

		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == headID) {
				// lemma an dieser stelle hat als kopf das verb mit der geg. id
				String dependant = sd.getTokens()[i];
				// String dependantLemma = sd.plemmas[i];
				String dependency = sd.getDepLabels()[i];

				if ("OA".equals(dependency) || "OA2".equals(dependency)
						|| "OC".equals(dependency) || "OP".equals(dependency)
						|| "PD".equals(dependency)) {
					System.out.println(String.format(
							"Found object for verb '%s':\t %s (DEP: %s )",
							head.toUpperCase(), dependant, dependency));
					String argument = getPhrase(i + 1, sd,
							new TreeSet<Integer>());

					filler.add(new SlotFiller(argument, UUID.randomUUID()));
				}
			}
		}
		return filler;
	}

	// vielleicht nur sowas kleines hier als Snippet zur Demo
	private int getSubjectID(Sentence sd) {
		String[] depLabels = sd.getDepLabels();
		for (int i = 0; i < depLabels.length; i++) {
			if ("SB".equals(depLabels[i])) {
				return i + 1;
			}
		}
		return -1;
	}

	private boolean subjectIsNoun(Sentence sd) {
		String subjectPOS = null;
		subjectPOS = getSubjectPOS(sd);

		if (subjectPOS == null) {
			// no subject
			return false;
		} else {
			switch (subjectPOS) {
			case "PPER":
				return false;
			case "NN":
				return true;
			default:
				// other (should not happen)
				System.err
						.println("Subject has other POS than PPER or NN or null");
				break;
			}
		}
		return false;
	}

	private String getSubjectPOS(Sentence sd) {
		String[] depLabels = sd.getDepLabels();
		for (int i = 0; i < depLabels.length; i++) {
			if ("SB".equals(depLabels[i])) {
				return sd.getPOSTags()[i];
			}
		}
		return null;
	}

	private SlotFiller getSubjectNP(Sentence sd, int subjectID) {
		// arrayIndex = subjectID - 1
		String subject = sd.getTokens()[subjectID - 1];
		System.out.println(String.format("Subject is a Noun (%s)", subject));
		/*
		 * process, i.e. Subject and Dependants seem to be the competences
		 */
		String argument = getPhrase(subjectID, sd, new TreeSet<Integer>());

		SlotFiller filler = new SlotFiller(argument, UUID.randomUUID());
		return filler;

	}

	private String getPhrase(int headID, Sentence sd, Set<Integer> components) {

		int[] heads = sd.getHeads();
		for (int i = 0; i < heads.length; i++) {
			if (heads[i] == headID) {
				getPhrase(i + 1, sd, components);
			}
		}
		components.add(headID);

		/*
		 * Phrase zusammensetzen
		 */
		StringBuffer sb = new StringBuffer();
		for (Integer i : components) {
			sb.append(sd.getTokens()[i - 1]).append(" ");
		}

		/*
		 * Entferne Leerzeichen vor Satzzeichen
		 */
		return TextCleaner.removeUnneccessaryWhitespace(sb.toString());
	}
}
