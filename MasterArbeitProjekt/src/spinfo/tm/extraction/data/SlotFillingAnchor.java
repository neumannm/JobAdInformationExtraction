package spinfo.tm.extraction.data;

import java.util.UUID;

public class SlotFillingAnchor {

	private String token;
	private String POS;
	private String precedingToken;
	private String precedingPOS;
	private String followingToken;
	private String followingPOS;
	private Class c;
	private UUID parentUUID;
	private int tokenPos;
	private boolean isCompetence;

	public SlotFillingAnchor(String token, int position, boolean isCompetence,
			UUID sectionID) {
		setC(c);
		setToken(token);
		setParentUUID(sectionID);
		setTokenPos(position);
		setCompetence(isCompetence);
	}

	private void setParentUUID(UUID classifyUnitID) {
		this.parentUUID = classifyUnitID;
	}

	private void setTokenPos(int tokenPos) {
		this.tokenPos = tokenPos;
	}

	private void setC(Class c) {
		this.c = c;
	}

	private void setToken(String token) {
		this.token = token;
	}

	public void setCompetence(boolean isCompetence) {
		this.isCompetence = isCompetence;
	}

	public void setFollowingPOS(String followingPOS) {
		this.followingPOS = followingPOS;
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

	public Class getC() {
		return this.c;
	}

	@Override
	public String toString() {
		return String.format("Anchor '%s' (at %s) from CU %s", this.token,
				this.tokenPos, this.parentUUID);
	}
	
	
	/* bisschen gehackt
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SlotFillingAnchor){
			SlotFillingAnchor other = (SlotFillingAnchor) obj;
			return this.token.equals(other.token) && this.parentUUID.toString().equals(other.parentUUID.toString());
		}
		return false;
	}
}
