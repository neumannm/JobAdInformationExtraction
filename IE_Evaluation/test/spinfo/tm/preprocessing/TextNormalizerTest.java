package spinfo.tm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import spinfo.tm.data.Paragraph;
import spinfo.tm.util.DataAccessor;

public class TextNormalizerTest {

	private List<Paragraph> paragraphs;

	@Before
	public void setUp() {
		paragraphs = DataAccessor.getFilteredCompetenceParagraphs();
	}

	@Test
	public void testNewLines() {
		System.out.println("Test start");
		for (Paragraph paragraph : paragraphs) {
			String content = paragraph.getContent().trim();
			if (content.contains("\n")) {
				System.out.println("CONTAINS NEWLINE:\n");
				System.out.println(content);
			}
			System.out.println("\n");
		}
	}

	@Test
	public void testListSymbols() {
		final char INVERTED_QUESTION_MARK = '\u00bf';
		final char HYPHENATION_POINT = '\u2027';
		final char MIDDLE_DOT = '\u00b7';
		final char BULLET = '\u2022';

		Pattern listPattern = Pattern.compile("^(-\\*|-|\\*|"
				+ HYPHENATION_POINT + "|" + MIDDLE_DOT + "|"
				+ INVERTED_QUESTION_MARK + "|" + BULLET
				+ "|\\d\\.?+\\)?+)\\p{Blank}?", Pattern.MULTILINE);
		Matcher listMatcher = listPattern.matcher("");

		List<String> noMatches = new ArrayList<>();

		for (Paragraph paragraph : paragraphs) {

			String content = paragraph.getContent();

			listMatcher.reset(content);
			if (listMatcher.find()) {
				String match = listMatcher.group();
				System.out.println("MATCH: " + match);
				System.out.println("\n");
			} else {
				noMatches.add(content);
			}
		}

		System.out.println(noMatches);
	}

	@Test
	public void testReplaceListSymbols() {
		final char INVERTED_QUESTION_MARK = '\u00bf';
		final char HYPHENATION_POINT = '\u2027';
		final char MIDDLE_DOT = '\u00b7';
		final char BULLET = '\u2022';

		Pattern listPattern = Pattern.compile("^(-\\*|-|\\*|"
				+ HYPHENATION_POINT + "|" + MIDDLE_DOT + "|"
				+ INVERTED_QUESTION_MARK + "|" + BULLET
				+ "|\\d\\.?+\\)?+)\\p{Blank}?", Pattern.MULTILINE);
		Matcher listMatcher = listPattern.matcher("");

		String replacement = "[*] ";

		for (Paragraph paragraph : paragraphs) {

			String content = paragraph.getContent();
			System.out.println("BEFORE:\n" + content);
			listMatcher.reset(content);

			StringBuffer contentSB = new StringBuffer();
			while (listMatcher.find()) {
				listMatcher.appendReplacement(contentSB, replacement);
			}
			listMatcher.appendTail(contentSB);

			System.out.println("AFTER:\n" + contentSB.toString());
			System.out.println("-------------------------------------------");
		}
	}
}
