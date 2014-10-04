package spinfo.tm.extraction.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import spinfo.tm.data.Paragraph;
import spinfo.tm.evaluation.IE_Evaluator;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.DataAccessor;

public class PatternMatcherWorkflow {

	public static void main(String[] args) {

		List<Paragraph> filteredParagraphs = DataAccessor
				.getFilteredCompetenceParagraphs();

		PatternMatcher pm = new PatternMatcher();
		Map<Paragraph, Set<SlotFiller>> allResults = new HashMap<>();

		int count = 0;
		for (Paragraph p : filteredParagraphs) {
			Map<SlotFiller, Pattern> result = pm.getContentOfInterest(p); // TODO:
																			// for
																			// regex
																			// evaluation
			Set<SlotFiller> resultFiller = result.keySet();
			count += resultFiller.size();
			for (SlotFiller slotFiller : resultFiller) {
				System.out.println(slotFiller);
			}
			if (!resultFiller.isEmpty())
				allResults.put(p, resultFiller);
			System.out.println("\n***********************\n");
		}

		System.out.println("Anzahl Ergebnisse: " + count);

		IE_Evaluator.evaluate(allResults);

		// TODO: regex evaluate??
	}
}