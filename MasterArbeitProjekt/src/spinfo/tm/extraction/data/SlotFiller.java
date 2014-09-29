package spinfo.tm.extraction.data;

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

	/**
	 * Constructor for a Slot Filler
	 * 
	 * @param token
	 *            Token for Slot Filler
	 */
	public SlotFiller(String token, Class c) {
		setContent(token);
		setC(c);
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
		return this.content + "(" + this.c + ")";
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
					&& this.c.equals(other.c);
		}
		return super.equals(obj);
	}
}