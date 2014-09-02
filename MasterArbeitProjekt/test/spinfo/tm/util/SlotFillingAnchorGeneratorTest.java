package spinfo.tm.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import spinfo.tm.data.Section;
import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFillingAnchor;

public class SlotFillingAnchorGeneratorTest {

	/*
	 * ist doch alles scheiße hier!
	 */
	@Test
	public void test() throws IOException {

		Set<SlotFillingAnchor> trainingSet;
		trainingSet = readFromFile("data/trainingsSet_ML.csv");
		
		for (SlotFillingAnchor trainingAnchor : trainingSet) {
			System.out.println(trainingAnchor.getToken());
			System.out.println(trainingAnchor.getParentUUID());
			System.out.println(trainingAnchor.getTokenPos());
			System.out.println("######");
		}

		List<Section> sections = Reader
				.readSectionsFromBinary("data/parsedSections.bin");
		Set<SlotFillingAnchor> allAnchors = SlotFillingAnchorGenerator
				.generateAsSet(sections);

		System.out.println("Größe Trainingsset: " + trainingSet.size());
		System.out.println("Größe Set aller Tokens: " + allAnchors.size());

		Set<SlotFillingAnchor> negativeAnchors = new HashSet<>(allAnchors);
		System.out.println(negativeAnchors.removeAll(trainingSet));

		System.out.println("Größe alle Tokens ohne Trainingsset: "
				+ negativeAnchors.size());

		for (SlotFillingAnchor anchor : allAnchors) {
			if (trainingSet.contains(anchor)) {
				anchor.setCompetence(true);
				System.out.println(anchor);
				System.out.println("Kompetenz: " + anchor.isCompetence());
			}
			// System.out.println("*********************");
		}
	}

	private Set<SlotFillingAnchor> readFromFile(String fileName)
			throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = in.readLine();// 1st line contains headings

		Class classID = null;
		int parentID = 0;
		UUID classifyUnitID = null;

		Set<SlotFillingAnchor> trainedData = new HashSet<>();
		while ((line = in.readLine()) != null) {

			String[] splits = line.split("\t");
			if (splits.length == 5) {
				if (splits[0].length() > 0 && splits[1].length() > 0
						&& splits[2].length() > 0) {
					// new classifyUnit
					parentID = Integer.parseInt(splits[0]);
					classifyUnitID = UUID.fromString(splits[1]);
					classID = Class.valueOf(splits[2]);
				}

				String token = splits[3];
				int position = Integer.parseInt(splits[4]);
				trainedData.add(new SlotFillingAnchor(token, position, true,
						classifyUnitID));

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

	@Test
	public void testSimple() {
		Set<String> all = new HashSet<>();
		all.add("Dies");
		all.add("ist");
		all.add("ein");
		all.add("Test");

		Set<String> some = new HashSet<>();
		some.add("ist");
		some.add("ein");

		System.out.println("Größe some: " + some.size());
		System.out.println("Größe all: " + all.size());

		Set<String> others = new HashSet<>(all);
		System.out.println(others.removeAll(some));

		System.out.println("Größe other: " + others.size());
	}
	
	@Test
	public void testEquals(){
		Section s = new Section("hallo abc", 123);
		
		SlotFillingAnchor a = new SlotFillingAnchor("abc", 0, false, s.getID());
		SlotFillingAnchor b = new SlotFillingAnchor("abc", 3, false, s.getID());
		
		System.out.println(a.equals(b));
	}
}
