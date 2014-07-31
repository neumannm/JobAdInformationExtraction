package spinfo.tm.extraction.parsing.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum for all POS Tags used by Mate Tools.
 * 
 * @author neumannm
 * 
 */
public enum POSGermanTag {

	ADJA("Attributives Adjektiv"), // [das] große [Haus]
	ADJD("Adverbiales oder prädikatives Adjektiv"), // [er fährt] schnell, [er
													// ist] schnell
	ADV("Adverb"), // schon, bald, doch
	APPR("Präposition; Zirkumposition links"), // in [der Stadt], ohne [mich]
	APPRART("Präposition mit Artikel"), // im [Haus], zur [Sache]
	APPO("Postposition"), // [ihm] zufolge, [der Sache] wegen
	APZR("Zirkumposition rechts"), // [von jetzt] an
	ART("Bestimmer oder unbestimmer Artikel"), // der, die, das, ein, eine
	CARD("Kardinalzahl"), // zwei [Männer], [im Jahre] 1994
	FM("Fremdsprachichles Material"), // [Er hat das mit ``] A big fish [''
										// übersetzt]
	ITJ("Interjektion"), // mhm, ach, tja
	KOUI("unterordnende Konjunktion mit zu und Infinitiv"), // um [zu leben],
															// anstatt [zu
															// fragen]
	KOUS("unterordnende Konjunktion mit Satz"), // weil, dass, damit, wenn, ob
	KON("nebenordnende Konjunktion"), // und, oder, aber
	KOKOM("Vergleichskonjunktion"), // als, wie
	NN("normales Nomen"), // Tisch, Herr, [das] Reisen
	NE("Eigennamen"), // Hans, Hamburg, HSV
	PDS("substituierendes Demonstrativpronomen"), // dieser, jener
	PDAT("attribuierendes Demonstrativpronomen"), // jener [Mensch]
	PIS("substituierendes Indefinitpronomen"), // keiner, viele, man, niemand
	PIAT("attribuierendes Indefinitpronomen ohne Determiner"), // kein [Mensch],
																// irgendein
																// [Glas]
	PIDAT("attribuierendes Indefinitpronomen mit Determiner"), // [ein] wenig
																// [Wasser],
																// [die] beiden
																// [Brüder]
	PPER("irreflexives Personalpronomen"), // ich, er, ihm, mich, dir
	PPOSS("substituierendes Possessivpronomen"), // meins, deiner
	PPOSAT("attribuierendes Possessivpronomen"), // mein [Buch], deine [Mutter]
	PRELS("substituierendes Relativpronomen"), // [der Hund ,] der
	PRELAT("attribuierendes Relativpronomen"), // [der Mann ,] dessen [Hund]
	PRF("reflexives Personalpronomen"), // sich, einander, dich, mir
	PWS("substituierendes Interrogativpronomen"), // wer, was
	PWAT("attribuierendes Interrogativpronomen"), // welche[Farbe], wessen [Hut]
	PWAV("adverbiales Interrogativ- oder Relativpronomen"), // warum, wo, wann,
															// worüber, wobei
	PAV("Pronominaladverb"), // dafür, dabei, deswegen, trotzdem
	PTKZU("zu vor Infinitiv"), // zu [gehen]
	PTKNEG("Negationspartike"), // nicht
	PTKVZ("abgetrennter Verbzusatz"), // [er kommt] an, [er fährt] rad
	PTKANT("Antwortpartikel"), // ja, nein, danke, bitte
	PTKA("Partikel bei Adjektiv oder Adverb"), // am [schönsten], zu [schnell]
	TRUNC("Kompositions-Erstglied"), // An- [und Abreise]
	VVFIN("finites Verb, voll"), // [du] gehst, [wir] kommen [an]
	VVIMP("Imperativ, voll"), // komm [!]
	VVINF("Infinitiv"), // gehen, ankommen
	VVIZU("Infinitiv mit zu"), // anzukommen, loszulassen
	VVPP("Partizip Perfekt"), // gegangen, angekommen
	VAFIN("finites Verb, aux"), // [du] bist, [wir] werden
	VAIMP("Imperativ, aux"), // sei [ruhig !]
	VAINF("Infinitiv, aux"), // werden, sein
	VAPP("Partizip Perfekt"), // gewesen
	VMFIN("finites Verb, modal"), // dürfen
	VMINF("Infinitiv, modal"), // wollen
	VMPP("Partizip Perfekt, modal"), // gekonnt, [er hat gehen] können
	XY("Nichtwort, Sonderzeichen enthaltend"), // 3:7, H2O, D2XW3
	UNDEFINED("Nicht definiert, zb. Satzzeichen"), DOLLARKOMMA("Komma"), // ,
	DOLLARPUNKT("Satzbeendende Interpunktion"), // . ? ! ; :
	DOLLARKLAMMER("sonstige Satzzeichen; satzintern"); // - [,]()

	private final String desc;

	private static final Map<String, POSGermanTag> nameToValueMap = new HashMap<String, POSGermanTag>();

	static {
		for (POSGermanTag value : EnumSet.allOf(POSGermanTag.class)) {
			nameToValueMap.put(value.name(), value);
		}
	}

	/**
	 * Get the POS Tag to a given name
	 * 
	 * @param name
	 *            name of POS Tag
	 * @return POS
	 */
	public static POSGermanTag forName(String name) {
		return nameToValueMap.get(name);
	}

	private POSGermanTag(String desc) {
		this.desc = desc;
	}

	/**
	 * Get the Tag's description
	 * 
	 * @return description as String
	 */
	public String getDesc() {
		return this.desc;
	}
}