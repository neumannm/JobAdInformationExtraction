package spinfo.tm.extraction.pattern;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.Class;

public class PatternMatcher {

	private Map<Pattern, Class> regExes;

	//TODO: rename method, change return type
	public void guessClasses(ClassifyUnit unitToClassify) {
		setupRegexes();

		String content = unitToClassify.getContent();
		
		match(content);	
		
	}

	private Map<Class, Integer> match(String input) {
		Map<Class, Integer> guesses = new TreeMap<>();
		Matcher m;
		for (Pattern pattern : regExes.keySet()) {
			int occurences = 0;
			m = pattern.matcher(input);
			while (m.find()) {
				// TODO: get matched substring of input
				occurences++;
			}
			guesses.put(regExes.get(pattern), occurences);
		}

		return guesses;
	}

	private void setupRegexes() {
		Pattern p;
		String lookahead, lookbehind;

		// Pattern p =
		// Pattern.compile("unternehmen|wir sind|sind wir|(wir )?über uns",
		// Pattern.CASE_INSENSITIVE);
		// regExes.put(p, Class.COMPANY_DESC);

		/*
		 * lists element ((?>\P{M}\p{M}*)+) = any number of graphemes
		 */
		lookbehind = "^(-*|-|*|\\d(\\.?\\)?)\\p{Blank}?"; // verschiedene
															// Zeichen die
		// Listenelemente einleiten
		p = Pattern.compile("(?<=" + lookbehind + ")(?>\\P{M}\\p{M}*)+$");
		regExes.put(p, Class.COMPANY_JOB); // class 2 and 3 (?)

		// p = Pattern.compile("\\bder(/die)? Bewerber(/?in)?\\b",
		// Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); // e.g.
		// // der/die
		// // Bewerber/in
		// regExes.put(p, Class.COMPETENCE);

		/*
		 * e.g. Bankkauffrau/-mann, Frisör/in
		 */
		p = Pattern.compile(
				"\\p{javaUpperCase}?\\p{javaLowerCase}+/-?\\p{javaLowerCase}+",
				Pattern.UNICODE_CASE);
		regExes.put(p, Class.JOB_DESC);

		p = Pattern.compile("\\(?m_w\\)?", Pattern.CASE_INSENSITIVE); // e.g.
																		// (m/w),
																		// m/w
		regExes.put(p, Class.JOB_DESC);

		/*
		 * 
		 */
		lookahead = "(ist|sind|wird|wäre(n)?)? (wünschenswert|erforderlich|vorausgesetzt|gewünscht)";
		p = Pattern.compile("(.+)(?=" + lookahead + ")");
		regExes.put(p, Class.COMPETENCE);

		/*
		 * 
		 */
		lookbehind = "(vorausgesetzt wird | Voraussetzung ist)";
		p = Pattern.compile("(?<=" + lookbehind + ")(.+)");
		regExes.put(p, Class.COMPETENCE);
		/*
		 * 
		 */
		lookahead = "(wird|werden) (vorausgesetzt|erwartet)";
		p = Pattern.compile("(.+)(?=" + lookahead + ")");
		regExes.put(p, Class.COMPETENCE);
		/*
		 * 
		 */
		lookbehind = "(wir setzen | setzen wir)";
		lookahead = "voraus\\.";
		p = Pattern.compile("(?<=" + lookbehind + ")(.+)(?=" + lookahead + ")");
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
				.compile("\\b\\\\p{javaUpperCase}\\p{javaLowerCase}+(heit|keit)\\b");
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
	}

	public Map<Pattern, Class> getRegExes() {
		return regExes;
	}
}