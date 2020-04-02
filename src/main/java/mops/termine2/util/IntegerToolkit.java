package mops.termine2.util;

import java.util.ArrayList;
import java.util.List;

public class IntegerToolkit {
	
	public static int getZiffer(int stelle, int zahl) {
		return zahl / ((int) Math.pow(10, stelle)) % 10;
	}
	
	public static List<Integer> findHighestIndex(int[] votes) {
		List<Integer> highest = new ArrayList<>();
		int highestValue = 0;
		
		for (int i = 0; i < votes.length; i++) {
			if (votes[i] >= highestValue) {
				if (votes[i] > highestValue) {
					highest.clear();
				}
				highest.add(i);
				highestValue = votes[i];
			}
		}
		
		return highest;
	}
	
	public static int getInt(String toParse) {
		try {
			return Integer.parseInt(toParse);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
