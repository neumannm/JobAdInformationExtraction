package spinfo.tm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassifyUnitSplitter {

	public static List<String> splitAtNewLine(String toSplit) {
		List<String> toReturn = new ArrayList<String>();
		String[] split = toSplit.split("\n");
		for (String string : split) {
			toReturn.add(string);
		}
		return toReturn;
	}

	public static List<String> splitAtEmptyLine(String toSplit) {
		List<String> toReturn = new ArrayList<String>();
		List<String> splitted = splitAtNewLine(toSplit);
		StringBuffer merged = new StringBuffer();
		for (String string : splitted) {
			string = string.trim();
			if (string.length() > 0) {
				merged.append(string + "\n");
			} else {
				toReturn.add(merged.toString());
				merged = new StringBuffer();
			}
		}
		toReturn.add(merged.toString());
		return toReturn;
	}

	public static List<String> mergeJobTitleAndLists(String toSplit) {
		List<String> toReturn = new ArrayList<String>();
		List<String> splitted = splitAtEmptyLine(toSplit);

		Pattern jobTitle = Pattern.compile("^.*\\w+/-?\\w+.*$",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
						| Pattern.UNICODE_CASE); // matches constructions with
													// slash in between, e.g.
													// 'm/w' or 'Bewerber/in'
		Matcher jobTitleMatcher = jobTitle.matcher(toSplit);

		Pattern list = Pattern.compile("(?imux)"
				+ "(^(-\\*|-|\\*|\\d(\\.?\\)?)?)" + // some symbols starting a
														// list element:
														// -*,-,*,1.,1),1.)
				"\\p{Blank}?" + // maybe whitespace in between
				"(?>\\P{M}\\p{M}*)+$)+"); // ((?>\P{M}\p{M}*)+) any number of
											// graphemes

		Matcher listMatcher = list.matcher(toSplit);

		String previous = "";
		boolean jobFound = false;
		for (String string : splitted) {
			string = string.trim();
			jobTitleMatcher.reset(string);
			if (string.startsWith("zu") && jobFound) {
				previous = previous + "\n" + string;
				jobFound = false;
			} else if (!jobFound && looksLikeJobTitle(jobTitleMatcher, toSplit)) {
				jobFound = true;
				previous = previous + "\n" + string;
			} else if (looksLikeList(listMatcher, previous)) {
				previous = previous + "\n" + string;
				jobFound = false;
			} else {
				if (previous.trim().length() > 0) {
					toReturn.add(previous);
				}
				previous = string;
				jobFound = false;
			}
		}
		toReturn.add(previous);

		return toReturn;
	}


	private static boolean looksLikeJobTitle(Matcher matcher, String string) {
		if (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			if ((end - start) < 70 && (end / (double) string.length() < 0.5))
				return true;
		}
		return false;
	}

	private static boolean looksLikeList(Matcher matcher, String string) {
		if (string.trim().endsWith(":") && matcher.find()) {
			return true;
		}
		return false;
	}
}
