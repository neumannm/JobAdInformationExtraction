package spinfo.tm.extraction.data;

import java.io.Serializable;
import java.util.UUID;

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

	public void setCompetence(boolean isCompetence) {
		this.isCompetence = isCompetence;
	}

	public void setFollowingPOS(String followingPOS) {
		this.followingPOS = followingPOS;
		setPunctuationFollowing();
	}

	public void setFollowingToken(String followingToken) {
		this.followingToken = followingToken;
	}

	public void setPrecedingPOS(String precedingPOS) {
		this.precedingPOS = precedingPOS;
	}

	public void setPrecedingToken(String precedingToken) {
		this.precedingToken = precedingToken;
	}

	public void setPOS(String pOS) {
		POS = pOS;
	}

	public boolean isCompetence() {
		return isCompetence;
	}

	public String getToken() {
		return token;
	}

	public String getPOS() {
		return POS;
	}

	public UUID getParentUUID() {
		return parentUUID;
	}

	public int getTokenPos() {
		return tokenPos;
	}

	public String getFollowingPOS() {
		return followingPOS;
	}

	public String getFollowingToken() {
		return followingToken;
	}

	public String getPrecedingPOS() {
		return precedingPOS;
	}

	public String getPrecedingToken() {
		return precedingToken;
	}

	public boolean isPunctuationFollowing() {
		return this.punctuationFollowing;
	}

	private void setPunctuationFollowing() {
		String followingPOS = this.getFollowingPOS();
		if ("$,".equals(followingPOS) || "$.".equals(followingPOS))
			this.punctuationFollowing = true;
	}

	public boolean hasSuffixOfInterest() {
		return this.hasSuffixOfInterest;
	}

	private void setHasSuffixOfInterest() {
		if (token.matches("[A-ZÄÖÜ].*heit") 
				|| token.matches("[A-ZÄÖÜ].*keit")
				|| token.matches("[A-ZÄÖÜ].*ung")
				|| token.matches("[A-ZÄÖÜ].*schaft")
				|| token.matches("[A-ZÄÖÜ].*nis(sen?)?")
				|| token.matches("[a-zäöü].*ig")
				|| token.matches("[a-zäöü].*lich")) {
			this.hasSuffixOfInterest = true;
		}
	}

	public boolean startsWithUpperCase() {
		return this.startsWithUpperCase;
	}

	private void setStartsWithUpperCase() {
		if (token.matches("\\b[A-ZÄÖÜ].*")) {
			this.startsWithUpperCase = true;
		}
	}

	@Override
	public String toString() {
		return String.format("Anchor '%s' (at %s) from CU %s", this.token,
				this.tokenPos, this.parentUUID);
	}

	/*
	 * bisschen gehackt (Vergleich läuft nur über gleiches Token und gleiche
	 * Section ID, in der sich das Token befindet - es kann aber evtl. auch
	 * zweimal das gleiche Token in der gleichen Section auftrete n...)
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PotentialSlotFillingAnchor) {
			PotentialSlotFillingAnchor other = (PotentialSlotFillingAnchor) obj;
			return this.token.equals(other.token)
					&& this.parentUUID.toString().equals(
							other.parentUUID.toString());
		}
		return false;
	}
}
