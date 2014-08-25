package spinfo.tm.data;

import java.util.List;
import java.util.Map;
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

//	private Map<String, SentenceData09> sentences; // TODO: remove? (replace
													// with Set<Sentence>)

	 private Map<Integer, Sentence> sentences;
	// private Set<Sentence> sentences;

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

	public Map<Integer, Sentence> getSentenceData() {
		return this.sentences;
	}

	public void setSentenceData(Map<Integer, Sentence> data) {
		this.sentences = data;
	}
}