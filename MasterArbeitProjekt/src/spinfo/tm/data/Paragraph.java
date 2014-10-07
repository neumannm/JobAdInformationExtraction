package spinfo.tm.data;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a paragraph in a Job Ad
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

	private Map<Integer, Sentence> sentences;

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
		this.content = content.trim();
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
	 * @return this paragraph's unique ID
	 */
	public UUID getID() {
		return id;
	}

	/**
	 * @return textual content of the paragraph
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return ID of the parent of this paragraph, i.e. of the job ad it belongs to
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
	 * @return class ID this paragraph definitively belongs to (determined through classification)
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
	 * set the sentence data
	 * @param data sentence data = parse result for each sentence
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