package spinfo.tm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

public class SlotFillingAnchorGenerator {

	/*
	 * currently not used
	 */
	public static Map<Paragraph, List<PotentialSlotFillingAnchor>> generate(List<Paragraph> paragraphs) {
		Map<Paragraph, List<PotentialSlotFillingAnchor>> toReturn = new HashMap<Paragraph, List<PotentialSlotFillingAnchor>>();
		List<PotentialSlotFillingAnchor> anchors;
		for (Paragraph paragraph : paragraphs) {
			anchors = new ArrayList<PotentialSlotFillingAnchor>();
			Map<Integer, Sentence> sentenceData = paragraph.getSentenceData();
			Sentence sentence;
			for (Integer sentenceNo : sentenceData.keySet()) {
				sentence = sentenceData.get(sentenceNo);
				String[] tokens = sentence.getTokens();
				PotentialSlotFillingAnchor anchor;
				for (int i = 0; i < tokens.length; i++) {
					anchor = new PotentialSlotFillingAnchor(tokens[i], i, false, paragraph.getID());
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
			toReturn.put(paragraph, anchors);
		}
		return toReturn;		
	}

	public static Set<PotentialSlotFillingAnchor> generateAsSet(List<Paragraph> paragraphs) {
		Set<PotentialSlotFillingAnchor> toReturn = new HashSet<PotentialSlotFillingAnchor>();
		for (Paragraph paragraph : paragraphs) {
			Map<Integer, Sentence> sentenceData = paragraph.getSentenceData();
			Sentence sentence;
			for (Integer sentenceNo : sentenceData.keySet()) {
				sentence = sentenceData.get(sentenceNo);
				String[] tokens = sentence.getTokens();
				PotentialSlotFillingAnchor anchor;
				for (int i = 0; i < tokens.length; i++) {
					anchor = new PotentialSlotFillingAnchor(tokens[i], i, false, paragraph.getID());
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