package spinfo.tm.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import spinfo.tm.data.ClassifyUnit;

public class UniversalMapper {

	private static Map<UUID, ClassifyUnit> cuIDMap = new HashMap<>();
	private static final String ALLCLASSIFYUNITSFILE = "data/allClassifyUnits.bin";

	static {
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(
					new FileInputStream(ALLCLASSIFYUNITSFILE));
			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof ClassifyUnit) {
					ClassifyUnit cu = (ClassifyUnit) readObject;
					cuIDMap.put(cu.getID(), cu);
				}
			}
		} catch (EOFException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ClassifyUnit getCUforID(UUID id) {
		return cuIDMap.get(id);
	}
}