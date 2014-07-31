package spinfo.tm.extraction.parsing.util;

import is2.data.SentenceData09;
import is2.io.CONLLReader09;

import java.util.LinkedList;
import java.util.List;

/**
 * Reader for SentenceData in CONLL09-Format.
 * 
 * @author neumannm
 * 
 */
public class SentenceDataReader {

	/**
	 * Reads the Sentence Data from the file denoted by the given path.
	 * 
	 * @param path
	 *            path to file containing Sentence Data
	 * @return List of Sentence Data (one for each sentence)
	 */
	public static List<SentenceData09> readFromFile(String path) {
		List<SentenceData09> toReturn = new LinkedList<>();

		CONLLReader09 reader = new CONLLReader09(true);
		reader.startReading(path);

		SentenceData09 nextCoNLL09;
		while ((nextCoNLL09 = reader.getNextCoNLL09()) != null) {
			toReturn.add(nextCoNLL09);
		}

		return toReturn;
	}
}
