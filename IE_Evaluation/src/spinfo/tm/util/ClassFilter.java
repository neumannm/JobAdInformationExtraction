package spinfo.tm.util;

import java.util.ArrayList;
import java.util.List;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;

/**
 * (Positive) Filter for paragraphs according to Class(es) they belong to.
 * 
 * @author neumannm
 * 
 */
public class ClassFilter {

	/**
	 * Keep paragraphs that belong to a specific set of classes
	 * 
	 * @param unitsToClassify
	 *            list of all Classify Units
	 * @param classes
	 *            classes we are interested in
	 * @return List of paragraphs that belong to the given classes
	 */
	public static List<Paragraph> filter(List<Paragraph> unitsToClassify,
			Class... classes) {
		List<Paragraph> toReturn = new ArrayList<Paragraph>();

		for (Paragraph paragraph : unitsToClassify) {
			int classID = paragraph.getActualClassID();

			boolean keep = false;

			for (Class c : classes) {
				if (c.getId() == classID) {
					keep = true;
					break;
				} else {
					keep = false;
				}
			}
			if (keep)
				toReturn.add(paragraph);
		}
		return toReturn;
	}
}