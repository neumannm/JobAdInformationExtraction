package spinfo.tm.extraction.data;


/**
 * Slotfiller for an Information Extraction Template.
 * 
 * @author neumannm
 * 
 */
public class SlotFiller {

	private String content;
	private int tokenPos;
	private Class c;

	/**
	 * Constructor for a Slot Filler
	 * 
	 * @param token
	 *            Token for Slot Filler
	 * @param tokenPos
	 *            position of the filler in the paragraph
	 */
	public SlotFiller(String token, int tokenPos) {
		this.content = token;
		this.tokenPos = tokenPos;
	}

	public void setC(Class c) {
		this.c = c;
	}

	public Class getC() {
		return c;
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
	public int getTokenPosition() {
		return tokenPos;
	}

	@Override
	public String toString() {
		return this.content + " (at " + this.tokenPos + ")";
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
					&& this.tokenPos == other.tokenPos;
		}
		return super.equals(obj);
	}
}