package spinfo.tm.preprocessing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPTokenizer {

	private SentenceModel sentencemodel;
	private TokenizerModel tokenizeModel;

	public OpenNLPTokenizer() {
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

	public String[] splitIntoSentences(String paragraph) {
		String[] sentences = null;

		SentenceDetectorME detector = new SentenceDetectorME(sentencemodel);
		sentences = detector.sentDetect(paragraph);

		return sentences;
	}

	public String[] tokenizeSentence(String sentence) {
		String tokens[] = null;

		Tokenizer tokenizer = new TokenizerME(tokenizeModel);
		tokens = tokenizer.tokenize(sentence);

		return tokens;
	}
}