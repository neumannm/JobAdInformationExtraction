package spinfo.tm.extraction.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing an Information Extraction Template.
 * 
 * @author neumannm
 * 
 */
public class Template {

	private static int count;
	private List<SlotFiller> content = new ArrayList<>();
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
	 * @return true iff adding was successful
	 */
	public boolean addContent(SlotFiller f) {
		return content.add(f);
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
	public List<SlotFiller> getContent() {
		return content;
	}

	@Override
	public String toString() {
		return String.format("Template for jobAd with ID %s. Content: %s",
				jobAdID, content);
	}

	public int size() {
		return this.content.size();
	}
}