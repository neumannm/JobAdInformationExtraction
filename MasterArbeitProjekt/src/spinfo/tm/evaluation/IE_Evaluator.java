package spinfo.tm.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.util.IETrainingDataGenerator;

public class IE_Evaluator {

	private static final String IE_TRAININGDATAFILE = "data/trainingIE_140816.csv";
	private static int fp = 0, tp = 0, fn = 0;

	public static void evaluate(Map<Paragraph, List<SlotFiller>> allResults) {
		IETrainingDataGenerator gen = new IETrainingDataGenerator(new File(
				IE_TRAININGDATAFILE), Class.COMPETENCE);

		Map<Paragraph, List<SlotFiller>> manuallyLabeled;

		try {
			manuallyLabeled = gen.getTrainingData();
			for (Paragraph par : allResults.keySet()) {
				if (manuallyLabeled.containsKey(par)) {
					List<SlotFiller> result = allResults.get(par);
					List<SlotFiller> gold = manuallyLabeled.get(par);
					compare(result, gold); // vergleiche die Slotfiller
											// von diesem Paragraphen
				} else {
					List<SlotFiller> list = allResults.get(par);
					for (SlotFiller slotFiller : list) {
						System.err.println("False positive: " + slotFiller.getContent());
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
			System.out.println("False positives:" + fp);
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

	private static void compare(List<SlotFiller> result, List<SlotFiller> gold) {
		boolean foundSth = false;
		for (SlotFiller resFiller : result) {
			foundSth = false;
			goldloop: for (SlotFiller goldFiller : gold) {
				if (goldFiller.getContent().contains(resFiller.getContent()) || resFiller.getContent().contains(goldFiller.getContent())) {
					foundSth = true;
					// logger.info("Gold contained in result");
					System.out.println(resFiller.getContent() + "\t|\t"
							+ goldFiller.getContent());

					double matchingPortion = calculateMatchingPortion(
							resFiller.getContent(), goldFiller.getContent());
					System.out.println(matchingPortion);
					tp++;
					break goldloop;
				} 
			}
			if (!foundSth) {
				System.err.println("False positive: " + resFiller.getContent());
				fp++;
				// for each result that has no match in gold --> fp++
			}
		}

		for (SlotFiller goldFiller : gold) {
			foundSth = false;
			resLoop: for (SlotFiller resFiller : result) {
				if (resFiller.getContent().contains(goldFiller.getContent())
						|| goldFiller.getContent().contains(
								resFiller.getContent())) {
					foundSth = true;
					// logger.info("Gold contained in result or the other way round");
					// System.out.println(resFiller.getContent() + "\t|\t"
					// + goldFiller.getContent());

					tp++;
					break resLoop;
				}
			}
			if (!foundSth) {
				System.err.println("False negative: " + goldFiller.getContent());
				fn++;
				// for each gold that has no match in result --> fn++
			}
		}
	}

	private static double calculateMatchingPortion(String result, String gold) {
		String[] resultTokens = result.split(" ");
		String[] goldTokens = gold.split(" ");

		double portion = resultTokens.length / (double) goldTokens.length;
		return portion > 1.0 ? 1.0 : portion;
	}

}
