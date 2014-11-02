package spinfo.tm.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.preprocessing.FeatureUnitTokenizer;
import spinfo.tm.util.IETrainingDataGenerator;
import spinfo.tm.util.StopwordFilter;

/**
 * Class for evaluation of results.
 * 
 * @author neumannm
 */
public class IE_Evaluator {

	private static final String IE_TRAININGDATAFILE = "data/trainingIE_140816.csv";
	private static int fp = 0, tp = 0, fn = 0;
	private static FeatureUnitTokenizer tokenizer;

	static {
		tokenizer = new FeatureUnitTokenizer();
	}

	/**
	 * Evaluates the results through comparison with the manually annotated
	 * training data
	 * 
	 * @param allResults
	 *            results of extraction process, i.e. mapping of paragraphs to
	 *            extracted fillers
	 */
	public static void evaluate(Map<Paragraph, Set<SlotFiller>> allResults) {
		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				IE_TRAININGDATAFILE), Class.COMPETENCE);

		Map<Paragraph, Set<SlotFiller>> manuallyLabeled;

		try {
			manuallyLabeled = gen.getTrainingData();
			
			for (Paragraph par : allResults.keySet()) {
				if (manuallyLabeled.containsKey(par)) {
					Set<SlotFiller> result = allResults.get(par);
					Set<SlotFiller> gold = manuallyLabeled.get(par);
					compare(result, gold); // vergleiche die Slotfiller
											// von diesem Paragraphen
				} else {
					Set<SlotFiller> list = allResults.get(par);
					for (SlotFiller slotFiller : list) {
						System.err.println("False positive: "
								+ slotFiller.getContent());
					}
					fp += allResults.get(par).size(); // wenn der ganze
														// Paragraph nicht
														// manuell ausgezeichnet
														// wurde, ist jeder
														// einzelne extrahierte
														// Filler ein FP
				}
			}

			System.out.println("True positives: " + tp);
			System.out.println("False positives: " + fp);
			System.out.println("False negatives: " + fn);

			float precision = tp / ((float) tp + fp);
			float recall = tp / ((float) tp + fn);

			System.out.println("Precision: " + precision);
			System.out.println("Recall: " + recall);

			float f1 = (2 * precision * recall) / ((float) precision + recall);
			System.out.println("F1: " + f1);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void compare(Set<SlotFiller> result, Set<SlotFiller> gold)
			throws IOException {
		List<String> resTokens;
		for (SlotFiller resFiller : result) {
			resTokens = tokenizer.tokenize(resFiller.getContent());
			List<String> goldTokens;
			int max = Integer.MIN_VALUE;

			System.out.println("#################################");
			goldloop: for (SlotFiller goldFiller : gold) {
				goldTokens = tokenizer.tokenize(goldFiller.getContent());
				System.out.println(resFiller.getContent() + "\t|\t"
						+ goldFiller.getContent());

				int is = calculateIntersectionSize(resTokens, goldTokens);
				if (is > max)
					max = is;

				if (max == goldTokens.size()) {
					break goldloop;
				}
			}
			if (max > 0) {
				tp++; // for each result that has at least 1 common token with
						// gold --> tp++
				System.out.println(">>>TRUE POSITIVE: "
						+ resFiller.getContent());
			} else {
				System.err.println("False positive: " + resFiller.getContent());
				// for each result that has no match in gold --> fp++
				fp++;
			}
		}

		List<String> goldTokens;
		for (SlotFiller goldFiller : gold) {
			goldTokens = tokenizer.tokenize(goldFiller.getContent());
			int max = Integer.MIN_VALUE;

			resLoop: for (SlotFiller resFiller : result) {
				resTokens = tokenizer.tokenize(resFiller.getContent());
				int is = calculateIntersectionSize(resTokens, goldTokens);
				if (is > max)
					max = is;

				if (max == goldTokens.size()) {
					break resLoop;
				}
			}
			if (max <= 0) {
				System.err
						.println("False negative: " + goldFiller.getContent());
				// for each gold that has no match in result --> fn++
				fn++;
			}
		}
	}

	private static int calculateIntersectionSize(List<String> resTokens,
			List<String> goldTokens) throws IOException {

		StopwordFilter filter = new StopwordFilter(new File(
				"data/stopwords.txt"));

		Set<String> result = new TreeSet<>(filter.filterStopwords(resTokens));
		Set<String> gold = new TreeSet<>(filter.filterStopwords(goldTokens));

		if (result.isEmpty() || result.size() == 0) {
			System.out.println();
		}

		System.out.println(result);
		System.out.println(gold);

		System.out.println("Anzahl Tokens result: " + result.size());
		System.out.println("Anzahl Tokens gold: " + gold.size());

		result.retainAll(gold); // intersection
		System.out.println("Anzahl Tokens gemeinsam: " + result.size());

		int intersection = result.size();
		System.out.println("---------");

		return intersection;
	}
}
