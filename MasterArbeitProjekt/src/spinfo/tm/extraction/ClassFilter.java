package spinfo.tm.extraction;

import java.util.ArrayList;
import java.util.List;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.Class;

/**
 * (Positive) Filter for ClassifyUnits according to Class(es) they belong to.
 * 
 * @author neumannm
 * 
 */
public class ClassFilter {

	/**
	 * Keep classifyUnits that belong to a specific set of classes
	 * 
	 * @param unitsToClassify
	 *            list of all Classify Units
	 * @param classes
	 *            classes we are interested in
	 * @return List of classifyUnits that belong to the given classes
	 */
	public static List<ClassifyUnit> filter(List<ClassifyUnit> unitsToClassify,
			Class... classes) {
		List<ClassifyUnit> toReturn = new ArrayList<ClassifyUnit>();

		for (ClassifyUnit classifyUnit : unitsToClassify) {
			int classID = classifyUnit.getActualClassID();

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
				toReturn.add(classifyUnit);
		}
		return toReturn;
	}
}