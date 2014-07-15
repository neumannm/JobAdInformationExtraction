package spinfo.tm.extraction.data;

public class SlotFiller {

	private String content;
	private int position;

	public SlotFiller(String token, int pos) {
		this.content = token;
		this.position = pos;
	}

	public String getContent() {
		return content;
	}

	public int getPosition() {
		return position;
	}
}