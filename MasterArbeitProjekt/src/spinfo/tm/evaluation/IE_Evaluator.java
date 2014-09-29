package spinfo.tm.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
				// TODO: manual evaluation or automatically detect overlap
				if (goldFiller.getContent().contains(resFiller.getContent())
						|| resFiller.getContent().contains(
								goldFiller.getContent())) {
					foundSth = true;
					System.out.println(resFiller.getContent() + "\t|\t"
							+ goldFiller.getContent());

					double matchingPortion = calculateMatchingPortion(
							resFiller.getContent(), goldFiller.getContent());
					System.out.println(matchingPortion);
					tp++;
					break goldloop;
				} 
//				else {
//					// ask user?
//					if (isPositiveMatch(resFiller.getContent(),
//							goldFiller.getContent())) {
//						foundSth = true;
//						break goldloop;
//					}
//				}
			}
			if (!foundSth) {
				System.err.println("False positive: " + resFiller.getContent());
				// for each result that has no match in gold --> fp++
				fp++;
			}
		}

		for (SlotFiller goldFiller : gold) {
			foundSth = false;
			resLoop: for (SlotFiller resFiller : result) {
				if (resFiller.getContent().contains(goldFiller.getContent())
						|| goldFiller.getContent().contains(
								resFiller.getContent())) {
					foundSth = true;
					tp++;
					break resLoop;
				}
			}
			if (!foundSth) {
				System.err
						.println("False negative: " + goldFiller.getContent());
				// for each gold that has no match in result --> fn++
				fn++;
			}
		}
	}

	private static boolean isPositiveMatch(String resultContent,
			String goldContent) {
		System.out.println(String.format("Is <%s> a match for <%s>?",
				resultContent, goldContent));
		Scanner in = new Scanner(System.in);
		int read = 0;
		read = in.nextInt();
		return read == 1;
	}

	private static double calculateMatchingPortion(String result, String gold) {
		String[] resultTokens = result.split(" ");
		String[] goldTokens = gold.split(" ");

		double portion = resultTokens.length / (double) goldTokens.length;
		return portion > 1.0 ? 1.0 : portion;
	}

	private void editDistance(String s1, String s2) {
	}
}
