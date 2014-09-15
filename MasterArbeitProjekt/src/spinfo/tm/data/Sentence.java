package spinfo.tm.data;

import java.io.Serializable;
import java.util.UUID;

//can be serialized and used in sorted sets
public class Sentence implements Serializable, Comparable<Sentence> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4118090561033143024L;

	private final UUID paragraphID;

	private final String[] tokens;
	private final String[] POSTags;
	private final String[] morphTags;
	private final String[] lemmas;
	private final String[] depLabels;
	private int[] heads;
	private int[] ids;
	
	private int positionInParagraph;

	/**
	 * @param tokens
	 *            - Tokens of the sentence (unmodified)
	 * @param POSTags
	 *            - Part of Speech Tags for tokens
	 * @param morphTags
	 *            - morphological Annotations for tokens
	 * @param lemmas
	 *            - lemmatized tokens *
	 * @param labels
	 *            - dependency label for token
	 * @param heads
	 *            - the tokens' heads (dependency parsed)
	 * @param parentID
	 *            - ID of the unit this sentence belongs to (i.e. paragraph)
	 * @param positionInParagraph
	 *            - the sentence's position in the higher unit (first, second
	 *            etc.)
	 */
	public Sentence(String[] tokens, String[] POSTags, String[] morphTags,
			String[] lemmas, String[] labels, int[] heads, UUID parentID,
			int positionInParagraph) {
		if (tokens.length != POSTags.length
				|| tokens.length != morphTags.length
				|| tokens.length != lemmas.length
				|| tokens.length != labels.length
				|| tokens.length != heads.length) {
			throw new IllegalArgumentException("Array lengths do not match");
		}
		this.tokens = tokens;
		this.lemmas = lemmas;
		this.POSTags = POSTags;
		this.morphTags = morphTags;
		this.paragraphID = parentID;
		this.positionInParagraph = positionInParagraph;
		this.heads = heads;
		this.depLabels = labels;
	}

	public UUID getParagraphID() {
		return paragraphID;
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

	public int[] getHeads() {
		return heads;
	}

	public String[] getDepLabels() {
		return depLabels;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int compareTo(Sentence other) {
		if (this.paragraphID.equals(other.paragraphID))
			return this.positionInParagraph - other.positionInParagraph;
		return this.paragraphID.compareTo(other.paragraphID);
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append(String.format(
				"Sentence from Paragraph %s (Sentence Nr.: %s)\n",
				paragraphID, positionInParagraph));
		for (int i = 0; i < tokens.length; i++) {
			out.append(String
					.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\n", i+1, tokens[i], lemmas[i],
							POSTags[i], morphTags[i], depLabels[i], heads[i]));
		}
		return out.toString();
	}
}