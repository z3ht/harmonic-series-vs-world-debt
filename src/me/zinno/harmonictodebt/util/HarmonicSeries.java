package me.zinno.harmonictodebt.util;

public class HarmonicSeries {
	
	private HarmonicSeries() {}
	
	public static double calcNextNormHarmVal(long currentIndex) {
		return 1.0 / (currentIndex +  1);
	}
	
	public static double calcNextAltHarmVal(long currentIndex) {
		return (((currentIndex) % 2 == 0) ? 1.0 : -1.0) / (currentIndex + 1);
	}
	
}
