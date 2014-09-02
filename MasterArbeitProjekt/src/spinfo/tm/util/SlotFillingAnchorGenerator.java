package spinfo.tm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spinfo.tm.data.Section;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.SlotFillingAnchor;

public class SlotFillingAnchorGenerator {

	public static Map<Section, List<SlotFillingAnchor>> generate(List<Section> sections) {
		Map<Section, List<SlotFillingAnchor>> toReturn = new HashMap<Section, List<SlotFillingAnchor>>();
		List<SlotFillingAnchor> anchors;
		for (Section section : sections) {
			anchors = new ArrayList<SlotFillingAnchor>();
			Map<Integer, Sentence> sentenceData = section.getSentenceData();
			Sentence sentence;
			for (Integer sentenceNo : sentenceData.keySet()) {
				sentence = sentenceData.get(sentenceNo);
				String[] tokens = sentence.getTokens();
				SlotFillingAnchor anchor;
				for (int i = 0; i < tokens.length; i++) {
					anchor = new SlotFillingAnchor(tokens[i], i, false, section.getID());
					anchor.setPOS(sentence.getPOSTags()[i]);
					if(i>0){
						anchor.setPrecedingPOS(sentence.getPOSTags()[i-1]);
						anchor.setPrecedingToken(sentence.getTokens()[i-1]);
					}
					if(i<tokens.length-1){
						anchor.setFollowingPOS(sentence.getPOSTags()[i+1]);
						anchor.setFollowingToken(sentence.getTokens()[i+1]);
					}
					anchors.add(anchor);
				}
			}
			toReturn.put(section, anchors);
		}
		return toReturn;		
	}

	public static Set<SlotFillingAnchor> generateAsSet(List<Section> sections) {
		Set<SlotFillingAnchor> toReturn = new HashSet<SlotFillingAnchor>();
		for (Section section : sections) {
			Map<Integer, Sentence> sentenceData = section.getSentenceData();
			Sentence sentence;
			for (Integer sentenceNo : sentenceData.keySet()) {
				sentence = sentenceData.get(sentenceNo);
				String[] tokens = sentence.getTokens();
				SlotFillingAnchor anchor;
				for (int i = 0; i < tokens.length; i++) {
					anchor = new SlotFillingAnchor(tokens[i], i, false, section.getID());
					anchor.setPOS(sentence.getPOSTags()[i]);
					if(i>0){
						anchor.setPrecedingPOS(sentence.getPOSTags()[i-1]);
						anchor.setPrecedingToken(sentence.getTokens()[i-1]);
					}
					if(i<tokens.length-1){
						anchor.setFollowingPOS(sentence.getPOSTags()[i+1]);
						anchor.setFollowingToken(sentence.getTokens()[i+1]);
					}
					toReturn.add(anchor);
				}
			}
		}
		return toReturn;	
	}
}