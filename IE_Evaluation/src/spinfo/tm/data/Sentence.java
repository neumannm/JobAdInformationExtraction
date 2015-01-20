package spinfo.tm.data;

import java.io.Serializable;
import java.util.UUID;

//can be serialized and used in sorted sets
/**
 * A sentence.
 * 
 * @author neumannm
 * 
 */
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

	private int positionInParagraph;

	/**
	 * Constructor.
	 * @param tokens
	 *            - Tokens of the sentence (unmodified)
	 * @param POSTags
	 *            - Part of Speech Tags for tokens
	 * @param morphTags
	 *            - morphological Annotations for tokens
	 * @param lemmas
	 *            - lemmatized tokens
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

	/**
	 * Get the unique ID of the paragraph this sentence belongs to.
	 * @return ID of the paragraph this sentence belongs to
	 */
	public UUID getParagraphID() {
		return paragraphID;
	}

	/**
	 * Get the sentence's lemmas.
	 * @return lemmas in this sentence
	 */
	public String[] getLemmas() {
		return lemmas;
	}

	/**
	 * Get the sentence's morphological information for each token.
	 * @return morphological tags for each token
	 */
	public String[] getMorphTags() {
		return morphTags;
	}

	/**
	 * Get the POS tag of each token in the sentence.
	 * @return part of speech tags for each token
	 */
	public String[] getPOSTags() {
		return POSTags;
	}

	/**
	 * Get the sentence's tokens.
	 * @return this sentence's tokens
	 */
	public String[] getTokens() {
		return tokens;
	}

	/**
	 * Get the position of this sentence in the paragraph (in terms of index).
	 * @return position of this sentence in the paragraph
	 */
	public int getPositionInParagraph() {
		return positionInParagraph;
	}

	/**
	 * Get the ID of the syntactical head for each token (according to dependency grammar).
	 * @return heads for each token
	 */
	public int[] getHeads() {
		return heads;
	}

	/**
	 * Get the type of dependency to the head of each token.
	 * @return dependency labels for each token
	 */
	public String[] getDepLabels() {
		return depLabels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Sentence other) {
		if (this.paragraphID.equals(other.paragraphID))
			return this.positionInParagraph - other.positionInParagraph;
		return this.paragraphID.compareTo(other.paragraphID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append(String.format(
				"Sentence from Paragraph %s (Sentence Nr.: %s)\n", paragraphID,
				positionInParagraph));
		for (int i = 0; i < tokens.length; i++) {
			out.append(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\n", i + 1,
					tokens[i], lemmas[i], POSTags[i], morphTags[i],
					depLabels[i], heads[i]));
		}
		return out.toString();
	}
}