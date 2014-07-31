package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;
import is2.io.CONLLReader09;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * Class for conversion of a paragraph (String) to SentenceData in
 * CONLL09-Format for use with Mate Tools
 * 
 * @author neumannm
 * 
 */
public class Paragraph2SentenceDataConverter {

	private SentenceModel sentencemodel;
	private TokenizerModel tokenizeModel;

	/**
	 * Constructor - sets the models to be used for sentence splitting and
	 * tokenization.
	 */
	public Paragraph2SentenceDataConverter() {
		setSentenceSplitModel("models/openNLPmodels/de-sent.bin");
		setTokenizeModel("models/openNLPmodels/de-token.bin");
	}

	private void setSentenceSplitModel(String model) {
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(model);
			sentencemodel = new SentenceModel(modelIn);
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

	private void setTokenizeModel(String model) {
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(model);
			tokenizeModel = new TokenizerModel(modelIn);
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

	/**
	 * Converts the given paragraph into list of SentenceData. Paragraph is
	 * first split into sentences, which are then each tokenized and converted
	 * to SentenceData Objects.
	 * 
	 * @param paragraph
	 *            Paragraph to be converted
	 * @return List of {@link SentenceData09} Objects
	 */
	public List<SentenceData09> convert(String paragraph) {
		List<SentenceData09> toReturn = new ArrayList<SentenceData09>();

		String[] sentences = splitIntoSentences(paragraph);

		SentenceData09 sentenceData;

		for (String sentence : sentences) {
			sentenceData = new SentenceData09();

			ArrayList<String> forms = new ArrayList<String>();
			String[] tokens = tokenizeSentence(sentence);
			forms.add(CONLLReader09.ROOT);

			for (String token : tokens)
				forms.add(token);

			// TODO: das muss doch einfacher gehen!
			sentenceData.init(forms.toArray(new String[0]));
			sentenceData.createWithRoot(sentenceData); // erzeugt neues _leeres_
														// SD Objekt mit Wurzel
			sentenceData.forms = forms.toArray(new String[0]);
			toReturn.add(sentenceData);
		}

		return toReturn;
	}

	private String[] splitIntoSentences(String paragraph) {
		String[] sentences = null;

		SentenceDetectorME detector = new SentenceDetectorME(sentencemodel);
		sentences = detector.sentDetect(paragraph);

		return sentences;
	}

	private String[] tokenizeSentence(String sentence) {
		String tokens[] = null;

		Tokenizer tokenizer = new TokenizerME(tokenizeModel);
		tokens = tokenizer.tokenize(sentence);

		return tokens;
	}
}