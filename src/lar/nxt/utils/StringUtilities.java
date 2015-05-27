package lar.nxt.utils;

import java.util.ArrayList;

/**
 * String Utilities Connection
 * 
 * @author Daniele
 *
 */
public class StringUtilities {

	/**
	 * Split String by Delimiter
	 * 
	 * @param s
	 *            Delimiter
	 * @param S
	 *            TargetString
	 * @return Array of Chunks
	 */
	public static String[] split(String s, String target) {
		String S = new String(target);
		ArrayList<String> list = new ArrayList<String>();
		int start = 0;
		while (start < S.length()) {
			int end = S.indexOf(s, start);
			if (end < 0)
				break;

			list.add(S.substring(start, end));
			start = end + s.length();
		}
		if (start < S.length())
			list.add(S.substring(start));
		return list.toArray(new String[list.size()]);
	}
}
