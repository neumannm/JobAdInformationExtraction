package spinfo.tm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to clean strings, i.e. remove unnecessary whitespace,
 * standardise list symbols etc.
 * 
 * @author neumannm
 * 
 */
public class TextCleaner {

	/**
	 * Remove whitespace before punctuation marks, after parantheses etc.
	 * 
	 * @param s
	 *            string to be cleaned
	 * @return clean string
	 */
	public static String removeUnneccessaryWhitespace(String s) {
		return s.replaceAll("\\s(?=[.,;:?!\"'*\\)\\s])", "")
				.replaceAll("(?<=[\\(\\s])\\s", "").trim();
	}

	/**
	 * Reduce all symbols that may start a list item to a unique symbol.
	 * 
	 * @param s
	 *            string to be processed
	 * @return string with standardised list symbols
	 */
	public static String normalizeListSymbols(String s) {
		final char INVERTED_QUESTION_MARK = '\u00bf';
		final char HYPHENATION_POINT = '\u2027';
		final char MIDDLE_DOT = '\u00b7';
		final char BULLET = '\u2022';

		Pattern listPattern = Pattern.compile("^(-\\*|-|\\*|"
				+ HYPHENATION_POINT + "|" + MIDDLE_DOT + "|"
				+ INVERTED_QUESTION_MARK + "|" + BULLET
				+ "|\\d\\.?+\\)?+)\\p{Blank}?", Pattern.MULTILINE);
		Matcher listMatcher = listPattern.matcher(s);

		String replacement = "[*] ";

		StringBuffer contentSB = new StringBuffer();
		while (listMatcher.find()) {
			listMatcher.appendReplacement(contentSB, replacement);
		}
		listMatcher.appendTail(contentSB);

		return contentSB.toString().trim();
	}
}