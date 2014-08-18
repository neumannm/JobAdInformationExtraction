package spinfo.tm.extraction.parsing.util;

import is2.data.SentenceData09;
import is2.tag.Tagger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spinfo.tm.extraction.parsing.ParagraphParser;

/*
 * TODO use openNLP Tagger or Mate Tagger? 
 * 
 * Mate wäre besser wegen Konsistenz...
 */
public class POSAnnotator {

	private Tagger posTagger;
	private POSTaggerME posTagger2;

	private final static Logger log = LogManager
			.getLogger(ParagraphParser.class);

	public POSAnnotator() {
		System.setProperty("log4j.configurationFile", "utils/log4j2.xml");

		log.info("\nReading the model of the Mate POS tagger");
		posTagger = new Tagger(
				"models/ger-tagger+lemmatizer+morphology+graph-based-3.6/tag-ger-3.6.model");

		log.info("\nReading the model of the OpenNLP POS tagger");
		initializeOpenNLPPOSTagger("models/openNLPmodels/de-pos-maxent.bin");
	}

	private void initializeOpenNLPPOSTagger(String model) {
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(model);
			POSModel POSmodel = new POSModel(modelIn);
			posTagger2 = new POSTaggerME(POSmodel);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public Map<String, String> tagWithOpenNLP(String[] sentence) {
		Map<String, String> toReturn = new HashMap<>();

		String[] tagged = posTagger2.tag(sentence);
		for (int i = 0; i < tagged.length; i++) {
			toReturn.put(sentence[i], tagged[i]);
		}
		return toReturn;
	}

	public Map<String, String> tagWithMate(SentenceData09 sentence) {
		Map<String, String> toReturn = new HashMap<>();

		sentence = posTagger.apply(sentence);
		for (int i = 0; i < sentence.length(); i++) {
			toReturn.put(sentence.forms[i], sentence.ppos[i]);
		}
		return toReturn;
	}
}