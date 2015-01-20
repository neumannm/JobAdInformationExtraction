package spinfo.tm.extraction.data;

import java.io.Serializable;
import java.util.UUID;

/**
 * A potential slot filling anchor is a token that is potentially the central
 * element in a phrase that constitutes a Slot Filler.
 * 
 * @author neumannm
 * 
 */
public class PotentialSlotFillingAnchor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6620206581561070846L;

	private String token;
	private String POS;
	private String precedingToken = "--";
	private String precedingPOS = "--";
	private String followingToken = "--";
	private String followingPOS = "--";
	private UUID parentUUID;
	private int tokenPos;
	private boolean isCompetence;

	private boolean punctuationFollowing;
	private boolean hasSuffixOfInterest;
	private boolean startsWithUpperCase;

	/**
	 * Constructor.
	 * 
	 * @param token
	 *            token String
	 * @param position
	 *            position of this anchor in the higher unit (e.g. sentence or
	 *            paragraph)
	 * @param isCompetence
	 *            does this token denote a competence?
	 * @param paragraphID
	 *            ID of the paragraph this token belongs to
	 */
	public PotentialSlotFillingAnchor(String token, int position,
			boolean isCompetence, UUID paragraphID) {
		setToken(token);
		setHasSuffixOfInterest();
		setStartsWithUpperCase();
		setParentUUID(paragraphID);
		setTokenPos(position);
		setCompetence(isCompetence);
	}

	private void setParentUUID(UUID paragraphID) {
		this.parentUUID = paragraphID;
	}

	private void setTokenPos(int tokenPos) {
		this.tokenPos = tokenPos;
	}

	private void setToken(String token) {
		this.token = token;
	}

	/**
	 * Set if this token denotes a competence.
	 * 
	 * @param isCompetence
	 */
	public void setCompetence(boolean isCompetence) {
		this.isCompetence = isCompetence;
	}

	/**
	 * Set the POS tag of the following token.
	 * 
	 * @param followingPOS
	 */
	public void setFollowingPOS(String followingPOS) {
		this.followingPOS = followingPOS;
		setPunctuationFollowing();
	}

	/**
	 * Set the String content of the following token.
	 * 
	 * @param followingToken
	 */
	public void setFollowingToken(String followingToken) {
		this.followingToken = followingToken;
	}

	/**
	 * Set the POS tag of the preceding token.
	 * 
	 * @param precedingPOS
	 */
	public void setPrecedingPOS(String precedingPOS) {
		this.precedingPOS = precedingPOS;
	}

	/**
	 * Set the String content of the preceding token.
	 * 
	 * @param precedingToken
	 */
	public void setPrecedingToken(String precedingToken) {
		this.precedingToken = precedingToken;
	}

	/**
	 * Set this token's POS tag.
	 * 
	 * @param pOS
	 */
	public void setPOS(String pOS) {
		POS = pOS;
	}

	/**
	 * @return true iff this token denotes a competence
	 */
	public boolean isCompetence() {
		return isCompetence;
	}

	/**
	 * Get the token's String content.
	 * 
	 * @return the token's String content
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Get the token's POS tag.
	 * 
	 * @return the token's POS tag
	 */
	public String getPOS() {
		return POS;
	}

	/**
	 * Get the unique ID of the paragraph this token is contained in.
	 * 
	 * @return unique ID of the paragraph this token is contained in
	 */
	public UUID getParentUUID() {
		return parentUUID;
	}

	/**
	 * Get the token's position in the higher unit
	 * 
	 * @return token position
	 */
	public int getTokenPos() {
		return tokenPos;
	}

	/**
	 * Get the POS Tag of the following token.
	 * 
	 * @return POS tag of the following token
	 */
	public String getFollowingPOS() {
		return followingPOS;
	}

	/**
	 * Get the following token.
	 * 
	 * @return String content of the following token
	 */
	public String getFollowingToken() {
		return followingToken;
	}

	/**
	 * Get the POS Tag of the preceding token.
	 * 
	 * @return POS tag of the preceding token
	 */
	public String getPrecedingPOS() {
		return precedingPOS;
	}

	/**
	 * Get the preceding token.
	 * 
	 * @return String content of the preceding token
	 */
	public String getPrecedingToken() {
		return precedingToken;
	}

	/**
	 * Is the next token a punctuation mark?
	 * 
	 * @return true iff following token is a punctuation mark
	 */
	public boolean isPunctuationFollowing() {
		return this.punctuationFollowing;
	}

	private void setPunctuationFollowing() {
		String followingPOS = this.getFollowingPOS();
		if ("$,".equals(followingPOS) || "$.".equals(followingPOS))
			this.punctuationFollowing = true;
	}

	/**
	 * Has the token a specific suffix? Suffixes: -heit -keit -ung -nis -nisse
	 * -nissen -ig -lich
	 * 
	 * @return true iff the token has a specific suffix that is very likely to
	 *         signal a competence
	 */
	public boolean hasSuffixOfInterest() {
		return this.hasSuffixOfInterest;
	}

	private void setHasSuffixOfInterest() {
		if (token.matches("[A-ZÄÖÜ].*heit") || token.matches("[A-ZÄÖÜ].*keit")
				|| token.matches("[A-ZÄÖÜ].*ung")
				|| token.matches("[A-ZÄÖÜ].*schaft")
				|| token.matches("[A-ZÄÖÜ].*nis(sen?)?")
				|| token.matches("[a-zäöü].*ig")
				|| token.matches("[a-zäöü].*lich")) {
			this.hasSuffixOfInterest = true;
		}
	}

	/**
	 * Does this token start with an upper case letter?
	 * 
	 * @return true iff this token starts with an upper case letter
	 */
	public boolean startsWithUpperCase() {
		return this.startsWithUpperCase;
	}

	private void setStartsWithUpperCase() {
		if (token.matches("\\b[A-ZÄÖÜ].*")) {
			this.startsWithUpperCase = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Anchor '%s' (at %s) from CU %s", this.token,
				this.tokenPos, this.parentUUID);
	}

	/*
	 * does not work with my code... (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result
	// + ((parentUUID == null) ? 0 : parentUUID.toString().hashCode());
	// result = prime * result + ((token == null) ? 0 : token.hashCode());
	// return result;
	// }

	/*
	 * bisschen gehackt (Vergleich läuft nur über gleiches Token/POS und gleiche
	 * Paragraph ID, in der sich das Token befindet - es kann aber evtl. auch
	 * zweimal das gleiche Token im gleichen Paragraph auftreten...)
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
		if (!(obj instanceof PotentialSlotFillingAnchor))
			return false;
		PotentialSlotFillingAnchor other = (PotentialSlotFillingAnchor) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (parentUUID == null) {
			if (other.parentUUID != null)
				return false;
		} else if (!parentUUID.toString().equals(other.parentUUID.toString()))
			return false;
		return true;
	}
}