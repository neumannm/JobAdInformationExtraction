package spinfo.tm.extraction.parsing.util;

import is2.data.SentenceData09;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import spinfo.tm.data.Sentence;

public class SentenceDataConverter {

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
