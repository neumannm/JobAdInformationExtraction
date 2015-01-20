package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import is2.tools.Tool;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.parsing.util.SentenceDataConverter;

public class ParagraphParser {

	private Tool lemmatizer;
	private is2.mtag.Tagger morphTagger;
	private Tagger posTagger;
	private Tool depParser;
	private Paragraph2SentenceDataConverter conv = new Paragraph2SentenceDataConverter();

	private final static Logger logger = Logger.getLogger(ParagraphParser.class
			.getSimpleName());

	/**
	 * Constructor. Create a new ParagraphParser Object. Sets up the tools that
	 * will be used for parsing a sentence.
	 */
	public ParagraphParser() {
		setUpTools();
	}

	private void setUpTools() {
		logger.info("\nReading the model of the lemmatizer");
		lemmatizer = new Lemmatizer(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/lemma-ger-3.6.model");

		logger.info("\nReading the model of the morphologic tagger");
		morphTagger = new is2.mtag.Tagger(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/morphology-ger-3.6.model");

		logger.info("\nReading the model of the POS tagger");
		posTagger = new Tagger(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/tag-ger-3.6.model");

		logger.info("\nReading the model of the dependency parser");
		depParser = new Parser(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/parser-ger-3.6.model");

	}

	/**
	 * Parse a list of paragraphs and return them.
	 * 
	 * @param paragraphs
	 *            paragraphs to be parsed
	 * @return paragrapghs enriched with parsing information
	 */
	public List<Paragraph> parse(List<Paragraph> paragraphs) {
		for (Paragraph paragraph : paragraphs) {
			paragraph = parse(paragraph);
		}
		return paragraphs;
	}

	/**
	 * Parse a paragraph.
	 * 
	 * @param paragraph
	 *            paragraph to be parsed
	 * @return paragrapgh enriched with parsing information
	 */
	public Paragraph parse(Paragraph paragraph) {
		String paragraphText = paragraph.getContent();
		Map<Integer, SentenceData09> processed = conv.convert(paragraphText);
		SentenceData09 sentenceData;
		for (Integer sentence : processed.keySet()) {
			sentenceData = applyTools(processed.get(sentence));
			processed.put(sentence, sentenceData);
		}

		paragraph.setSentenceData(SentenceDataConverter.convert(processed,
				paragraph.getID()));
		return paragraph;
	}

	private SentenceData09 applyTools(SentenceData09 sentenceData) {
		logger.info("Applying the lemmatizer");
		sentenceData = lemmatizer.apply(sentenceData);

		logger.info("\nApplying the morphologic tagger");
		sentenceData = morphTagger.apply(sentenceData);

		logger.info("\nApplying the part-of-speech tagger");
		sentenceData = posTagger.apply(sentenceData);

		logger.info("\nApplying the parser");
		sentenceData = depParser.apply(sentenceData);

		return sentenceData;
	}
}