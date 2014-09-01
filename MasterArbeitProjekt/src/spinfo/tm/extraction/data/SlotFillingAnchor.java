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

	public SlotFillingAnchor(String token, int position, Class c,
			UUID classifyUnitID) {
		setC(c);
		setToken(token);
		setParentUUID(classifyUnitID);
		setTokenPos(position);
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
}
