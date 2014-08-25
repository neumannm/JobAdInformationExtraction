package spinfo.tm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import spinfo.tm.data.ClassifyUnit;
import spinfo.tm.extraction.data.JobAd;

/*
 * Idee: Zugriff von ClassifyUnit auf Parent-JobAd haben (weil JobAd wiederum das Template verwaltet)
 */
public class UniversalMapper {

	private static Map<ClassifyUnit, JobAd> cuJobadMap = new HashMap<ClassifyUnit, JobAd>();
	private static Map<UUID, ClassifyUnit> cuIDMap = new HashMap<>();

	public static Map<ClassifyUnit, JobAd> map(List<ClassifyUnit> cus) {

		for (ClassifyUnit cu : cus) {
			int parentID = cu.getParentID();
			JobAd jobAd = new JobAd(parentID, null);
			cuJobadMap.put(cu, jobAd);
			cuIDMap.put(cu.getID(), cu);
		}
		return cuJobadMap;
	}

	public static JobAd getJobAdForCU(ClassifyUnit unitToClassify) {
		return cuJobadMap.get(unitToClassify);
	}

	public static ClassifyUnit getCUforID(UUID id) {
		return cuIDMap.get(id);
	}
}
