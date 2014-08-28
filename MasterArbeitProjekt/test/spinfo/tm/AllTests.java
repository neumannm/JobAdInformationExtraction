package spinfo.tm;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import spinfo.tm.extraction.parsing.CompetenceFinderTest;
import spinfo.tm.extraction.parsing.ParagraphConverterTest;
import spinfo.tm.extraction.parsing.ParserTest;
import spinfo.tm.extraction.pattern.PatternMatchingTest;

@RunWith(Suite.class)
@SuiteClasses({ ParagraphConverterTest.class, /* IE_TDTester.class, */
ParserTest.class, PatternMatchingTest.class, CompetenceFinderTest.class})
public class AllTests {

	@BeforeClass
	public static void setUpForAllTests() {
		System.out
				.println("Test: wird das vor allen anderen Tests ausgeführt?");
		// TODO: was wird von allen Tests gleichermaßen benötigt? das müsste
		// dann in eine Datei gespeichert werden, weil man nichts an die anderen
		// Tests übergeben kann
		
		
	}
}