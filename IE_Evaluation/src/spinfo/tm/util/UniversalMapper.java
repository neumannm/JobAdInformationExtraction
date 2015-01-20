package spinfo.tm.util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import spinfo.tm.data.Paragraph;

/**
 * Helper class that allows rertrieving of paragraph objects if ID is given.
 * 
 * @author neumannm
 * 
 */
public class UniversalMapper {

	private static Map<UUID, Paragraph> paragraphIDMap = new HashMap<>();
	private static final String ALLPARAGRAPHSFILE = "data/allParagraphs.bin";

	static {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				ALLPARAGRAPHSFILE))) {

			Object readObject;
			while (true) {
				readObject = is.readObject();
				if (readObject instanceof Paragraph) {
					Paragraph s = (Paragraph) readObject;
					paragraphIDMap.put(s.getID(), s);
				}
			}
		} catch (EOFException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the paragraph object that has a specific ID.
	 * 
	 * @param id
	 *            the paragraph's ID
	 * @return paragraph with the specified ID
	 */
	public static Paragraph getParagraphforID(UUID id) {
		return paragraphIDMap.get(id);
	}
}