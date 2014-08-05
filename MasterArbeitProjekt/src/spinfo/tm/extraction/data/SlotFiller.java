package spinfo.tm.extraction.data;

/**
 * Slotfiller for an Information Extraction Template.
 * 
 * @author neumannm
 * 
 */
public class SlotFiller {

	private String content;
	private int position;

	/**
	 * Constructor for a Slot Filler
	 * 
	 * @param token
	 *            Token for Slot Filler
	 * @param pos
	 *            Position in Text
	 */
	public SlotFiller(String token, int pos) {
		this.content = token;
		this.position = pos;
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
	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return this.content + " (at " + this.position + ")";
	}
}