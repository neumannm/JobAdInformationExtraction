package spinfo.tm.extraction.data;

import java.util.List;

import spinfo.tm.data.ClassifyUnit;

/**
 * Class representing a job advertisement.
 * @author neumannm
 *
 */
public class JobAd {

	private Template template;
	private List<ClassifyUnit> classifyUnits;
	private String textContent;
	private int ID;

	public JobAd(String content) {
		setTextContent(content);
	}

	public void setClassifyUnits(List<ClassifyUnit> classifyUnits) {
		this.classifyUnits = classifyUnits;
	}

	public List<ClassifyUnit> getClassifyUnits() {
		return classifyUnits;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getID() {
		return ID;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getTextContent() {
		return textContent;
	}
}