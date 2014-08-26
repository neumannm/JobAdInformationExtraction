package spinfo.tm.extraction.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;

/*
 * Soll über RegExes die Kompetenzen aus den Paragraphen ziehen.
 * 
 * TODO: Ausfüllen der Templates
 * 
 * TODO: größere Matches gegenüber kleineren bevorzugen, wie lässt sich das hier umsetzen?
 * 
 * TODO: token position wird hier anders berechnet, nämlich als Zeichenposition!
 * 
 * Idee: man könnte prüfen welche Teile übereinstimmen und nur diese zurückgeben - 
 * Problem: übereinstimmender Teil könnte zu klein sein (aber besser als zu groß oder?)
 */
public class PatternMatcher {

	private Map<Pattern, Class> regExes;

	public PatternMatcher() {
		setupRegexes();
	}

	public List<SlotFiller> getContentOfInterest(ClassifyUnit unitToClassify,
			List<SlotFiller> list) {

		String content = unitToClassify.getContent();

		// List of matches for 1 classifyUnit:
		List<TokenPosPair> results = match(content);
		for (TokenPosPair result : results) {
			list.add(new SlotFiller(result.getToken(), result.getPosition()));
		}

		return list;
	}

	// TODO: prefer longer matches to shorter ones?
	private List<TokenPosPair> match(String input) {
		List<TokenPosPair> tokensAndPositions = new ArrayList<>();

		String token;
		int position;
		Matcher m;
		for (Pattern pattern : regExes.keySet()) {
			m = pattern.matcher(input);
			while (m.find()) {
				token = m.group();
				position = m.start();
				tokensAndPositions.add(new TokenPosPair(token, position));
				System.out.println(String.format(
						"Matched %s at position %s\n\twith Pattern %s", token,
						position, pattern.pattern()));
			}
		}
		return tokensAndPositions;
	}

	private void setupRegexes() {
		regExes = new HashMap<Pattern, Class>();

		Pattern p;
		String lookahead, lookbehind;

		/*
		 * lists element ((?>\P{M}\p{M}*)+) = any number of graphemes
		 */
		lookbehind = "^(-\\*|-|\\*|\\u2027|\\d(.?)?)\\p{Blank}?";
		p = Pattern.compile("(?<=" + lookbehind + ")(?=(\\P{M}\\p{M}*)+$).+",
				Pattern.MULTILINE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'der Bewerber sollte X haben' 'die Bewerberin sollte X mitbringen'
		 * 'der/die Bewerber/in sollte x sein'
		 */
		lookbehind = "\\b((der(/die)?)|die) Bewerber(/?in)? sollte";
		lookahead = "sein|mitbringen|haben";
		p = Pattern.compile("(?<=" + lookbehind + ") ([^.\\n]+?)(?="
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
		lookahead = "(ist|sind|wird|wäre(n)?) (wünschenswert|erforderlich|vorausgesetzt|gewünscht|erwartet)";
		p = Pattern.compile("([^.\\n]+?)(?=" + lookahead + ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * '... wird X erwartet' etc.
		 */
		lookbehind = "ist|sind|wird|wäre(n)?+";
		lookahead = "wünschenswert|erforderlich|vorausgesetzt|gewünscht|erwartet";
		p = Pattern.compile("(?<=" + lookbehind + ")([^.\\n]+?)(" + lookahead
				+ ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'erwartet wird X' etc.
		 */
		lookbehind = "(wünschenswert|erforderlich|vorausgesetzt|gewünscht|erwartet) (ist|sind|wird|wäre(n)?+)";
		p = Pattern.compile("(?<=" + lookbehind + ")([^.\\n]+)",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'vorausgesetzt wird X'
		 */
		lookbehind = "(vorausgesetzt wird |Voraussetzung ist |wir erwarten |wünschen (?:wir )?uns )";
		p = Pattern.compile("(?<=" + lookbehind + ")(.+?)(?=\\.)",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'wir setzen X voraus' | 'setzen wir X voraus'
		 */
		lookbehind = "(wir setzen | setzen wir )";
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
		p = Pattern.compile("(Führerschein|FS) (Klasse|Kl.)? .+?(?="
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
		 * 'erwarten wir'
		 * 
		 * lookbehind = ""; lookahead = ""; p = Pattern .compile("(?<=" +
		 * lookbehind + ")(.+?)(?=" + lookahead + ")"); // regExes.put(p,
		 * Class.COMPETENCE); // ...
		 */

		/*
		 * 'Sie verfügen über / verfügen Sie über ...'
		 */
		lookbehind = "(Sie verfügen über)|(verfügen Sie über)|(Sie sind)|(Sie haben)";
		p = Pattern.compile("(?<=" + lookbehind + ")[^\\.\\n]+",
				Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

	}

	public Map<Pattern, Class> getRegExes() {
		return regExes;
	}
}

class TokenPosPair {

	private int position;
	private String token;

	public TokenPosPair(String token, int position) {
		this.setToken(token);
		this.setPosition(position);
	}

	public int getPosition() {
		return position;
	}

	private void setPosition(int position) {
		this.position = position;
	}

	public String getToken() {
		return token;
	}

	private void setToken(String token) {
		this.token = token;
	}
}