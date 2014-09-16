package spinfo.tm.evaluation.util;

import java.util.List;

/**
 * Class to bundle trainings and test sets applicable 
 * to machine learning techniques
 * @author jhermes
 *
 * @param <T> generic parameter for type of items in the sets
 */
public class TrainingTestSets<T>{
	private List<T> training;
	private List<T> test;
	
	public TrainingTestSets(List<T> training, List<T> test) {
		super();
		this.training = training;
		this.test = test;
	}
	
	public List<T> getTrainingSet() {
		return training;
	}
	
	public List<T> getTestSet() {
		return test;
	}
}