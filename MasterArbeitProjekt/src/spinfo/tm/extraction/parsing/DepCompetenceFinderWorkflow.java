package spinfo.tm.extraction.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.IE_Evaluator;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.DataAccessor;

public class DepCompetenceFinderWorkflow {

	private static final String VERBSOFINTERESTFILE = "models/verbsOfInterest.txt";

	public static void main(String[] args) {

		List<Paragraph> parsedParagraphs = DataAccessor
				.getParsedCompetenceParagraphs();

		Map<String, String> verbsOfInterest = readVerbsOfInterest(VERBSOFINTERESTFILE);
		DepCompetenceFinder finder = new DepCompetenceFinder(verbsOfInterest);
		Map<Paragraph, Set<SlotFiller>> allResults = new HashMap<Paragraph, Set<SlotFiller>>();

		int count = 0;
		for (Paragraph par : parsedParagraphs) {
			Set<SlotFiller> results = finder.findCompetences(par);
			count += results.size();
			for (SlotFiller slotFiller : results) {
				System.out.println(slotFiller);
			}
			if (!results.isEmpty())
				allResults.put(par, results);
		}

		System.out.println("Anzahl Ergebnisse: " + count);

		IE_Evaluator.evaluate(allResults);
	}

	private static Map<String, String> readVerbsOfInterest(String file) {
		File inputFile = new File(file);
		if (!inputFile.getName().endsWith(".txt")) {
			System.err.println("Wrong file format");
			return null;
		}

		Map<String, String> toReturn = new HashMap<String, String>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inputFile)))){		
			String line;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(":");
				if (split.length == 1) {
					toReturn.put(split[0], null);
					continue;
				}
				if (split.length == 2)
					toReturn.put(split[0], split[1]);
				else {
					System.err.println("Wrong format");
					return null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return toReturn;
	}

	// private static List<String> readVerbsOfInterest(String fileName) {
	// List<String> verbs = null;
	//
	// File file = new File(fileName);
	// BufferedReader r = null;
	// try {
	// verbs = new ArrayList<String>();
	// r = new BufferedReader(new InputStreamReader(new FileInputStream(
	// file)));
	// String line;
	// while ((line = r.readLine()) != null) {
	// if(line.split("\\s").length != 1) {
	// throw new IllegalArgumentException("File has wrong format");
	// }
	// verbs.add(line.trim());
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// r.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return verbs;
	// }
}
