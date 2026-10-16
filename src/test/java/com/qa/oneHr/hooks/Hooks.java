package com.qa.oneHr.hooks;

import java.util.Collection;

import com.qa.oneHr.testbase.TestBase;
import com.qa.oneHr.utilities.ConfigurationUtilities;
import com.qa.oneHr.utilities.EnhancedPdfReportManager;
import com.qa.oneHr.utilities.TestContextSetup;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;


public class Hooks extends TestBase {
    private static ThreadLocal<Scenario> scenarioThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Long> scenarioStartTime = new ThreadLocal<>();
    //private static volatile String latestScenarioName = "Unknown Scenario"; // Default value
    //private static volatile String latestTagName = "No Tag"; // Default value
    //public static volatile String latestUser = "Default User"; // Default user (modify as needed)
    private static ThreadLocal<String> scenarioNameThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> tagNameThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> userThreadLocal = new ThreadLocal<>();
    private TestContextSetup testcontext;
    public Hooks(TestContextSetup testcontext) {
        this.testcontext=testcontext;
    }


    @Before
    public void beforeScenario(Scenario scenario) throws Exception {
        scenarioThreadLocal.set(scenario);
        scenarioStartTime.set(System.currentTimeMillis());
        //Scneario Name
        scenarioNameThreadLocal.set(scenario.getName());

        // Extracting tags
        // Extract tags
        Collection<String> tags = scenario.getSourceTagNames();
        String tagName = tags.isEmpty() ? "No Tag" : String.join(", ", tags);
        tagNameThreadLocal.set(tagName);

        // Extract user from tags
        String user = "Default User"; // fallback
        for (String tag : tags) {
            if (tag.startsWith("@User:")) {
                user = tag.replace("@User:", "").trim();
                break;
            }
        }
        userThreadLocal.set(user);

        // Feature file name (for the "Feature" column in the HTML execution report)
        String featureUri = scenario.getUri().toString();
        String featureName = featureUri.substring(featureUri.lastIndexOf('/') + 1);

        //Fetching the browser, Environment, latestTagname, LatestUser
        EnhancedPdfReportManager.setScenarioMeta(
                ConfigurationUtilities.getProperty("browser"),
                ConfigurationUtilities.getProperty("Environment"),
                tagName,
                user,
                featureName
        );

//        // Extracting user (Assuming a user tag format like @User:JohnDoe)
//        for (String tag : tags) {
//            if (tag.startsWith("@User:")) {
//                latestUser = tag.replace("@User:", "").trim(); // Extract the user from tag
//                break;
//            }
//        }

        System.out.println("📌 Tags: " + tagName);
        System.out.println("👤 User: " + user);
        System.out.println("✅ Scenario initialized: " + scenario.getName());
        createExtentTest(scenario.getName(), "✅ Scenario Description: "+scenario.getName(),tagName,ConfigurationUtilities.getProperty("User"));
        testcontext.getPageObjectManager().getLoginPage().launchBrowser();
        testcontext.getPageObjectManager().getLoginPage().openURL();
        System.out.println(
                "" +   " ██████╗  ███╗   ██╗ ███████╗   ██╗  ██╗ ██████╗ \n" +
                        "██╔═══██╗ ████╗  ██║ ██╔════╝   ██║  ██║ ██╔══██╗\n" +
                        "██║   ██║ ██╔██╗ ██║ █████╗     ███████║ ██████╔╝\n" +
                        "██║   ██║ ██║╚██╗██║ ██╔══╝     ██╔══██║ ██╔══██╗\n" +
                        "╚██████╔╝ ██║ ╚████║ ███████╗   ██║  ██║ ██║  ██║\n" +
                        " ╚═════╝  ╚═╝  ╚═══╝ ╚══════╝   ╚═╝  ╚═╝ ╚═╝  ╚═╝\n"
        );
        System.out.println("=========== AUTOMATION EXECUTION STARTED ===========");
    }

    public static String getScenarioName() {
        return scenarioNameThreadLocal.get();
    }

    public static String getTagName() {
        return tagNameThreadLocal.get();
    }

    public static String getLatestUser() {
        return userThreadLocal.get();
    }

    public static Scenario getScenario() {
        return scenarioThreadLocal.get();
    }

    public static void setLatestUser(String user) {
        userThreadLocal.set(user);
    }

    @After
    public void afterScenario() throws InterruptedException {
        String scenarioName=getScenarioName();
        System.out.println("Scenario: " + scenarioName);

        // Calculate duration
        long durationMs = System.currentTimeMillis() - scenarioStartTime.get();
        long minutes = durationMs / 60000;
        long seconds = (durationMs % 60000) / 1000;
        String durationText = minutes + " min " + seconds + " sec";

        // Generate PDF with duration
        String pdfPath = EnhancedPdfReportManager.generatePdf(scenarioName, durationText, dt);

        // Attach clickable PDF link (relative path) in Spark report
        if (pdfPath != null && !pdfPath.isEmpty() && TestBase.getTest() != null) {
            String pdfRelativePath = "./PdfReports/" + new java.io.File(pdfPath).getName();
            TestBase.getTest().info("Download PDF Report: <a href='" + pdfRelativePath + "' target='_blank'>Click Here</a>");
        }
        System.out.println("Scenario: " + scenarioName);
        TestBase.quitDriver();
        System.out.println(
                "" +   " ██████╗  ███╗   ██╗ ███████╗   ██╗  ██╗ ██████╗ \n" +
                        "██╔═══██╗ ████╗  ██║ ██╔════╝   ██║  ██║ ██╔══██╗\n" +
                        "██║   ██║ ██╔██╗ ██║ █████╗     ███████║ ██████╔╝\n" +
                        "██║   ██║ ██║╚██╗██║ ██╔══╝     ██╔══██║ ██╔══██╗\n" +
                        "╚██████╔╝ ██║ ╚████║ ███████╗   ██║  ██║ ██║  ██║\n" +
                        " ╚═════╝  ╚═╝  ╚═══╝ ╚══════╝   ╚═╝  ╚═╝ ╚═╝  ╚═╝\n"
        );
        System.out.println("=========== AUTOMATION EXECUTION ENDED ===========");
        testcontext.softAssert.assertAll();
        // Clean up ThreadLocal after scenario
        scenarioThreadLocal.remove();
        scenarioStartTime.remove();
        scenarioNameThreadLocal.remove();
        tagNameThreadLocal.remove();
        userThreadLocal.remove();
    }

}



