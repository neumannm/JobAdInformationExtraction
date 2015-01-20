package spinfo.tm.extraction.learning;

import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

/**
 * Interface for an unspecified classifier strategy, e.g. Naive Bayes, Rocchio, kNN...
 * 
 * @author neumannm
 *
 */
public interface ClassifierStrategy {

	/**
	 * Classify a specific token
	 * @param token token to be classified
	 * @return True iff token belongs to desired class, False otherwise
	 */
	Boolean classify(PotentialSlotFillingAnchor token);

	/**
	 * Train the classifier with a manually classified token.
	 * @param anchor manually classified token
	 * @return this
	 */
	ClassifierStrategy train(PotentialSlotFillingAnchor anchor);
}
