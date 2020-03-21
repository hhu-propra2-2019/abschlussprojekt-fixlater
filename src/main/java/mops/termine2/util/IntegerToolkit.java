package mops.termine2.util;

public class IntegerToolkit {
	
	public static int getFirstZiffer(int zahl) {
		return zahl % 10;
	}
	
	public static int getSecondZiffer(int zahl) {
		return zahl / 10 % 10;
	}
}
