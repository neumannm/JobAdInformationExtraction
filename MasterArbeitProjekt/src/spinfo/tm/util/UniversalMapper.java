package spinfo.tm.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import spinfo.tm.data.Section;

public class UniversalMapper {

	private static Map<UUID, Section> sectionIDMap = new HashMap<>();
	private static final String ALLSECTIONSFILE = "data/allSections.bin";

	static {
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(
					new FileInputStream(ALLSECTIONSFILE));
			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof Section) {
					Section s = (Section) readObject;
					sectionIDMap.put(s.getID(), s);
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

	public static Section getSectionforID(UUID id) {
		return sectionIDMap.get(id);
	}
}