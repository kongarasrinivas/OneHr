package com.qa.oneHr.testbase;

import java.awt.AWTException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import com.qa.oneHr.utilities.EnhancedPdfReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.safari.SafariDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.qa.oneHr.utilities.ConfigurationUtilities;
import com.qa.oneHr.utilities.ReusableMethods;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestBase {
	public static ThreadLocal<WebDriver> driver=new ThreadLocal<>();//This Makes driver thread-safe
	public static ReusableMethods webdriver = new ReusableMethods();
	public static Date date = new Date();
	public static SimpleDateFormat formatter = new SimpleDateFormat("d-MMM-YY HH-mm-ss");
	public static String dt = formatter.format(date);

	// ---- Suite-level metadata for the consolidated HTML execution report ----
	public static String executionId = java.util.UUID.randomUUID().toString();
	public static java.time.LocalDateTime suiteStartTime;
	public static java.time.LocalDateTime suiteEndTime;
	public static String chromeBrowserVersion = "";
	public static String TestDescription;
	public static String ImageName;
	public static String LogDescription;
	public static By PassLocator;
	public static String EnterText;
	protected static final Logger LOG = (Logger) LogManager.getLogger(TestBase.class);

	static String Concatnate = ".";

	public static void openBrowser() throws InterruptedException {


		if (ConfigurationUtilities.getProperty("browser").equalsIgnoreCase("chrome")) {
			// Let WebDriverManager resolve the chromedriver matching the installed Chrome.
			// Do not pin driverVersion: Chrome auto-updates and a pinned driver breaks
			// with "This version of ChromeDriver only supports Chrome version N".
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("start-maximized"); // open Browser in maximized mode
			options.addArguments("disable-infobars"); // disabling infobars
			options.addArguments("--disable-extensions"); // disabling extensions
			options.addArguments("--disable-gpu"); // applicable to Windows os only
			options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
			options.addArguments("--no-sandbox"); // Bypass OS security model
			options.setExperimentalOption("useAutomationExtension", false);
			options.addArguments("--disable-in-process-stack-traces");
			options.addArguments("--disable-logging");
			options.addArguments("--log-level=3");
			options.addArguments("--remote-allow-origins=*");
			options.addArguments("--disable-notifications");
			options.setAcceptInsecureCerts(true);
			//options.addArguments("--headless");
			options.setExperimentalOption("detach", true);
			setDriver(new ChromeDriver(options));
			getDriver().manage().window().maximize();
			getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
			if (chromeBrowserVersion.isEmpty()) {
				chromeBrowserVersion = ((ChromeDriver) getDriver()).getCapabilities().getBrowserVersion();
			}

		} else if (ConfigurationUtilities.getProperty("browser").equalsIgnoreCase("edge")) {
			WebDriverManager.edgedriver().setup();
			setDriver(new EdgeDriver()) ;
			getDriver().manage().window().maximize();
			getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

		} else if (ConfigurationUtilities.getProperty("browser").equalsIgnoreCase("firefox")) {
			// Create a Firefox Profile
			FirefoxProfile profile = new FirefoxProfile();

			// Enable full-page screenshot functionality
			profile.setPreference("devtools.camera.enabled", false);
			profile.setPreference("webdriver.firefox.profile", true); // Full-page screenshots

			// Set FirefoxOptions with the profile
			FirefoxOptions options = new FirefoxOptions();
			options.setProfile(profile);
			//options.addArguments("--headless");
			// Setup WebDriver for Firefox
			WebDriverManager.firefoxdriver().setup(); // Correct WebDriverManager setup for Firefox
			setDriver(new FirefoxDriver(options)) ; // Correct capitalization for FirefoxDriver

			getDriver().manage().window().maximize(); // Maximize the browser window
			getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30)); // Set implicit wait
		} else {
			setDriver(new SafariDriver());
			getDriver().manage().window().maximize();
			getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		}

	}

	public static void openURL() throws InterruptedException, AWTException {
		String baseURL = ConfigurationUtilities.getProperty("Environment")+ "URL";
		getDriver().get(ConfigurationUtilities.getProperty(baseURL));
		webdriver.screenzoomout();
	}

	//These below two methods are newly added
	public static WebDriver getDriver() {
		return driver.get(); //Used to get current thread driver
	}

	public static void setDriver(WebDriver drv) {
		driver.set(drv);
	}

	public static void quitDriver() {
		try {
			if (driver.get() != null) {
				driver.get().quit();
			}
		} catch (Exception ignored) {}
		finally {
			driver.remove();
		}
	}

	// Logger Information
	public static void LogInFo(String LogDescription) {
		LOG.info(LogDescription);
	}

	public static void LogWarn(String LogDescription) {
		LOG.warn(LogDescription);
	}

	public static void LogDeBug(String LogDescription) {
		LOG.debug(LogDescription);
	}

	public static void LogTrace(String LogDescription) {
		LOG.trace(LogDescription);
	}

	public static ExtentReports extent;
	public static ExtentHtmlReporter spark;
	//public static ExtentTest test;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	public static ExtentTest getTest() { //This we are using to call this private variable test.
		return test.get();
	}
	// Destination file for the report - one folder per run, shared with the PDF report
	public static String runReportFolder = "./OneHrHtml&PDFReports/OneHrReports " + dt + "/test-output";
	static String destFile = runReportFolder + "/htmlreports/OneHr Application Automation-Report.html";

	// Original report location - kept alongside the newer per-run folder above so
	// both formats keep getting generated, not just one replacing the other.
	static String legacyDestFile = "./OneHrExtentReports/OneHrAutomationReport" + dt + ".html";
	public static ExtentHtmlReporter legacySpark;

	// Initialize ExtentReports and attach the reporter
	public synchronized static void initializeExtentReports() {
		if (extent == null) {  // Ensure the extent is not re-initialized
			suiteStartTime = java.time.LocalDateTime.now();
			new java.io.File(destFile).getParentFile().mkdirs();
			new java.io.File(legacyDestFile).getParentFile().mkdirs();
			spark = new ExtentHtmlReporter(destFile);
			legacySpark = new ExtentHtmlReporter(legacyDestFile);
			extent = new ExtentReports();
			extent.attachReporter(spark, legacySpark);
			String env = ConfigurationUtilities.getProperty("Environment");

			// Set system information
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("Browser Name", ConfigurationUtilities.getProperty("browser"));  // Or get from config properties
			extent.setSystemInfo("Environment", ConfigurationUtilities.getProperty("Environment"));
			extent.setSystemInfo("Base URL", ConfigurationUtilities.getProperty(env+ "URL"));
			extent.setSystemInfo("User Name", ConfigurationUtilities.getProperty("User"));
			extent.setSystemInfo("User Email", ConfigurationUtilities.getProperty("UserEmail"));

			// Report configurations - applied to both reporters so they render identically
			for (ExtentHtmlReporter reporter : new ExtentHtmlReporter[]{spark, legacySpark}) {
				reporter.config().setDocumentTitle("OneHr Automation Testing Report");
				reporter.config().setReportName("OneHr Automation Test Suite");
				reporter.config().setTheme(Theme.DARK);
				reporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
				reporter.config().setCss(".r-img {width: 75%;}");
			}
		}
	}

	// Create Extent Test
	public static ExtentTest createExtentTest(String testName, String description, String category, String author) {
		if (extent == null) {
			initializeExtentReports();  // Initialize extent if not already initialized
		}

		ExtentTest extentTest = extent.createTest(testName, description)
				.assignCategory(category)
				.assignAuthor(author);

		test.set(extentTest);
		return extentTest;
	}


	// Finalize the report
	public static void finalizeReport() {
		suiteEndTime = java.time.LocalDateTime.now();
		if (extent != null) {
			extent.flush();  // Write the report to the file
		}
	}

	// Add customized text for passing status
	public static void TestPass(String TextDescription) {
		if (test.get() != null) {
			test.get().pass("<font color='green' style='font-weight:normal; font-size:14px;'>" + TextDescription + "</font>");
		}
	}

	// Add customized text for failing status
	public static void TestFail(String TextDescription) {
		if (test.get() != null) {
			test.get().fail("<font color='red' style='font-weight:normal; font-size:12px;'>" + TextDescription + "</font>");
		}
	}

	// Add customized text for info status
	public static void TestInfo(String TextDescription) {
		if (test.get() != null) {
			test.get().info("<font color='blue' style='font-weight:normal; font-size:12px;'>" + TextDescription + "</font>");
		}
	}

	public static void TakeScreenshot(String TestDescription, String ImageName) throws IOException, InterruptedException {

//        String base64 = ReusableMethods.CaptureScreenShotBase64();
//
//        // Add prefix required by Extent Reports
//        String finalBase64Image = "data:image/png;base64," + base64;
//
//        ScreenCapture(TestDescription + " - " + ImageName, finalBase64Image);
		Thread.sleep(200);
		String base64 = ReusableMethods.CaptureScreenShotBase64();
		//String finalBase64Image = base64;

		ScreenCapture(TestDescription + " - " + ImageName, base64);

		// ADD THIS LINE
		EnhancedPdfReportManager.addStep(TestDescription, "PASSED", base64);
	}

	// Capture screenshot and add log details to the report
	public static void ScreenCapture(String details, String Image) {
		String greenText = "<span style='color:green;'>" + details + "</span>";
		test.get().pass
				(greenText,
						MediaEntityBuilder.createScreenCaptureFromBase64String("data:image/jpeg;base64," + Image)
								.build()
				);
	}

	// Capture screenshot and add log details to the report
	public static void ScreenCaptureForFailedstep(String details, String Image) {
		String redText = "<span style='color:red;'>" + details + "</span>";
		test.get().fail
				(redText,
						MediaEntityBuilder.createScreenCaptureFromBase64String("data:image/jpeg;base64," + Image)
								.build()
				);
	}

	public static void TakeScreenshotforFailedstep(String TestDescription, String ImageName) throws IOException, InterruptedException {

		Thread.sleep(200);
		String base64 = ReusableMethods.CaptureScreenShotBase64();

		ScreenCaptureForFailedstep(TestDescription + " - " + ImageName, base64);

		EnhancedPdfReportManager.addStep(TestDescription, "FAILED", base64);
	}

}

