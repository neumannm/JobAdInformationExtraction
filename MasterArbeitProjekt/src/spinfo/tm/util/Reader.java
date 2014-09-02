package spinfo.tm.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import spinfo.tm.data.Section;
import spinfo.tm.data.Sentence;
import spinfo.tm.preprocessing.TrainingDataReader;

public class Reader {

	public static List<Section> readSectionsFromCSV(String fileName)
			throws IOException {
		File trainingDataFile = new File(fileName);
		TrainingDataReader tdg = new TrainingDataReader(trainingDataFile);

		List<Section> paragraphs = tdg.getTrainingData();
		return paragraphs;
	}

	public static List<Section> readSectionsFromBinary(String fileName) {
		List<Section> toReturn = new ArrayList<>();
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(fileName));
			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof Section) {
					toReturn.add((Section) readObject);
				}
			}
		} catch (EOFException e) {
			return toReturn;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}
	
	
	public static List<Sentence> readSentencesFromBinary(String fileName) {
		List<Sentence> toReturn = new ArrayList<>();
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(fileName));
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
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}

}
