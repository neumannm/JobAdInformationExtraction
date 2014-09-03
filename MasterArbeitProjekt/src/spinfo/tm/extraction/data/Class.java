package spinfo.tm.extraction.data;


/**
 * Classes we are interested in.
 * 
 * @author neumannm
 * 
 */
public enum Class{
	COMPETENCE(3, "Kompetenz"), COMPANY_DESC(1, "Unternehmensbeschreibung"), JOB_DESC(
			2, "Stellenbeschreibung"), OTHER(4, "Sonstiges"), COMPANY_JOB(5,
			"Unternehmens- und Stellenbeschreibung"), COMPANY_COMPETENCE(6,
			"Unternehmensbeschreibung und Kompetenz"), JOB_COMPETENCE(7,
			"Stellenbeschreibung und Kompetenz");

	private int id;
	private String desc;

	private Class(int ID, String desc) {
		this.setId(ID);
		this.setDesc(desc);
	}
	
	/**
	 * Get the description for this item.
	 * @return String representation
	 */
	public String getDesc() {
		return desc;
	}

	private void setDesc(String desc) {
		this.desc = desc;
	}
	
	/**
	 * Get the item's ID.
	 * @return ID
	 */
	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}
}