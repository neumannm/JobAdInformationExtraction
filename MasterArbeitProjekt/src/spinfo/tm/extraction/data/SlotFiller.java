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

	/**
	 * 
	 */
	private static final long serialVersionUID = -2889857331591898502L;

	private String content;
	private Class c;
	private UUID parentID;

	/**
	 * Constructor for a Slot Filler
	 * 
	 * @param token
	 *            Token for Slot Filler
	 */
	public SlotFiller(String token, UUID parentID) {
		setContent(token);
		setParentID(parentID);
	}

	private void setParentID(UUID parentID) {
		this.parentID = parentID;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setC(Class c) {
		this.c = c;
	}

	public Class getC() {
		return c;
	}

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