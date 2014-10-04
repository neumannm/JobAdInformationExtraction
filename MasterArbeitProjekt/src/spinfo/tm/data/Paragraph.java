package spinfo.tm.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a paragraph in a Job Ad (typically a paragraph)
 * 
 * @author neumannm
 * 
 */
public class Paragraph implements Comparable<Paragraph>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -753179063671511501L;

	private String content;
	private int parentID;
	private int classID;
	private UUID id;
	private List<String> featureUnits;
	private double[] featureVector;

	private Map<Integer, Sentence> sentences;

	// private Set<Sentence> sentences;

	/**
	 * Create a new paragraph with specified ID.
	 * 
	 * @param content
	 *            - The paragraph's textual content.
	 * @param parentID
	 *            - ID of the paragraph's parent (i.e. job ad)
	 * @param id
	 *            - unique ID of this paragraph
	 */
	public Paragraph(String content, int parentID, UUID id) {
		super();
		this.content = content;
		this.parentID = parentID;
		this.id = id;
	}

	/**
	 * Create a paragraph with random ID.
	 * 
	 * @param content
	 *            - The paragraph's textual content.
	 * @param parentID
	 *            - ID of the paragraph's parent (i.e. job ad)
	 */
	public Paragraph(String content, int parentID) {
		this(content, parentID, UUID.randomUUID());
	}

	/**
	 * @return
	 */
	public List<String> getFeatureUnits() {
		return featureUnits;
	}

	/**
	 * @param featureUnits
	 */
	public void setFeatureUnits(List<String> featureUnits) {
		this.featureUnits = featureUnits;
	}

	/**
	 * @return
	 */
	public double[] getFeatureVector() {
		return featureVector;
	}

	/**
	 * @param featureVector
	 */
	public void setFeatureVector(double[] featureVector) {
		this.featureVector = featureVector;
	}

	/**
	 * @return
	 */
	public UUID getID() {
		return id;
	}

	/**
	 * @return
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return
	 */
	public int getParentID() {
		return parentID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return parentID + "\t" + classID + "\t" + content + "\n";
	}

	/**
	 * @return
	 */
	public int getActualClassID() {
		return classID;
	}

	/**
	 * @param classID
	 */
	public void setActualClassID(int classID) {
		this.classID = classID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Paragraph other) {
		return this.id.compareTo(other.id);
	}

	/**
	 * @return mapping of sentence position to sentence data
	 */
	public Map<Integer, Sentence> getSentenceData() {
		return this.sentences;
	}

	/**
	 * @param data
	 */
	public void setSentenceData(Map<Integer, Sentence> data) {
		this.sentences = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Paragraph))
			return false;
		Paragraph other = (Paragraph) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}