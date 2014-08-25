package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import is2.tools.Tool;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.parsing.util.SentenceDataConverter;

public class ParagraphParser {

	private Tool lemmatizer;
	private is2.mtag.Tagger morphTagger;
	private Tagger posTagger;
	private Tool depParser;
	private Paragraph2SentenceDataConverter conv = new Paragraph2SentenceDataConverter();

	private final static Logger log = LogManager
			.getLogger(ParagraphParser.class);

	public ParagraphParser() {
		System.setProperty("log4j.configurationFile", "utils/log4j2.xml");
		setUpTools();
	}

	private void setUpTools() {
		log.info("\nReading the model of the lemmatizer");
		lemmatizer = new Lemmatizer(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/lemma-ger-3.6.model");

		log.info("\nReading the model of the morphologic tagger");
		morphTagger = new is2.mtag.Tagger(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/morphology-ger-3.6.model");

		log.info("\nReading the model of the POS tagger");
		posTagger = new Tagger(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/tag-ger-3.6.model");

		log.info("\nReading the model of the dependency parser");
		depParser = new Parser(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/parser-ger-3.6.model");

	}

	public List<ClassifyUnit> parse(List<ClassifyUnit> cus) {
		for (ClassifyUnit classifyUnit : cus) {
			parse(classifyUnit);
		}
		return cus;
	}

	public void parse(ClassifyUnit cu) {
		String paragraph = cu.getContent();
		Map<Integer, SentenceData09> processed = conv.convert(paragraph);
		SentenceData09 sentenceData;
		for (Integer sentence : processed.keySet()) {
			sentenceData = applyTools(processed.get(sentence));
			processed.put(sentence, sentenceData);
		}

		cu.setSentenceData(SentenceDataConverter.convert(processed, cu.getID()));
	}

	public SentenceData09 applyTools(SentenceData09 sentenceData) {
		log.info("Applying the lemmatizer");
		sentenceData = lemmatizer.apply(sentenceData);

		log.info("\nApplying the morphologic tagger");
		sentenceData = morphTagger.apply(sentenceData);

		log.info("\nApplying the part-of-speech tagger");
		sentenceData = posTagger.apply(sentenceData);

		log.info("\nApplying the parser");
		sentenceData = depParser.apply(sentenceData);

		return sentenceData;
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
