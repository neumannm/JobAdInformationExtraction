package spinfo.tm.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import spinfo.tm.data.ClassifyUnit;

/**
 * Class to annotate ClassifyUnits manually with (one or more) classIDs
 * 
 * @author jhermes, geduldig
 * 
 */
public class TrainingDataGenerator {

	private File tdFile;
	private List<ClassifyUnit> classifiedData;
	boolean singleClassAnnotation = true;
	private int numberOfCategories = 0;

	/**
	 * Instanciates a new TrainingDataGenerator corresponding to the specified
	 * file.
	 * 
	 * @param trainingDataFile
	 *            File for trained data
	 */
	public TrainingDataGenerator(File trainingDataFile) {
		this.tdFile = trainingDataFile;
		classifiedData = new ArrayList<ClassifyUnit>();
	}

	public TrainingDataGenerator(File trainingDataFile,
			boolean singleClassAnnotation) {
		this.tdFile = trainingDataFile;
		classifiedData = new ArrayList<ClassifyUnit>();
		this.singleClassAnnotation = singleClassAnnotation;
	}

	/**
	 * Returns trained (manually annotated) data from training data file.
	 * 
	 * @return List of manually annotated ClassifyUnits
	 * @throws IOException
	 */
	public List<ClassifyUnit> getTrainingData() throws IOException {
		if (classifiedData.isEmpty()) {
			classifiedData = new ArrayList<ClassifyUnit>();
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

						ClassifyUnit utc = new ClassifyUnit(content.toString(),
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
				ClassifyUnit utc = new ClassifyUnit(content.toString(),
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

	/**
	 * Asks the annotator to classify each ClassifyUnit-content.
	 * 
	 * @param dataList
	 *            ClassifyUnits to manually annotate
	 * @throws Exception 
	 * @throws IOException
	 */
	// public void generateTrainingData(List<ClassifyUnit> dataList)
	// throws IOException {
	// BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	// if (!singleClassAnnotation) {
	// System.out
	// .println("Bewerten Sie die aufgeführten Items nach Zugehörigkeit zur gewünschten Klasse. \nGeben Sie zunächst die Anzahl unterschiedlicher Klassen ein.");
	// String answer = in.readLine();
	// int numberOfClasses = Integer.parseInt(answer);
	// for (int i = 0; i < dataList.size(); i++) {
	// ClassifyUnit item = dataList.get(i);
	// System.out.println(item.getContent());
	// // answer = in.readLine();
	// answer = "1";
	// if (answer.equals("b")) {
	// classifiedData.remove(classifiedData.size() - 1);
	// i--;
	// i--;
	// continue;
	// }
	// if (answer.length() > 3 || answer.length() == 0) {
	// System.out.println("try again");
	// i--;
	// continue;
	// }
	// boolean[] IDs = new boolean[numberOfClasses];
	//
	// String[] classes = answer.split(",");
	// for (int c = 0; c < classes.length; c++) {
	// int currentClass = Integer.parseInt(classes[c]);
	// if (currentClass == 0) {
	// System.out.println("0 ist keine zulässige Kategorie");
	// System.out.println("try again...");
	// i--;
	// continue;
	// }
	// if (currentClass > numberOfClasses) {
	// System.out.println("ungültige kategorie (zu groß)");
	// i--;
	// continue;
	// }
	// IDs[currentClass - 1] = true;
	// }
	//
	// item.setClassIDs(IDs);
	// classifiedData.add(item);
	// }
	//
	// }
	// else{
	// System.out.println("Bewerten Sie die aufgeführten Items nach Zugehörigkeit zur gewünschten Klasse.")
	// String Answer
	// }
	// PrintWriter out = new PrintWriter(new FileWriter(tdFile));
	// for (ClassifyUnit unitToClassify : classifiedData) {
	// out.print(unitToClassify.getID() + "\t");
	// out.print(unitToClassify.getParentID() + "\t");
	// boolean[] IDs = unitToClassify.getClassIDs();
	// out.print(IDs[0] + " " + IDs[1] + " " + IDs[2] + " " + IDs[3]
	// + "\t");
	// out.println(unitToClassify.getContent());
	// }
	// out.flush();
	// out.close();
	// }
	
	private boolean[] setMultiClassAnnotation(boolean[] classIDs, String answer) throws Exception{
		System.out.println("multiclassannot.");
		String[] categories = answer.split(",");
		if(categories.length > 2 || categories.length == 0){
			throw new Exception();
		}
		for(int i = 0; i < categories.length; i++){
			try{
				int currentClass = Integer.parseInt(categories[i]);
				if(currentClass<=0 || currentClass > numberOfCategories){
					throw new Exception();
				}
				classIDs[currentClass] = true;
			}
			catch(NumberFormatException e){
				throw new Exception();
			}
		}
		return classIDs;
	}

	private int getSingleClassAnnotation(String answer) throws Exception{
		int category = 0;
		try{
			category = Integer.parseInt(answer);
			if(category == 0 || category > numberOfCategories){
				throw new Exception();
			}
			
			return category;
		}
		catch(Exception e){
			throw new Exception();
		}
		
	
	}
	
	public void annotate(List<ClassifyUnit> dataList) throws IOException {
		
		getTrainingData();
		System.out.println("Training Data Size: " + classifiedData.size());
		int start = classifiedData.size();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		// Anzahl der Kategorien festlegen
			
			System.out.println("Geben Sie die Zahl der unterschiedlichen Klassen an");
				
			while (numberOfCategories == 0) {
				String answer = in.readLine();
				try {
					numberOfCategories = Integer.parseInt(answer);
					if(numberOfCategories == 0){
						System.out.println("invalid number. try again...");
						continue;
					}
					if (numberOfCategories < 0) {
						numberOfCategories = 0;
						System.out.println("invalid number. try again...");
						continue;
					}
					ClassifyUnit.setNumberOfClasses(numberOfCategories);
				} catch (ClassCastException e) {
					continue;
				}
			}
		
		//annotate...
		for (int i = start; i < dataList.size(); i++) {
			ClassifyUnit currentCU = dataList.get(i);
			//boolean[] classIDs = new boolean[numberOfCategories];
			int classID = 0;
			System.out.println(currentCU.getContent());
			String answer = in.readLine();
			if (answer.equals("stop")) {
				break;
			}
			if (answer.equals("b")) {
				classifiedData.remove(classifiedData.size() - 1);
				i--;
				i--;
				continue;
			}
			
			
			// single class annotation
			if (singleClassAnnotation) {
				try{
					classID = getSingleClassAnnotation(answer);
				}
				catch(Exception e){
					System.out.println("invalid category. please try again...");
					i--;
					continue;
				}
			}
			
			
			
			//multi class annotation
//			else{
//				
//				try{
//					classI = setMultiClassAnnotation(answer);
//					}
//				catch(Exception e){
//					i--;
//					System.out.println("invalid category_ies. try again");
//					continue;
//				}
//			
//			}
			currentCU.setActualClassID(classID);
			classifiedData.add(currentCU);
		}	
		PrintWriter out = new PrintWriter(new FileWriter(tdFile));
		for (ClassifyUnit unitToClassify : classifiedData) {
			out.print(unitToClassify.getID() + "\t");
			out.print(unitToClassify.getParentID() + "\t");
			out.print(unitToClassify.getActualClassID()+"\n");
			out.println(unitToClassify.getContent().trim().replaceAll("\t", " "));
			out.println();
		}
		out.flush();
		out.close();
	}
}
