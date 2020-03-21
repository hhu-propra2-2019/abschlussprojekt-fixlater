package mops.termine2.util;

public class IntegerToolkit {
	
	public static int getZiffer(int stelle, int zahl) {
		return zahl / ((int) Math.pow(10, stelle)) % 10;
	}
}
