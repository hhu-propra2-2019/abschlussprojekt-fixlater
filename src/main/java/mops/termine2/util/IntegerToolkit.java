package mops.termine2.util;

public class IntegerToolkit {
	
	public static int getFirstZiffer(int zahl) {
		return zahl % 10;
	}
	
	public static int getSecoundZiffer(int zahl) {
		return zahl / 10 % 10;
	}
}
