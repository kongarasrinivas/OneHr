package com.qa.oneHr.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StepCounterUtil {
	private static int stepCounter = 1;

	public static void resetCounter() {
		stepCounter = 1;
	}

	public static int getAndIncrementStepCounter() {
		return stepCounter++;
	}

	public static String getCurrentTimestamp() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
	}
}
