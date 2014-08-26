package spinfo.tm.extraction.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SlotFillingAnchor {

	private String token;
	private List<Map<String, String>> precedingContext; // String1: token,
														// String2:POSTag
	private List<Map<String, String>> followingContext; // String1: token,
														// String2:POSTag
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

	public UUID getParentUUID() {
		return parentUUID;
	}

	public int getTokenPos() {
		return tokenPos;
	}

	public List<Map<String, String>> getFollowingContext() {
		return followingContext;
	}

	public List<Map<String, String>> getPrecedingContext() {
		return precedingContext;
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
