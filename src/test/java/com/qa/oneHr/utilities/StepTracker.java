package com.qa.oneHr.utilities;

public class StepTracker {
	private static ThreadLocal<String> currentStep = new ThreadLocal<>();

	public static void setStep(String step) {
		currentStep.set(step);
	}

	public static String getStep() {
		return currentStep.get() != null ? currentStep.get() : "Undefined_Step";
	}

	public static void clear() {
		currentStep.remove();
	}
}
