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
import spinfo.tm.extraction.data.Template;

/*
 * Soll über RegExes die Kompetenzen aus den Paragraphen ziehen.
 * 
 * TODO: Ausfüllen der Templates
 * 
 * TODO: größere Matches gegenüber kleineren bevorzugen, wie lässt sich das hier umsetzen?
 */
public class PatternMatcher {

	private Map<Pattern, Class> regExes;

	public PatternMatcher() {
		setupRegexes();
	}

	// TODO: change return type
	public List<SlotFiller> getContentOfInterest(ClassifyUnit unitToClassify,
			Template template) {
		List<SlotFiller> toReturn = new ArrayList<SlotFiller>();

		String content = unitToClassify.getContent();

		// List of matches for 1 classifyUnit:
		List<TokenPosPair> results = match(content);
		for (TokenPosPair result : results) {
			toReturn.add(new SlotFiller(result.getToken(), result.getPosition()));
		}

		return toReturn;
	}

	// TODO: change return type?
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
			}
		}
		return tokensAndPositions;
	}

	private void setupRegexes() {
		regExes = new HashMap<Pattern, Class>();

		Pattern p;
		String lookahead, lookbehind;

		// Pattern p =
		// Pattern.compile("unternehmen|wir sind|sind wir|(wir )?über uns",
		// Pattern.CASE_INSENSITIVE);
		// regExes.put(p, Class.COMPANY_DESC);

		/*
		 * lists element ((?>\P{M}\p{M}*)+) = any number of graphemes
		 */
		lookbehind = "^(-\\*|-|\\*|\\u2027|\\d(.?)?)\\p{Blank}?";
		p = Pattern.compile("(?<=" + lookbehind + ")(?>\\P{M}\\p{M}*)+$", Pattern.MULTILINE);
		regExes.put(p, Class.COMPANY_JOB); // class 2 and 3 (?)

		/*
		 * 'der Bewerber sollte X haben' 'die Bewerberin sollte X mitbringen'
		 * 'der/die Bewerber/in sollte x sein'
		 */
		lookbehind = "\\bder(/die)? Bewerber(/?in)? sollte";
		lookahead = "sein|mitbringen|haben";
		p = Pattern.compile("(?<=" + lookbehind + ")(.+?)(?=" + lookahead + ")", Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * e.g. Bankkauffrau/-mann, Frisör/in
		 */
		p = Pattern.compile(
				"\\p{javaUpperCase}?\\p{javaLowerCase}+/-?\\p{javaLowerCase}+",
				Pattern.UNICODE_CASE);
		regExes.put(p, Class.JOB_DESC);

		/*
		 * 'X ist erforderlich'
		 * TODO: evtl. die Modifikatoren in den Match aufnehmen
		 */
		lookahead = "(ist|sind|wird|wäre(n)?)?(.*)(wünschenswert|erforderlich|vorausgesetzt|gewünscht)";
		p = Pattern.compile("(?<=\\.\\s?)(.+)(?=" + lookahead + ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 'vorausgesetzt wird X'
		 * TODO: evtl. die Modifikatoren in den Match aufnehmen
		 */
		lookbehind = "(vorausgesetzt wird | Voraussetzung ist )";
		p = Pattern.compile("(?<=" + lookbehind + ")(.+?)(?=\\.)", Pattern.CASE_INSENSITIVE);
		regExes.put(p, Class.COMPETENCE);

		/*
		 * TODO: evtl. die Modifikatoren in den Match aufnehmen
		 */
		lookahead = "(wird|werden) (vorausgesetzt|erwartet)";
		p = Pattern.compile("(?<=(\\.\\s?)|^)(.+?)(?=" + lookahead + ")");
		regExes.put(p, Class.COMPETENCE);
		
		/*
		 * TODO: evtl. die Modifikatoren in den Match aufnehmen
		 */
		lookbehind = "(wir setzen | setzen wir)";
		lookahead = "voraus\\.";
		p = Pattern.compile("(?<=" + lookbehind + ")(.+?)(?=" + lookahead + ")",
				Pattern.CASE_INSENSITIVE);
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
		// ...

		/*
		 * Führerschein
		 */
		// ...

		/*
		 * Berufsausbildung
		 */
		// ...
		
		/*
		 * 'erwarten wir' oder 'erwarten wir', 'wir wünschen (uns)'
		 */
		// ...
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