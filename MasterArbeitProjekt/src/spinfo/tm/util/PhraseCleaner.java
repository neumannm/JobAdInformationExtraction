package spinfo.tm.util;

public class PhraseCleaner {

	public static String removeUnneccessaryWhitespace(String s){
		return s.replaceAll("\\s(?=[.,;:?!\\_\"'*\\-\\)\\s])", "").replaceAll("(?<=[\\(\\_\\s])\\s", "").trim();
	}
}