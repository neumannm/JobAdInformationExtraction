package spinfo.tm.extraction.data;

import java.util.UUID;

/**
 * Slotfiller for an Information Extraction Template.
 * 
 * @author neumannm
 * 
 */
public class SlotFiller {

	private String content;
	private UUID classifyUnitID;

	/**
	 * Constructor for a Slot Filler
	 * 
	 * @param token
	 *            Token for Slot Filler
	 * @param cuID
	 *            Position in Text
	 */
	public SlotFiller(String token, UUID cuID) {
		this.content = token;
		this.classifyUnitID = cuID;
	}

	/**
	 * Get content of the Slot Filler
	 * 
	 * @return String content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Get position of the Slot Filler in higher unit.
	 * 
	 * @return position
	 */
	public UUID getClassifyUnitID() {
		return classifyUnitID;
	}

	@Override
	public String toString() {
		return this.content + " (at " + this.classifyUnitID + ")";
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
					&& this.classifyUnitID.equals(other.classifyUnitID);
		}
		return super.equals(obj);
	}
}