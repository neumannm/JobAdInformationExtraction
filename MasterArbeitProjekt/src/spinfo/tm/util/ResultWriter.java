package spinfo.tm.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spinfo.tm.data.Paragraph;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.data.SlotFiller;

public class ResultWriter {

	private static final File outputDir = new File("output");

	static {
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}

	public static void writeManualExtractionResults(
			Map<Paragraph, Set<SlotFiller>> allResults, String originClassName) {
		PrintWriter out = null;

		try {
			out = new PrintWriter(new FileWriter(outputDir + "/"
					+ originClassName + "-Results.csv"));
			out.println("Paragraph ID\tParagraph Text\tExtracted Competences");
			for (Paragraph par : allResults.keySet()) {
				out.print(par.getID() + "\t"); // paragraph ID
				out.print(par.getContent().replaceAll("\n", " ") + "\t"); // paragraph
																			// content
				for (SlotFiller sf : allResults.get(par)) {
					out.print(sf.getContent());
					out.print("\n\t\t");
				}
				out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (out != null) {
			out.flush();
			out.close();
		}
	}

	public static void writeEvaluationResults(float precision, float recall,
			float f1, String originClassName) {
		PrintWriter out = null;

		try {
			out = new PrintWriter(new FileWriter(outputDir + "/"
					+ originClassName + "-Eval.txt"));
			out.println("Evaluation results from Extraction with "
					+ originClassName);
			out.println("Precision: " + precision);
			out.println("Recall: " + recall);
			out.println("F1: " + f1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (out != null) {
			out.flush();
			out.close();
		}
	}

	public static void writeClassificationExtractionResults(
			Map<Paragraph, List<PotentialSlotFillingAnchor>> allResults,
			String originClassName) {
		PrintWriter out = null;

		try {
			out = new PrintWriter(new FileWriter(outputDir + "/"
					+ originClassName + "-Results.csv"));
			out.println("Paragraph ID\tParagraph Text\tClassified Anchors");
			for (Paragraph par : allResults.keySet()) {
				out.print(par.getID() + "\t"); // paragraph ID
				out.print(par.getContent().replaceAll("\n", " ") + "\t"); // paragraph
																			// content
				for (PotentialSlotFillingAnchor sf : allResults.get(par)) {
					out.print(sf.getToken());
					out.print("\n\t\t");
				}
				out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (out != null) {
			out.flush();
			out.close();
		}

	}

}
