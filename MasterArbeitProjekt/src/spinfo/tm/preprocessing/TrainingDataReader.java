package spinfo.tm.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import spinfo.tm.data.Section;

/**
 * Class to annotate ClassifyUnits manually with (one or more) classIDs
 * 
 * @author jhermes, geduldig
 * 
 */
public class TrainingDataReader {

	private File tdFile;
	private List<Section> classifiedData;
	boolean singleClassAnnotation = true;

	/**
	 * Instanciates a new TrainingDataGenerator corresponding to the specified
	 * file.
	 * 
	 * @param trainingDataFile
	 *            File for trained data
	 */
	public TrainingDataReader(File trainingDataFile) {
		this.tdFile = trainingDataFile;
		classifiedData = new ArrayList<Section>();
	}

	public TrainingDataReader(File trainingDataFile,
			boolean singleClassAnnotation) {
		this.tdFile = trainingDataFile;
		classifiedData = new ArrayList<Section>();
		this.singleClassAnnotation = singleClassAnnotation;
	}

	/**
	 * Returns trained (manually annotated) data from training data file.
	 * 
	 * @return List of manually annotated ClassifyUnits
	 * @throws IOException
	 */
	public List<Section> getTrainingData() throws IOException {
		if (classifiedData.isEmpty()) {
			classifiedData = new ArrayList<Section>();
			BufferedReader in = new BufferedReader(new FileReader(tdFile));
			String line = in.readLine();
			StringBuffer content = new StringBuffer();
			int parentID = 0;
			UUID paragraphID = null;
			
			//boolean[] classes = new boolean[0];
			int classID = 0;
			while (line != null) {
				String[] splits = line.split("\t");
				if (splits.length == 3) {

					if (/**classes.length**/classID != 0) {

						Section utc = new Section(content.toString(),
								parentID, paragraphID);
//						System.out.println(parentID);
//						System.out.println(paragraphID);
//						System.out.println(content);
						//utc.setClassIDs(classes);
						utc.setActualClassID(classID);
						classifiedData.add(utc);
					}
					paragraphID = UUID.fromString(splits[0]);
					parentID = Integer.parseInt(splits[1]);
					content = new StringBuffer();
					// content.append(splits[3] + "\n");
					classID = Integer.parseInt(splits[2]);
//					String[] ids = splits[2].split(" ");
//					classes = new boolean[ids.length];
//					for (int i = 0; i < classes.length; i++) {
//						classes[i] = Boolean.parseBoolean(ids[i]);
//					}

				} else {
					content.append(line + "\n");
				}
				line = in.readLine();

			}
			if (/**classes.length**/ classID != 0) {
				Section utc = new Section(content.toString(),
						parentID, paragraphID);
//				System.out.println(parentID);
//				System.out.println(paragraphID);
//				System.out.println(content);
				utc.setActualClassID(classID);
				//utc.setClassIDs(classes);
				classifiedData.add(utc);
			}
			in.close();
		}
		return classifiedData;
	}
}