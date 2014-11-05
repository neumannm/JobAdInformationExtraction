package spinfo.tm.extraction.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;

/**
 * For extracting content ({@link SlotFiller}) from @link{Paragraph}s by using
 * regular expressions (regex).
 * 
 * @author neumannm
 * 
 */
public class PatternMatcher {

	private Map<Pattern, Class> regExes;

	private static Logger logger = Logger.getLogger("PatternMatcher");

	/**
	 * Constructor. Sets up regexes to be used for extraction.
	 */
	public PatternMatcher() {
		setupRegexes();
	}

	/**
	 * Get phrases that are likely to denote competences.
	 * 
	 * @param paragraph
	 *            paragraph to be examined
	 * @return mapping between {@link SlotFiller} objects and the paragraph they
	 *         belong to
	 */
	public Map<SlotFiller, Pattern> getContentOfInterest(Paragraph paragraph) {
		Map<SlotFiller, Pattern> toReturn = new HashMap<>();
		String content = paragraph.getContent();

		// List of matches for 1 paragraph:
		Map<String, Pattern> results = match(content);
		for (String result : results.keySet()) {
			toReturn.put(new SlotFiller(result, paragraph.getID()),
					results.get(result));
		}
		return toReturn;
	}

	private Map<String, Pattern> match(String input) {
		Map<String, Pattern> matchedContent = new HashMap<>();

		String token;
		Matcher m;
		for (Pattern pattern : regExes.keySet()) {
			m = pattern.matcher(input);
			while (m.find()) {
				token = m.group();
				matchedContent.put(token.trim(), pattern);
				logger.info(String.format("Matched %s \n\twith Pattern %s",
						token, pattern.pattern()));
			}
		}
		return matchedContent;
	}

	private void setupRegexes() {
		regExes = new HashMap<Pattern, Class>();

		Pattern p;
		String lookahead, lookbehind;

		/*
		 * lists element ((?>\P{M}\p{M}*)+) = any number of graphemes
		 */
		lookbehind = "^\\[\\*\\] ";
		p = Pattern.compile("(?<=" + lookbehind + ")(?=(\\P{M}\\p{M}*)+$).+",
				Pattern.MULTILINE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'der Bewerber sollte X haben' 'die Bewerberin sollte X mitbringen'
		 * 'der/die Bewerber/in sollte x sein'
		 */
		lookbehind = "\\b((der(/die)?)|die) Bewerber(/?in)? sollten?";
		lookahead = "sein|mitbringen|haben";
		p = Pattern.compile("(?<=" + lookbehind + ") ([^.\\n]+?)(?=\\b"
				+ lookahead + ")", Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * e.g. Bankkauffrau/-mann, Frisör/in
		 */
		p = Pattern.compile(
				"\\p{javaUpperCase}\\p{javaLowerCase}+/-?\\p{javaLowerCase}+",
				Pattern.UNICODE_CASE);
		regExes.put(p, Class.JOB_DESC);

		/*
		 * 'X ist erforderlich'
		 */
		lookahead = "(ist|sind|wird|werden|wäre(n)?) (wünschenswert|erforderlich|vorausgesetzt|gewünscht|erwartet)";
		p = Pattern.compile("([^.\\n]+?)(?=\\b" + lookahead + ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * '... wird X erwartet' etc.
		 */
		lookbehind = "ist|sind|wird|werden|wäre(n)?+";
		lookahead = "wünschenswert|erforderlich|vorausgesetzt|gewünscht|erwartet";
		p = Pattern.compile("(?<=\\b" + lookbehind + ")([^.\\n]+?)("
				+ lookahead + ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'erwartet wird X' etc.
		 */
		lookbehind = "(wünschenswert|erforderlich|vorausgesetzt|voraussetzung|gewünscht|erwartet) (ist|sind|wird|wäre(n)?+)\\b";
		p = Pattern.compile("(?<=" + lookbehind + ")([^.\\n]+)",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'vorausgesetzt wird X'
		 */
		lookbehind = "((wir erwarten )|(wünschen (?:wir )?uns ))\\b";
		p = Pattern.compile("(?<=" + lookbehind + ")(.+?)(?=\\.)",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'wir setzen X voraus' | 'setzen wir X voraus'
		 */
		lookbehind = "(wir setzen|setzen wir)\\b";
		lookahead = "voraus\\.";
		p = Pattern.compile("(?<=" + lookbehind + ")([^.\\n]+?)(?=" + lookahead
				+ ")", Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * Datumsangabe
		 */
		p = Pattern
				.compile("(0[1-9]|[12][0-9]|3[01])([- /.])(0?[1-9]|1[012])\\2(19|20)\\d\\d");
		regExes.put(p, Class.OTHER);

		/*
		 * Eigenschaften, die auf -heit oder -keit enden
		 */
		p = Pattern
				.compile("\\b\\p{javaUpperCase}\\p{javaLowerCase}+(heit|keit)\\b");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * Kenntnisse, Erfahrungen von/in etwas
		 */
		lookahead = "\\.|\\n"; // bis zum Satzende oder Zeilenumbruch
		p = Pattern
				.compile("(Kenntnis(se)?|Erfahrung(en)?) (von|in|im|der|des) .+?(?="
						+ lookahead + ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * Führerschein
		 */
		lookahead = "\\.|\\n"; // bis zum Satzende oder Zeilenumbruch
		p = Pattern.compile("(Führerschein|FS) (Klasse |Kl.)?+.+?(?="
				+ lookahead + ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * Berufsausbildung (1)
		 */
		lookbehind = "";
		lookahead = "";
		p = Pattern.compile(
				"(?=(berufs)?ausbildung)[^.\\n]+(?=abgeschlossen\\.)",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * Berufsausbildung (2)
		 */
		lookbehind = "";
		lookahead = "";
		p = Pattern.compile("(?=abgeschlossene (berufs)?ausbildung)[^.\\n]+",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'Sie verfügen über / verfügen Sie über ...'
		 */
		lookbehind = "((Sie verfügen|verfügen Sie) über)|(Sie (sind|haben))";
		p = Pattern.compile("(?<=" + lookbehind + ")[^\\.\\n]+",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

	}

	/**
	 * Get all regexes used by this class.
	 * 
	 * @return map of all defined RegExes and their classes
	 */
	public Map<Pattern, Class> getRegExes() {
		return regExes;
	}
}