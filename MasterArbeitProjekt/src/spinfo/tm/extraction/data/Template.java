package spinfo.tm.extraction.data;

import java.util.Map;
import java.util.TreeMap;

public class Template {

	private static int count;
	private Map<Class, SlotFiller> content = new TreeMap<>();
	private int jobAdID;
	private int id;

	public Template(int parentID) {
		this.setParentID(parentID);
		this.setID(++count);
	}

	public SlotFiller addContent(Class c, SlotFiller f) {
		return content.put(c, f);
	}

	public SlotFiller getContentForClass(Class c) {
		return content.get(c);
	}

	public void setParentID(int parentID) {
		this.jobAdID = parentID;
	}

	public int getParentID() {
		return jobAdID;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("Template for jobAd with ID %s. Content: %s",
				jobAdID, content);
	}
}