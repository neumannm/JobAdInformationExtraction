package spinfo.tm.extraction.parsing;

import is2.data.SentenceData09;
import is2.io.CONLLReader09;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spinfo.tm.preprocessing.OpenNLPTokenizer;

/**
 * Class for conversion of a paragraph (String) to SentenceData in
 * CONLL09-Format for use with Mate Tools
 * 
 * @author neumannm
 * 
 */
public class Section2SentenceDataConverter {

	/**
	 * Converts the given paragraph into list of SentenceData. Paragraph is
	 * first split into sentences, which are then each tokenized and converted
	 * to SentenceData Objects.
	 * 
	 * @param section
	 *            Paragraph to be converted
	 * @return List of {@link SentenceData09} Objects
	 */
	public Map<Integer, SentenceData09> convert(String section) {
		Map<Integer, SentenceData09> toReturn = new HashMap<>();

		OpenNLPTokenizer splitter = new OpenNLPTokenizer();
		String[] sentences = splitter.splitIntoSentences(section);

		SentenceData09 sentenceData;

		for (int i = 0; i < sentences.length; i++) {
			
			sentenceData = new SentenceData09();

			ArrayList<String> forms = new ArrayList<String>();
			String[] tokens = splitter.tokenizeSentence(sentences[i]);
			forms.add(CONLLReader09.ROOT);

			for (String token : tokens)
				forms.add(token);

			sentenceData.init(forms.toArray(new String[0]));
			toReturn.put(i, sentenceData);
		}
		return toReturn;
	}
}