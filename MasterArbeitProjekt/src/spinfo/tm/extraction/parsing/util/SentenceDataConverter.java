package spinfo.tm.extraction.parsing.util;

import is2.data.SentenceData09;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import spinfo.tm.data.Sentence;

/**
 * Helper class for conversion of the MATE specific parsing data into own
 * format.
 * 
 * @author neumannm
 * 
 */
public class SentenceDataConverter {

	/**
	 * Converts MATE specific parsing data into own format. (see
	 * {@link Sentence})
	 * 
	 * @param processed
	 *            sentences processed by mate tools
	 * @param parentID
	 *            unique ID of the paragraph
	 * @return mapping of sentence number to sentence data
	 */
	public static Map<Integer, Sentence> convert(
			Map<Integer, SentenceData09> processed, UUID parentID) {

		Map<Integer, Sentence> converted = new TreeMap<>();
		Sentence s;
		SentenceData09 sd;
		for (Integer sentenceNo : processed.keySet()) {
			sd = processed.get(sentenceNo);

			s = new Sentence(sd.forms, sd.ppos, sd.pfeats, sd.plemmas,
					sd.plabels, sd.pheads, parentID, sentenceNo);

			converted.put(sentenceNo, s);
		}

		return converted;
	}

}
