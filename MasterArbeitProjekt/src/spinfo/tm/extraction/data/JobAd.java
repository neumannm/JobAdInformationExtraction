package spinfo.tm.extraction.data;

import java.util.List;

import spinfo.tm.data.ClassifyUnit;

/**
 * Class representing a job advertisement.
 * 
 * @author neumannm
 * 
 */
public class JobAd {

	private Template template;
	private List<ClassifyUnit> classifyUnits;
	private String textContent;
	private int ID;

	/**
	 * Constructor - sets content of this Job Ad.
	 * 
	 * @param content
	 *            String content (=text) of Job Ad
	 */
	public JobAd(String content) {
		setTextContent(content);
	}

	/**
	 * Get classify units that belong to this Job Ad.
	 * 
	 * @return list of {@link ClassifyUnit}s
	 */
	public List<ClassifyUnit> getClassifyUnits() {
		return classifyUnits;
	}

	/**
	 * Get ID of this Job Ad.
	 * 
	 * @return ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Get the Job Ad's template
	 * 
	 * @return {@link Template}-Object
	 */
	public Template getTemplate() {
		return template;
	}

	private void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	/**
	 * Get textual content of this Job Ad.
	 * 
	 * @return String that holds the content
	 */
	public String getTextContent() {
		return textContent;
	}
}