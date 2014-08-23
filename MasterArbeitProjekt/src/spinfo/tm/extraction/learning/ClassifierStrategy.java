package spinfo.tm.extraction.learning;

import spinfo.tm.extraction.data.Class;
import spinfo.tm.extraction.data.SlotFiller;
import spinfo.tm.extraction.data.SlotFillingAnchor;

public interface ClassifierStrategy {

	Class classify(SlotFillingAnchor token);

	ClassifierStrategy train(SlotFillingAnchor anchor, Class c);
}
