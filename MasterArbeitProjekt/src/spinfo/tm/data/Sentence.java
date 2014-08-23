package spinfo.tm.data;

import java.io.Serializable;
import java.util.UUID;

public class Sentence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4118090561033143024L;

	private final UUID classifyUnitID;

	private final String[] tokens;
	private final String[] POSTags;
	private final String[] morphTags;
	private final String[] lemmas;

	private int positionInParagraph;

	public Sentence(String[] tokens, String[] POSTags, String[] morphTags,
			String[] lemmas, UUID parentID, int positionInParagraph) {
		this.tokens = tokens;
		this.lemmas = lemmas;
		this.POSTags = POSTags;
		this.morphTags = morphTags;
		this.classifyUnitID = parentID;
		this.positionInParagraph = positionInParagraph;
	}

	public UUID getClassifyUnitID() {
		return classifyUnitID;
	}

	public String[] getLemmas() {
		return lemmas;
	}

	public String[] getMorphTags() {
		return morphTags;
	}

	public String[] getPOSTags() {
		return POSTags;
	}

	public String[] getTokens() {
		return tokens;
	}

	public int getPositionInParagraph() {
		return positionInParagraph;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}