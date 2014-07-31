package spinfo.tm.extraction.data;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class representing an Information Extraction Template.
 * 
 * @author mandy
 * 
 */
public class Template {

	private static int count;
	private Map<Class, SlotFiller> content = new TreeMap<>();
	private int jobAdID;
	private int id;

	/**
	 * Constructor - sets reference to higher unit (= the Template's parent)
	 * 
	 * @param parentID
	 *            ID of the higher unit (Job Ad)
	 */
	public Template(int parentID) {
		this.setParentID(parentID);
		this.setID(++count);
	}

	/**
	 * Add content to the template.
	 * 
	 * @param c
	 *            Class of content to be added
	 * @param f
	 *            Conent to be added
	 * @return updated instance of Template
	 */
	public SlotFiller addContent(Class c, SlotFiller f) {
		return content.put(c, f);
	}

	/**
	 * Get all content that belongs to a specific class.
	 * 
	 * @param c
	 *            Class
	 * @return SlotFiller associated with this class
	 */
	public SlotFiller getContentForClass(Class c) {
		return content.get(c);
	}

	private void setParentID(int parentID) {
		this.jobAdID = parentID;
	}

	/**
	 * Get the ID of the template's parent.
	 * 
	 * @return parent ID
	 */
	public int getParentID() {
		return jobAdID;
	}

	private void setID(int id) {
		this.id = id;
	}

	/**
	 * Get the template's ID
	 * 
	 * @return template ID
	 */
	public int getID() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("Template for jobAd with ID %s. Content: %s",
				jobAdID, content);
	}
}