package spinfo.tm.extraction.pattern;

import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.IE_Evaluator;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.DataAccessor;
import spinfo.tm.util.ResultWriter;

/**
 * Workflow zur Extraktion von Kompetenz-Phrasen aus Paragraphen mithilfe regulärer Ausdrücke.
 * 
 * @author neumannm
 */
public class PatternMatcherWorkflow {

	private static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger("PatternMatcherWorkflow");

		List<Paragraph> filteredParagraphs = DataAccessor
				.getFilteredCompetenceParagraphs();

		PatternMatcher pm = new PatternMatcher();
		Map<Paragraph, Set<SlotFiller>> allResults = new HashMap<>();

		int count = 0;
		for (Paragraph p : filteredParagraphs) {
			// save pattern that matched each slot filler for evaluation
			Map<SlotFiller, Pattern> result = pm.getContentOfInterest(p);

			Set<SlotFiller> resultFiller = result.keySet();
			count += resultFiller.size();
			for (SlotFiller slotFiller : resultFiller) {
				System.out.println(slotFiller);
			}
			if (!resultFiller.isEmpty())
				allResults.put(p, resultFiller);
			logger.info("\n***********************\n");

			// evaluateRegExes(result);
		}

		logger.info("Anzahl Ergebnisse: " + count);

		IE_Evaluator.evaluate(allResults, PatternMatcherWorkflow.class.getSimpleName());
		
		ResultWriter.writeManualExtractionResults(allResults, PatternMatcherWorkflow.class.getSimpleName());
	}
}