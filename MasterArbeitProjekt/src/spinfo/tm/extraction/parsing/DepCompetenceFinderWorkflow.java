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
import java.util.logging.Logger;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.IE_Evaluator;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.DataAccessor;
import spinfo.tm.util.ResultWriter;

/**
 * Workflow zur Extraktion von Kompetenz-Phrasen aus Paragraphen mithilfe eines Dependenzparsers.
 * 
 * @author neumannm
 *
 */
public class DepCompetenceFinderWorkflow {

	private static final String VERBSOFINTERESTFILE = "models/verbsOfInterest.txt";

	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("DepCompetenceFinderWorkflow");

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

		logger.info("Anzahl Ergebnisse: " + count);

		for (Paragraph paragraph : allResults.keySet()) {
			for (SlotFiller filler : allResults.get(paragraph)) {
				System.out.println(filler);
			}
			System.out.println("--------------");
		}
		
		
		IE_Evaluator.evaluate(allResults, DepCompetenceFinderWorkflow.class.getSimpleName());
		
		ResultWriter.writeManualExtractionResults(allResults, DepCompetenceFinderWorkflow.class.getSimpleName());
	}

	private static Map<String, String> readVerbsOfInterest(String file) {
		File inputFile = new File(file);
		if (!inputFile.getName().endsWith(".txt")) {
			logger.severe("Wrong file format");
			return null;
		}

		Map<String, String> toReturn = new HashMap<String, String>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile)))) {
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
					logger.severe("Wrong format");
					return null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
}