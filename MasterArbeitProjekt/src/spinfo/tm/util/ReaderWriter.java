package spinfo.tm.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import spinfo.tm.data.Paragraph;
import spinfo.tm.data.Sentence;
import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.preprocessing.TrainingDataReader;

public class ReaderWriter {

	public static List<Paragraph> readParagraphsFromCSV(String fileName)
			throws IOException {
		File trainingDataFile = new File(fileName);

		if (!trainingDataFile.exists()) {
			System.err.println("Base file does not exist! Aborting...");
			System.exit(1);
		}

		TrainingDataReader tdg = new TrainingDataReader(trainingDataFile);

		List<Paragraph> paragraphs = tdg.getTrainingData();
		return paragraphs;
	}

	public static List<Paragraph> readParagraphsFromBinary(File paragraphsFile) {
		List<Paragraph> toReturn = new ArrayList<>();
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				paragraphsFile))) {

			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof Paragraph) {
					toReturn.add((Paragraph) readObject);
				}
			}
		} catch (EOFException e) {
			return toReturn;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public static List<Sentence> readSentencesFromBinary(File file) {
		List<Sentence> toReturn = new ArrayList<>();
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				file))) {

			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof Sentence) {
					toReturn.add((Sentence) readObject);
				}
			}
		} catch (EOFException e) {
			return toReturn;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public static List<PotentialSlotFillingAnchor> readSlotFillingAnchorsFromBinary(
			File anchorsFile) {
		List<PotentialSlotFillingAnchor> toReturn = new ArrayList<>();
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				anchorsFile))) {

			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof PotentialSlotFillingAnchor) {
					toReturn.add((PotentialSlotFillingAnchor) readObject);
				}
			}
		} catch (EOFException e) {
			return toReturn;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public static List<SlotFiller> readPotentialFillersFromBinary(File file) {
		List<SlotFiller> toReturn = new ArrayList<>();
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				file))) {

			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof SlotFiller) {
					toReturn.add((SlotFiller) readObject);
				}
			}
		} catch (EOFException e) {
			return toReturn;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public static void saveToBinaryFile(Collection<?> data, File file) {
		try (ObjectOutputStream os = new ObjectOutputStream(
				new FileOutputStream(file))) {
			for (Object o : data) {
				os.writeObject(o);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}