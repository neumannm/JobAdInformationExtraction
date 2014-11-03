package spinfo.tm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.parsing.ParagraphParser;
import spinfo.tm.util.ClassFilter;
import spinfo.tm.util.IETrainingDataGenerator;
import spinfo.tm.util.ReaderWriter;
import spinfo.tm.util.SlotFillingAnchorGenerator;

/**
 * Run this class as Application to prepare your workspace for running the
 * Worflows. Reads and creates all necessary data and saves them in binary files for use
 * in the Information Extraction Task.
 * 
 * @author neumannm
 * 
 */
public class Preparation {

	/*
	 * Nur ausführen, wenn Dateien nicht vorhanden! (parsen dauert)
	 */
	public static void main(String[] args) {
		final String TRAININGDATAFILE = "data/SingleClassTrainingDataFiltered.csv";
		final String ALLPARAGRAPHSFILE = "data/allParagraphs.bin";
		final String FILTEREDPARAGRAPHSFILE = "data/filteredParagraphs.bin";
		final String PARSEDSENTENCESFILE = "data/parsedSentences.bin";
		final String PARSEDPARAGRAPHSFILE = "data/parsedParagraphs.bin";
		final String POTENTIALANCHORSFILE = "data/potentialSlotFillingAnchors.bin";
		final String ANNOTATEDFILLERSFILE = "data/annotatedSlotFillers.bin";
		final String ANNOTATEDANCHORSFILE = "data/annotatedSlotFillingAnchors.bin";
		final String IE_TRAINING_DATA_FILE = "data/trainingIE_140816.csv";
		final String TRAINING_SET_ML_FILE = "data/trainingsSet_ML.csv";

		try {
			List<Paragraph> allParagraphs = readParagraphsFromCSV(TRAININGDATAFILE);
			ReaderWriter.saveToBinaryFile(allParagraphs, new File(
					ALLPARAGRAPHSFILE));

			List<Paragraph> filteredParagraphs = filterParagraphs(allParagraphs);
			ReaderWriter.saveToBinaryFile(filteredParagraphs, new File(
					FILTEREDPARAGRAPHSFILE));

			List<Paragraph> parsedParagraphs = parseFilteredParagraphs(filteredParagraphs);
			ReaderWriter.saveToBinaryFile(parsedParagraphs, new File(
					PARSEDPARAGRAPHSFILE));

			List<Sentence> allSentences = getAllParsedSentences(parsedParagraphs);
			ReaderWriter.saveToBinaryFile(allSentences, new File(
					PARSEDSENTENCESFILE));

			List<SlotFiller> slotFillerGold = readSlotFillerFromCSV(IE_TRAINING_DATA_FILE);
			ReaderWriter.saveToBinaryFile(slotFillerGold, new File(
					ANNOTATEDFILLERSFILE));

			List<PotentialSlotFillingAnchor> slotFillingAnchorsGold = readAnchorsFromCSV(TRAINING_SET_ML_FILE);
			ReaderWriter.saveToBinaryFile(slotFillingAnchorsGold, new File(
					ANNOTATEDANCHORSFILE));

			Set<PotentialSlotFillingAnchor> potentialAnchors = createPotentialFillerAnchors(parsedParagraphs);
			ReaderWriter.saveToBinaryFile(potentialAnchors, new File(
					POTENTIALANCHORSFILE));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<SlotFiller> readSlotFillerFromCSV(
			String trainingDataFile) throws IOException {
		List<SlotFiller> toReturn = new ArrayList<>();

		IETrainingDataGenerator tdg = new IETrainingDataGenerator(new File(
				trainingDataFile), Class.COMPETENCE);
		Map<Paragraph, Set<SlotFiller>> trainingData = tdg.getTrainingData();

		for (Paragraph par : trainingData.keySet()) {
			toReturn.addAll(trainingData.get(par));
		}

		return toReturn;
	}

	private static List<PotentialSlotFillingAnchor> readAnchorsFromCSV(
			String trainingDataFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(trainingDataFile));
		String line = in.readLine();// 1st line contains headings

		Class classID = null;
		int parentID = 0;
		UUID paragraphID = null;

		List<PotentialSlotFillingAnchor> trainedData = new ArrayList<>();
		while ((line = in.readLine()) != null) {

			String[] splits = line.split("\t");
			if (splits.length == 5) {
				if (splits[0].length() > 0 && splits[1].length() > 0
						&& splits[2].length() > 0) {
					// new paragraph
					parentID = Integer.parseInt(splits[0]);
					paragraphID = UUID.fromString(splits[1]);
					classID = Class.valueOf(splits[2]);
				}

				String token = splits[3];
				int position = Integer.parseInt(splits[4]);
				trainedData.add(new PotentialSlotFillingAnchor(token, position,
						true, paragraphID));

			} else if (splits.length == 0 && line.trim().isEmpty()) {
				// new line in file
			} else {
				in.close();
				throw new IOException("File seems to have wrong format");
			}
		}
		in.close();
		return trainedData;
	}

	private static Set<PotentialSlotFillingAnchor> createPotentialFillerAnchors(
			List<Paragraph> parsedParagraphs) {
		return SlotFillingAnchorGenerator.generateAsSet(parsedParagraphs);
	}

	private static List<Paragraph> readParagraphsFromCSV(String trainingdatafile)
			throws IOException {
		List<Paragraph> paragraphs = ReaderWriter
				.readParagraphsFromCSV(trainingdatafile);
		System.out.println("Anzahl Paragraphs insgesamt: " + paragraphs.size());
		return paragraphs;
	}

	private static List<Paragraph> filterParagraphs(
			List<Paragraph> allParagraphs) {
		Class[] classesToAnnotate = { Class.COMPETENCE,
				Class.COMPANY_COMPETENCE, Class.JOB_COMPETENCE };

		List<Paragraph> filteredParagraphs = ClassFilter.filter(allParagraphs,
				classesToAnnotate);

		System.out.println("Anzahl Paragraphs gefiltert: "
				+ filteredParagraphs.size());
		return filteredParagraphs;
	}

	private static List<Sentence> getAllParsedSentences(
			List<Paragraph> parsedParagraphs) {
		List<Sentence> toReturn = new ArrayList<Sentence>();
		for (Paragraph paragraph : parsedParagraphs) {
			toReturn.addAll(paragraph.getSentenceData().values());
		}
		System.out.println("Number of sentences: " + toReturn.size());
		return toReturn;
	}

	private static List<Paragraph> parseFilteredParagraphs(
			List<Paragraph> filteredParagraphs) {
		ParagraphParser parser = new ParagraphParser();
		List<Paragraph> toReturn = parser.parse(filteredParagraphs);

		return toReturn;
	}
}