package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import is2.tools.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spinfo.tm.data.ClassifyUnit;

public class ParagraphParser {

	private Tool lemmatizer;
	private is2.mtag.Tagger morphTagger;
	private Tagger posTagger;
	private Tool depParser;
	private Paragraph2SentenceDataConverter conv = new Paragraph2SentenceDataConverter();

	public ParagraphParser() {

		setUpTools();

	}

	private void setUpTools() {
		File toolsDir = new File("parsingTools");

		if (!toolsDir.exists() || !toolsDir.isDirectory()) {
			toolsDir.mkdir();
		}

		if (toolsDir.list().length == 0) {
			System.out.println("\nReading the model of the lemmatizer");
			lemmatizer = new Lemmatizer(
					"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/lemma-ger-3.6.model");

			System.out.println("\nReading the model of the morphologic tagger");
			morphTagger = new is2.mtag.Tagger(
					"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/morphology-ger-3.6.model");

			System.out.println("\nReading the model of the POS tagger");
			posTagger = new Tagger(
					"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/tag-ger-3.6.model");

			System.out.println("\nReading the model of the dependency parser");
			depParser = new Parser(
					"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/parser-ger-3.6.model");

			serializeTools();
		}

		deserializeTools();
	}

	private void serializeTools() {
		// TODO Auto-generated method stub
		
	}

	private void deserializeTools() {
		// TODO Auto-generated method stub

	}

	public List<SentenceData09> parse(ClassifyUnit cu) {
		List<SentenceData09> toReturn = new ArrayList<SentenceData09>();

		String paragraph = cu.getContent();
		List<SentenceData09> processed = conv.convert(paragraph);
		for (SentenceData09 sentenceData : processed) {
			// System.out.println("Applying the lemmatizer");
			sentenceData = lemmatizer.apply(sentenceData);

			// System.out.println("\nApplying the morphologic tagger");
			sentenceData = morphTagger.apply(sentenceData);

			// System.out.println("\nApplying the part-of-speech tagger");
			sentenceData = posTagger.apply(sentenceData);

			// System.out.println("\nApplying the parser");
			sentenceData = depParser.apply(sentenceData);

			toReturn.add(sentenceData);
		}

		return toReturn;
	}

	public void resetLemmatizer(String model) {
		lemmatizer = new Lemmatizer(model);
	}

	public void resetMorphTagger(String model) {
		morphTagger = new is2.mtag.Tagger(model);
	}

	public void resetPOSTagger(String model) {
		posTagger = new Tagger(model);
	}

	public void resetParser(String model) {
		depParser = new Parser(model);
	}
}
