package spinfo.tm.extraction.data;

import java.io.Serializable;
import java.util.UUID;

/**
 * Slotfiller for an Information Extraction Template.
 * 
 * @author neumannm
 * 
 */
public class SlotFiller implements Serializable {

	private static final long serialVersionUID = -2889857331591898502L;

	private String content;
	private Class c;
	private UUID parentID;

	/**
	 * Constructor for a Slot Filler
	 * 
	 * @param token
	 *            Token for Slot Filler
	 * @param parentID
	 *            unique ID of the paragraph this SlotFiller is associated with
	 */
	public SlotFiller(String token, UUID parentID) {
		setContent(token);
		setParentID(parentID);
	}

	private void setParentID(UUID parentID) {
		this.parentID = parentID;
	}

	/**
	 * Set the slot filler's string content.
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Set the class this Filler belongs to.
	 * 
	 * @param c
	 */
	public void setC(Class c) {
		this.c = c;
	}

	/**
	 * Get the class this Filler belongs to.
	 * 
	 * @return class this Filler belongs to
	 */
	public Class getC() {
		return c;
	}

	/**
	 * Get ID of the filler's higher unit (i.e. paragraph)
	 * 
	 * @return ID of the filler's higher unit (i.e. paragraph)
	 */
	public UUID getParentID() {
		return parentID;
	}

	/**
	 * Get content of the Slot Filler
	 * 
	 * @return String content
	 */
	public String getContent() {
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.content + " (" + this.parentID + ")";
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
		result = prime * result
				+ ((parentID == null) ? 0 : parentID.hashCode());
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
		if (!(obj instanceof SlotFiller))
			return false;
		SlotFiller other = (SlotFiller) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.trim().equalsIgnoreCase(other.content.trim()))
			return false;
		if (parentID == null) {
			if (other.parentID != null)
				return false;
		} else if (!parentID.equals(other.parentID))
			return false;
		return true;
	}
}