package spinfo.tm.extraction.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing an Information Extraction Template.
 * 
 * @author neumannm
 * 
 */
public class Template {

	private static int count;
	private Map<Class, List<SlotFiller>> content = new HashMap<>();
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
	 *            Content to be added
	 * @return true iff adding was successful
	 */
	public boolean addContent(SlotFiller f, Class c) {
		if (!content.containsKey(c) || content.get(c) == null) {
			content.put(c, new ArrayList<SlotFiller>());
		}
		return content.get(c).add(f);
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

	/**
	 * Get the template's content
	 * 
	 * @return SlotFillers
	 */
	public Map<Class, List<SlotFiller>> getContent() {
		return content;
	}

	/**
	 * @param c
	 * @return
	 */
	public List<SlotFiller> getContentForClass(Class c) {
		return content.get(c);
	}

	@Override
	public String toString() {
		return String.format("Template for jobAd with ID %s. Content: %s",
				jobAdID, content);
	}

	/**
	 * @return
	 */
	public int size() {
		return this.content.size();
	}
}