package com.qa.oneHr.utilities;

import com.qa.oneHr.testbase.TestBase;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestStepFinished;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;


public class StepListener extends TestBase implements ConcurrentEventListener{
    // Stores step name separately for each thread (parallel safe)
    private static ThreadLocal<String> currentStep = new ThreadLocal<>();

    // Stores step result separately for each thread (parallel safe)
    private static ThreadLocal<Status> currentStepStatus = new ThreadLocal<>();

    // Tracks the running scenario name and per-scenario step order, used to name
    // screenshots so the Mochawesome converter can line them up with cucumber.json steps
    private static ThreadLocal<String> mochawesomeScenarioName = new ThreadLocal<>();
    private static ThreadLocal<Integer> mochawesomeStepCounter = ThreadLocal.withInitial(() -> 0);

    public static String getCurrentStep() {
        return currentStep.get();
    }

    public static Status getCurrentStepStatus() {
        return currentStepStatus.get();
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {

        publisher.registerHandlerFor(TestCaseStarted.class, event -> {
            mochawesomeStepCounter.set(0);
            mochawesomeScenarioName.set(event.getTestCase().getName());
        });

        publisher.registerHandlerFor(TestStepFinished.class, event -> {

            if (event.getTestStep() instanceof PickleStepTestStep) {

                PickleStepTestStep step = (PickleStepTestStep) event.getTestStep();

                String stepText = step.getStep().getText();
                String override = EnhancedPdfReportManager.consumeStepTextOverride();
                if (override != null) {
                    stepText = override;
                }
                stepText = capitalizeWords(stepText);
                Status stepStatus = event.getResult().getStatus();

                try {

                    if (getDriver() == null)
                        return;

                    if (stepStatus == Status.FAILED) {
                        System.out.println("DEBUG failed step '" + stepText + "' current URL: " + getDriver().getCurrentUrl());
                        TakeScreenshotforFailedstep(stepText, "image");
                    }  else if (stepStatus == Status.PASSED) {

                        TakeScreenshot(stepText, "image");

                    }
                    else if (stepStatus == Status.SKIPPED) {
                        if (getTest() != null) {
                            getTest().skip(stepText);
                        }
                    }

                    if (stepStatus == Status.PASSED || stepStatus == Status.FAILED) {
                        saveScreenshotForMochawesome();
                    }

                } catch (Exception e) {
                    System.out.println("Screenshot error: " + stepText);
                }
            }
        });
    }

    // Title-cases the step text for display in the reports, e.g.
    // 'the user enters full name "Test"' -> 'The User Enters Full Name "Test"'.
    // Text inside double quotes (actual entered/selected values) is left exactly
    // as-is, since those are real data, not step wording.
    private static String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder result = new StringBuilder();
        boolean inQuotes = false;
        boolean capitalizeNext = true;
        for (char c : text.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                result.append(c);
                capitalizeNext = false;
                continue;
            }
            if (inQuotes) {
                result.append(c);
            } else if (Character.isWhitespace(c)) {
                result.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // Saves a JPEG per step under target/mochawesome-report/screenshots/<scenario>/step_<n>.jpg
    // so scripts/cucumber-to-mochawesome.js can attach it to the matching step by position.
    // JPEG (not PNG) because ReusableMethods.CaptureScreenShotBase64 now downscales and
    // JPEG-compresses screenshots to keep all the reports under 5MB.
    private static void saveScreenshotForMochawesome() {
        try {
            int stepNumber = mochawesomeStepCounter.get() + 1;
            mochawesomeStepCounter.set(stepNumber);

            String scenarioName = mochawesomeScenarioName.get() == null ? "scenario" : mochawesomeScenarioName.get();
            String sanitizedScenario = scenarioName.replaceAll("[^a-zA-Z0-9]", "_");

            String base64 = ReusableMethods.CaptureScreenShotBase64();
            byte[] imageBytes = Base64.getDecoder().decode(base64);

            File folder = new File("./target/mochawesome-report/screenshots/" + sanitizedScenario);
            folder.mkdirs();

            try (FileOutputStream fos = new FileOutputStream(new File(folder, "step_" + stepNumber + ".jpg"))) {
                fos.write(imageBytes);
            }
        } catch (Exception e) {
            System.out.println("Failed to save mochawesome screenshot: " + e.getMessage());
        }
    }
}