package spinfo.tm.data;

import is2.data.SentenceData09;

import java.util.List;
import java.util.UUID;

/**
 * A basic unit for all classify tasks.
 * 
 * @author jhermes
 * 
 * @author neumannm
 * 
 */
public class ClassifyUnit implements Comparable<ClassifyUnit> {

	// private static int count;
	private String content;
	private int parentID;
	private int actualClassID;
	private UUID id;
	private List<String> FeatureUnits;
	private double[] FeatureVector;
	private boolean[] classIDs;
	private List<SentenceData09> sentenceData; // new: results from parser
												// (parsed this.content)
	private static int NUMBEROFCLASSES;

	public boolean[] getClassIDs() {
		return classIDs;
	}

	public static void setNumberOfClasses(int numberOfClasses) {
		NUMBEROFCLASSES = numberOfClasses;
	}

	public static int getNumberOfClasses() {
		return NUMBEROFCLASSES;
	}

	public void setClassIDs(boolean[] classIDs) {
		this.classIDs = classIDs;
	}

	public ClassifyUnit(String content, int parentID, UUID id) {
		super();
		this.content = content;
		this.parentID = parentID;
		this.classIDs = new boolean[4];
		this.id = id;
	}

	public ClassifyUnit(String content, int parentID) {
		this(content, parentID, UUID.randomUUID());
	}

	public List<String> getFeatureUnits() {
		return FeatureUnits;
	}

	public void setFeatureUnits(List<String> featureUnits) {
		FeatureUnits = featureUnits;
	}

	public double[] getFeatureVector() {
		return FeatureVector;
	}

	public void setFeatureVector(double[] featureVector) {
		FeatureVector = featureVector;
	}

	public UUID getID() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public int getParentID() {
		return parentID;
	}

	public String toString() {
		return parentID + "\t" + actualClassID + "\t" + content + "\n";
	}

	public int getActualClassID() {
		return actualClassID;
	}

	public void setActualClassID(int classID) {
		this.actualClassID = classID;
	}

	@Override
	public int compareTo(ClassifyUnit other) {
		return this.id.compareTo(other.id);
	}

	public List<SentenceData09> getSentenceData() {
		return this.sentenceData;
	}

	public void setSentenceData(List<SentenceData09> data) {
		this.sentenceData = data;
	}
}