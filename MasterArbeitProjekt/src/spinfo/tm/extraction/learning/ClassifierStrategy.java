package spinfo.tm.extraction.learning;

import spinfo.tm.extraction.data.PotentialSlotFillingAnchor;

public interface ClassifierStrategy {

	Boolean classify(PotentialSlotFillingAnchor token);

	ClassifierStrategy train(PotentialSlotFillingAnchor anchor);
}
