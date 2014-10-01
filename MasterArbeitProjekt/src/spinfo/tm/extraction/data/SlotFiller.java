package spinfo.tm.extraction.data;

import java.util.UUID;

/**
 * Slotfiller for an Information Extraction Template.
 * 
 * @author neumannm
 * 
 */
/*
 * TODO Kopplung an Paragraph oder JobAd?
 */
public class SlotFiller {

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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SlotFiller) {
			SlotFiller other = (SlotFiller) obj;
			return this.content.trim().equalsIgnoreCase(other.content.trim())
					&& this.parentID.equals(other.parentID);
		}
		return super.equals(obj);
	}
}