package spinfo.tm.extraction.learning;

import spinfo.tm.extraction.data.Class;

public interface ClassifierStrategy {

	String classify(String token);

	ClassifierStrategy train(String token, Class c);
}
