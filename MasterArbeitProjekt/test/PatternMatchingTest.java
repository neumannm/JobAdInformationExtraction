import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.IETrainingDataGenerator;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.JobAd;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.pattern.PatternMatcher;
import spinfo.tm.preprocessing.TrainingDataGenerator;
import spinfo.tm.util.UniversalMapper;

public class PatternMatchingTest {
	private List<ClassifyUnit> paragraphs = new ArrayList<ClassifyUnit>();
	private Map<UUID, ClassifyUnit> classifyUnits = new HashMap<UUID, ClassifyUnit>();
	private Map<ClassifyUnit, JobAd> map;

	private void setUp() throws IOException {
		File trainingDataFile = new File(
				"data/SingleClassTrainingDataFiltered.csv");
		/* Training data generation */
		TrainingDataGenerator tdg = new TrainingDataGenerator(trainingDataFile);

		paragraphs = tdg.getTrainingData();
		System.out.println("Anzahl ClassifyUnits insgesamt: "
				+ paragraphs.size());

		for (ClassifyUnit cu : paragraphs) {
			classifyUnits.put(cu.getID(), cu);
		}

		map = UniversalMapper.map(paragraphs);
	}

	@Test
	public void testSimple() {
		String input = "Bitte senden Sie Ihre Bewerbung bis zum 31.03.2014 ein.";
		/* regulärer Ausdruck für Datumsangaben: */
		Pattern p = Pattern
				.compile("(0[1-9]|[12][0-9]|3[01])([- /.])(0?[1-9]|1[012])\\2(19|20)\\d\\d");
		Matcher m = p.matcher(input);
		while (m.find()) {
			System.out.println("\nNew Match:");
			System.out.println(m.group()); // 31.03.2014
			System.out.println("Position: " + m.start()); // 42
			for (int i = 0; i <= m.groupCount(); i++) {
				System.out.println("Gruppe " + i + ": " + m.group(i));
			}
		}

		System.out.println("*************************");

		input = "Unsere Anforderungen:\n" + "- Zuverlässigkeit\n"
				+ "- Kompetenz\n" + "- Sie sind einfach ganz toll";
		String lookbehind = "^(-\\*|-|\\*|\\u2027|\\d(.?)?)\\p{Blank}?";
		p = Pattern.compile("(?<=" + lookbehind + ")(?=(\\P{M}\\p{M}*)+$).+",
				Pattern.MULTILINE);
		m = p.matcher(input);
		while (m.find()) {
			System.out.println("\nNew Match:");
			System.out.println(m.group());
			System.out.println("Position: " + m.start());
			for (int i = 0; i <= m.groupCount(); i++) {
				System.out.println("Gruppe " + i + ": " + m.group(i));
			}
		}
	}

	// TODO: test each regex
	@Test
	public void testWithDummyData() {

		PatternMatcher pm = new PatternMatcher();

		/* aufgelistete Anforderungen */
		String content = "Unsere Anforderungen:\n" + "- Zuverlässigkeit\n"
				+ "- Kompetenz\n" + "- Sie sind einfach ganz toll";
		ClassifyUnit cu = new ClassifyUnit(content, 0);
		List<SlotFiller> result = pm.getContentOfInterest(cu, null);
		List<SlotFiller> vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("Zuverlässigkeit", cu.getID()));
		vorlage.add(new SlotFiller("Kompetenz", cu.getID()));
		vorlage.add(new SlotFiller("Sie sind einfach ganz toll", cu.getID()));

		printResults(vorlage, result);

		/* 'sollte X sein/haben/mitbringen' */
		content = "Der Bewerber sollte fit sein. "
				+ "Die Bewerberin sollte viel Make-up haben. "
				+ "Und der/die Bewerber/in sollte seine/ihre eigenen Klamotten mitbringen";
		cu = new ClassifyUnit(content, 0);
		result = pm.getContentOfInterest(cu, null);
		vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("fit", cu.getID()));
		vorlage.add(new SlotFiller("viel Make-up", cu.getID()));
		vorlage.add(new SlotFiller("seine/ihre eigenen Klamotten", cu.getID()));

		printResults(vorlage, result);

		/* Jobbezeichnung */
		content = "Wir suchen eine/n Bankkauffrau/-mann. Wir suchen weiterhin eine/n Frisör/in.";
		cu = new ClassifyUnit(content, 0);
		result = pm.getContentOfInterest(cu, null);
		vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("Bankkauffrau/-mann", cu.getID()));
		vorlage.add(new SlotFiller("Frisör/in", cu.getID()));

		printResults(vorlage, result);

		/*
		 * 'ist|sind|wird|wäre(n) ...
		 * wünschenswert|erforderlich|vorausgesetzt|gewünscht'
		 */
		content = "Zuverlässigkeit ist unbedingt erforderlich. "
				+ "Weiterhin wird Wissen vorausgesetzt. Es wird außerdem gewünscht, "
				+ "dass Sie nett sind. Gute Manieren wären wünschenswert."; // TODO
		cu = new ClassifyUnit(content, 0);
		result = pm.getContentOfInterest(cu, null);
		vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("Zuverlässigkeit", cu.getID()));
		vorlage.add(new SlotFiller("weiterhin wird Wissen", cu.getID()));
		vorlage.add(new SlotFiller("Gute Manieren", cu.getID()));

		printResults(vorlage, result);

		/* vorausgesetzt wird X / Voraussetzung ist X */
		content = "Vorausgesetzt wird gutes Benehmen. Voraussetzung ist unbedingt höfliches Auftreten. Und noch mehr.";
		cu = new ClassifyUnit(content, 0);
		result = pm.getContentOfInterest(cu, null);
		vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("gutes Benehmen", cu.getID()));
		vorlage.add(new SlotFiller("unbedingt höfliches Auftreten", cu.getID()));

		printResults(vorlage, result);

		/*
		 * wird X vorausgesetzt / werden XY vorausgesetzt / wird X erwartet /
		 * werden XY erwartet
		 */
		content = "Dass Sie schicke Anzüge besitzen wird vorausgesetzt. "
				+ "Dass Sie gut riechen wird erwartet. Bereitschaft zu harter Arbeit wird erwartet. "
				+ "Auch Stil und gutes Aussehen werden erwartet."; // TODO
		cu = new ClassifyUnit(content, 0);
		result = pm.getContentOfInterest(cu, null);
		vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("Dass Sie schicke Anzüge besitzen", cu
				.getID()));
		vorlage.add(new SlotFiller("Dass Sie gut riechen", cu.getID()));
		vorlage.add(new SlotFiller("Auch Stil und gutes Aussehen", cu.getID()));
		vorlage.add(new SlotFiller("Bereitschaft zu harter Arbeit", cu.getID()));

		printResults(vorlage, result);

		/* wir setzen X voraus / setzen wir X voraus */
		content = "Darum setzen wir schicke Anzüge voraus. "
				+ "Wir setzen dass Sie schön sind voraus. Außerdem müssen Sie klug sein."; // TODO
		cu = new ClassifyUnit(content, 0);
		result = pm.getContentOfInterest(cu, null);
		vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("schicke Anzüge", cu.getID()));
		vorlage.add(new SlotFiller("dass Sie schön sind", cu.getID()));

		printResults(vorlage, result);

		/* -heit/-keit */
		content = "Wir wünschen uns Ehrlichkeit, Pünktlichkeit und Zuverlässigkeit. Aber nicht zu viel davon.";
		cu = new ClassifyUnit(content, 0);
		result = pm.getContentOfInterest(cu, null);
		vorlage = new ArrayList<SlotFiller>();
		vorlage.add(new SlotFiller("Ehrlichkeit", cu.getID()));
		vorlage.add(new SlotFiller("Pünktlichkeit", cu.getID()));
		vorlage.add(new SlotFiller("Zuverlässigkeit", cu.getID()));

		printResults(vorlage, result);

	}

	private void printResults(List<SlotFiller> vorlage, List<SlotFiller> result) {
		for (SlotFiller slotFiller : vorlage) {
			System.out.println(String.format("Vorlage: '%s' - contained: %s",
					slotFiller, result.contains(slotFiller)));
			if (!result.contains(slotFiller))
				System.out.println("\tResult: " + result);
			// Assert.assertTrue(String.format(
			// "Result should contain '%s',  but didn't. (result: %s)",
			// slotFiller.getContent(), result), result.contains(slotFiller));
		}
		System.out.println();
	}

	@Test
	public void testWithRealData() throws IOException {
		setUp();

		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				"data/trainingIE_140623.csv"), Class.COMPETENCE, classifyUnits);

		Map<ClassifyUnit, Map<String, Integer>> trainingData = gen
				.getTrainingData();
		PatternMatcher pm = new PatternMatcher();

		int count = 0;
		for (ClassifyUnit cu : trainingData.keySet()) {
			// System.out.println(cu);

			JobAd parent = map.get(cu);
			List<SlotFiller> sf = pm.getContentOfInterest(cu,
					parent.getTemplate());
			count += sf.size();
			for (SlotFiller slotFiller : sf) {
				System.out.println(slotFiller);
			}

			System.out.println("\n***********************\n");
		}

		System.out.println("Anzahl Ergebnisse: " + count);
	}

	@Test
	public void printRegExes() {
		PatternMatcher pm = new PatternMatcher();
		Map<Pattern, Class> regExes = pm.getRegExes();
		for (Pattern p : regExes.keySet()) {
			System.out.println(p.pattern());
			System.out.println();
		}
	}
}