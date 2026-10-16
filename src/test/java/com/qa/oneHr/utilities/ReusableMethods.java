package com.qa.oneHr.utilities;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.qa.oneHr.testbase.TestBase;

public class ReusableMethods extends TestBase {
	public  By PassLocator;
	public  String EnterText;
	private WebElement previousElement;

	    // Remove highlight from element
	    private void removeHighlight(WebDriver driver, WebElement element) {
	        try {
	            JavascriptExecutor js = (JavascriptExecutor) driver;
	            js.executeScript("arguments[0].style.border=''", element);
	        } catch (StaleElementReferenceException e) {
	            System.out.println("Element is stale, cannot remove highlight.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public boolean isElementDisplayed(By PassLocator)
	    {
	    	try {
	    	WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait until element is visible
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));
			highlight(getDriver(), element);
			return element.isDisplayed();

		} catch (Exception e) {
			LogInFo("Element is not displayed " + PassLocator + " | Exception: " + e.getMessage());
			throw e; // rethrow so test fails properly
		}
	    }
	    	
	    public boolean isElementDisplayedOnScreen(By locator) {
	        return getDriver().findElements(locator).size() > 0;
	    }

	public String getInputFieldValue(By locator) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		return element.getAttribute("value");
	}
	public String getExistingValue(By locator) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		return element.getText();
	}
	public String validateExistingValue(By locator, String fieldName, String regex) {
		String value = getExistingValue(locator);

		if (value == null || value.trim().isEmpty()) {
			return fieldName + " is empty";
		}
		if (!value.matches(regex)) {
			return fieldName + " format is invalid: " + value;
		}
		return "PASS";
	}

	public String validateFieldWithMessage(By locator, String fieldName, String regex) {
		String value = getInputFieldValue(locator);

		if (value == null || value.trim().isEmpty()) {
			return fieldName + " is empty";
		}

		if (!value.matches(regex)) {
			return fieldName + " format is invalid: " + value;
		}

		return "PASS";
	}

	public boolean isFieldValuePresent(By locator) {
		String value = getInputFieldValue(locator);
		return value != null && !value.trim().isEmpty();
	}
	public boolean isExistingValuePresent(By locator)
	{
		String Result=getExistingValue(locator);
		return Result != null && !Result.trim().isEmpty();
	}
	    
	    public void typeSequentially(WebDriver driver, By locator, String text, int delayInMillis) {
	        WebElement element = driver.findElement(locator);
	        element.clear();
	        highlight(getDriver(), element);

	        for (char ch : text.toCharArray()) {
	            element.sendKeys(String.valueOf(ch));
	            try {
	                Thread.sleep(delayInMillis);  // delay between each character
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	        }
	    }

	    public void clickOnEnter() throws AWTException
	    {
	    	Robot r=new Robot();
	    	r.keyPress(KeyEvent.VK_ENTER);
	    	r.keyRelease(KeyEvent.VK_ENTER);
	    }
	    // Highlight element
	    private void highlight(WebDriver driver, WebElement element) {
	        try {
	            if (previousElement != null && !previousElement.equals(element)) {
	                removeHighlight(driver, previousElement);
	            }
	            JavascriptExecutor js = (JavascriptExecutor) driver;
	            js.executeScript("arguments[0].style.border='4px solid orange'", element);
	            previousElement = element;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public void handleFrame(WebDriver driver, int index) {
			driver.switchTo().frame(index);
		}

		public void handleFrame(WebDriver driver, String nameOrId) {
			driver.switchTo().frame(nameOrId);
		}

		public void handleFrame(WebDriver driver, By ele) {
			driver.switchTo().frame(driver.findElement(ele));
		}

		public void switchToParentFrame(WebDriver driver) {
			driver.switchTo().parentFrame();
		}

		public void switchToDefaultContent(WebDriver driver) {
			driver.switchTo().defaultContent();
		}
		
		public void handleDynamicDropdown(WebDriver driver,By element, String requiredText) {
			List<WebElement> elements=driver.findElements(element);
			for (int i = 0; i < elements.size(); i++) {
				String values = elements.get(i).getText().trim();
				if (values.equals(requiredText)) {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].click();", elements.get(i));
					break;
				}
			}
		}
		

		public void handleDynamicDropdownWithTyping(
		        By inputFieldBy,
		        By suggestionsBy,
		        String requiredText
		) throws InterruptedException {
		    WebElement inputField = getDriver().findElement(inputFieldBy);
		    // Type character by character
		    for (char c : requiredText.toCharArray()) {
		        inputField.sendKeys(Character.toString(c));
		        Thread.sleep(200);
		    }
		    // Wait for suggestions to populate
		    Thread.sleep(1000);
		    List<WebElement> suggestions = getDriver().findElements(suggestionsBy);

		    // Loop through suggestions and click matching one
		    for (WebElement option : suggestions) {
		        String text = option.getText().trim();
		        if (text.contains(requiredText)) {
		            option.click();
		            break;
		        }
		    }
		}
		
		//It will fetch the current date
		public String appendCurrentDate(String baseName) {
			String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			return baseName + "_" + date;
		}
		
	// Enter Text by using PassLocator
	public void enterText(By PassLocator, String EnterText) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

			// Wait until element is visible
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));
			wait.until(ExpectedConditions.elementToBeClickable(PassLocator));
			highlight(getDriver(), element);
			element.clear(); // optional: clear before typing
			element.sendKeys(EnterText);

			LogInFo("Entered Text on " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to enter text on: " + PassLocator + " | Exception: " + e.getMessage());
			throw e; // rethrow so test fails properly
		}
	}

	// Max width (px) a step screenshot is downscaled to, and the JPEG quality it's
	// saved at, before being embedded as base64. Every report (both Extent HTML
	// reporters, the custom OneHr HTML report and the consolidated PDF) embeds one
	// copy of this per step, so an uncompressed, full-resolution PNG per step is
	// what was pushing whole-suite reports to 5MB+; downscaling + JPEG-compressing
	// once here shrinks every report that consumes it.
	private static final int SCREENSHOT_MAX_WIDTH = 1000;
	private static final float SCREENSHOT_JPEG_QUALITY = 0.5f;

	public static String CaptureScreenShotBase64() throws IOException {
		try {
			Point pos = getDriver().manage().window().getPosition();
			Dimension dim = getDriver().manage().window().getSize();

			int x = pos.getX();
			int y = pos.getY();
			int width = dim.getWidth();
			int height = dim.getHeight();

			Rectangle browserRect = new Rectangle(x, y, width, height);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(browserRect);
			BufferedImage scaled = scaleDown(image, SCREENSHOT_MAX_WIDTH);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writeJpeg(scaled, SCREENSHOT_JPEG_QUALITY, baos);

			return Base64.getEncoder().encodeToString(baos.toByteArray());

		} catch (Exception e) {
			throw new IOException("Failed to capture screenshot", e);
		}
	}

	// Scales the image down proportionally so its width doesn't exceed maxWidth.
	// Returns the source unchanged (still re-drawn onto an RGB buffer, since JPEG
	// can't encode Robot's ARGB output) when it's already narrower than that.
	private static BufferedImage scaleDown(BufferedImage source, int maxWidth) {
		int width = source.getWidth();
		int height = source.getHeight();
		int targetWidth = Math.min(width, maxWidth);
		int targetHeight = Math.round(height * (targetWidth / (float) width));

		BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = scaled.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(source, 0, 0, targetWidth, targetHeight, null);
		g.dispose();
		return scaled;
	}

	// Encodes as JPEG at the given quality (0.0-1.0) instead of ImageIO.write's
	// default, which for "jpg" is a much higher, un-tunable quality.
	private static void writeJpeg(BufferedImage image, float quality, ByteArrayOutputStream out) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
		ImageWriter writer = writers.next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);

		try (ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
			writer.setOutput(ios);
			writer.write(null, new IIOImage(image, null, null), param);
		} finally {
			writer.dispose();
		}
	}
	// elementLocator must be resolved to a real WebElement first - executeScript
	// only auto-converts WebElement arguments into their DOM element in the
	// script; passing a By locator directly (as this used to) doesn't work.
	public void sendKeysWithJavaScript(WebDriver driver, By elementLocator, String value) {
		try {
			WebElement el = driver.findElement(elementLocator);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView(true);", el);

			// A plain "el.value = x" (or sometimes even a real sendKeys, on a
			// controlled input wrapped by a framework like React) can get
			// silently wiped on the field's next re-render, because the
			// framework's own tracked state was never actually updated - the
			// DOM shows the value for a moment, then it reverts to empty.
			// Going through the <input> element's native value setter
			// (bypassing the framework's overridden one) before dispatching a
			// real 'input'/'change' event is the standard workaround.
			js.executeScript(
					"const nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;"
					+ "nativeSetter.call(arguments[0], arguments[1]);"
					+ "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
					+ "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
					el, value);
		} catch (Exception jsEx) {
			System.out.println("JavaScript sendKeys failed: " + jsEx.getMessage());
		}
	}

	public void DynamicXpathContains(String Text) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			By locator = By.xpath("//*[contains(text(),'" + Text + "')]");

			// Wait for element to be visible
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

			// Wait until element is clickable
			wait.until(ExpectedConditions.elementToBeClickable(locator));
			highlight(getDriver(), element);
			element.click();

			LogInFo("Clicked on DynamicXpathContains element: " + Text);

		} catch (Exception e) {
			LogInFo("Failed to click DynamicXpathContains element: " + Text + " | Exception: " + e.getMessage());
			throw e;
		}

	}

	public void DynamicXpathText(String Text) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			By locator = By.xpath("//*[text()='" + Text + "']");

			// Wait for element to be visible
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

			// Wait for element to be clickable
			wait.until(ExpectedConditions.elementToBeClickable(locator));
			highlight(getDriver(), element);
			// Click the element
			element.click();

			LogInFo("Clicked on DynamicXpathText element: " + Text);

		} catch (Exception e) {
			LogInFo("Failed to click DynamicXpathText element: " + Text + " | Exception: " + e.getMessage());
			throw e;
		}

	}

	// Clear Text Method
	public void clearText(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait for element to be visible
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(PassLocator));

			// Wait until element is clickable
			wait.until(ExpectedConditions.elementToBeClickable(PassLocator));
			highlight(getDriver(), element);
			// Clear the field
			element.clear();

			LogInFo("Cleared text on: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to clear text on: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}

	}

	// Click Button
	public void clickOnButton(By locator) {
		// Up to one retry if the element goes stale between being located and
		// being clicked (e.g. a table row re-rendering) - re-locating from
		// scratch on retry, not reusing anything from the failed attempt.
		for (int attempt = 1; attempt <= 2; attempt++) {
			try {
				WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));

				// Wait until visible, then wait until clickable and click THAT
				// (fresher) reference - not the one from the visibility wait.
				// elementToBeClickable() re-locates the element itself; discarding
				// its result and clicking the earlier reference is what let a
				// StaleElementReferenceException happen when the page re-rendered
				// in the gap between the two waits.
				wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
				highlight(getDriver(), element);
				element.click();

				LogInFo("Clicked on button: " + locator);
				return;

			} catch (StaleElementReferenceException e) {
				if (attempt == 2) {
					LogInFo("Failed to click button: " + locator + " | Exception: " + e.getMessage());
					throw e;
				}
				LogInFo("Stale element clicking " + locator + " - retrying once.");

			} catch (ElementClickInterceptedException e) {
				// Some transient floating UI (a toast, an assistant-invite
				// popup, a dialog's backdrop mid-close, etc. - confirmed live:
				// a fixed-position "nf-assistant-invite-in" toast covering a
				// row's Actions button) can sit on top of the target at the
				// exact moment of a native click, without ever being something
				// this test is meant to interact with. A JS click bypasses
				// that visual-overlap check entirely, so fall back to it
				// instead of failing the whole test over an unrelated popup.
				LogInFo("Click intercepted on " + locator + " - falling back to JS click.");
				ClickByJavaScript_Executor(locator);
				return;

			} catch (Exception e) {
				LogInFo("Failed to click button: " + locator + " | Exception: " + e.getMessage());
				throw e; // rethrow to fail test
			}
		}
	}

	// GET TEXT METHOD
	public String getText(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait until element is visible
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));

			// Get text
			highlight(getDriver(),element);
			String Text = element.getText();
			System.out.println(Text);
			LogInFo("Retrieved text: " + Text + " from element: " + PassLocator);

			return Text;

		} catch (Exception e) {
			LogInFo("Failed to get text from element: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}

	// GET ATTRIBUTE
	public String getPlaceholder(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait until element is visible
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));
			// Get placeholder attribute
			String placeholder = element.getAttribute("placeholder");
			LogInFo("Retrieved placeholder: " + placeholder + " from element: " + PassLocator);

			return placeholder;

		} catch (Exception e) {
			LogInFo("Failed to get placeholder from element: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}

	// BY USING TEXT OF DROPDOWN.
	public void selectTextOnDropDown(By PassLocator, String EnterText) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait until dropdown is visible
			WebElement dropdownElement = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));
		
			// Select by visible text
			Select selectValue = new Select(dropdownElement);
			highlight(getDriver(), dropdownElement);
			selectValue.selectByVisibleText(EnterText);

			LogInFo("Selected '" + EnterText + "' from dropdown: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to select '" + EnterText + "' from dropdown: " + PassLocator + " | Exception: "
					+ e.getMessage());
			throw e;
		}
	}

	public static int IndexNumber;

	// BY USING INDEX OF DROPDOWN.
	public void selectIndexeOnDropDown(By PassLocator, int IndexNumber) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait until dropdown is visible
			WebElement dropdownElement = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));
	
			Select selectValue = new Select(dropdownElement);
			highlight(getDriver(), dropdownElement);
			selectValue.selectByIndex(IndexNumber);

			LogInFo("Selected index '" + IndexNumber + "' from dropdown: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to select index '" + IndexNumber + "' from dropdown: " + PassLocator + " | Exception: "
					+ e.getMessage());
			throw e;
		}
	}

	// BY USING VALUE OF DROPDOWN.
	public void selectValueOnDropDown(By PassLocator, String EnterText) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait until dropdown is visible
			WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));
			Select selectValue = new Select(dropdown);
			highlight(getDriver(), dropdown);
			selectValue.selectByValue(EnterText);

			LogInFo("Selected value '" + EnterText + "' from dropdown: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to select value '" + EnterText + "' from dropdown: " + PassLocator + " | Exception: "
					+ e.getMessage());
			throw e;
		}
	}

	// CHECK VALUE IN CHECK BOX
	public void selectValueOnCheckBox(By chkBoxCollection, By labelText, By getInput, String EnterText) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait until at least one checkbox is visible
			wait.until(ExpectedConditions.presenceOfElementLocated(chkBoxCollection));

			List<WebElement> chkBoxCollections = getDriver().findElements(chkBoxCollection);

			for (WebElement webElement : chkBoxCollections) {
				WebElement getLabel = webElement.findElement(labelText);
				WebElement getInput1 = webElement.findElement(getInput);

				if (getLabel.getText().equals(EnterText)) {
					if (!getInput1.isSelected()) {
						getLabel.click();
						LogInFo("Selected checkbox: " + EnterText);
					} else {
						LogInFo("Checkbox already selected: " + EnterText);
					}
					break;
				}
			}

		} catch (Exception e) {
			LogInFo("Failed to select checkbox: " + EnterText + " | Exception: " + e.getMessage());
			throw e;
		}
	}

	/// SWITCH TABS
	public void switchTabs() {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Get the current window handle (parent window)
			String parentWindow = getDriver().getWindowHandle();

			// Wait until there is more than 1 window handle
			wait.until(driver -> driver.getWindowHandles().size() > 1);

			// Get all window handles (parent + child windows)
			Set<String> windowHandles = getDriver().getWindowHandles();

			for (String childWindow : windowHandles) {
				if (!parentWindow.equals(childWindow)) {
					// Switch to child window
					getDriver().switchTo().window(childWindow);

					// Optional: perform actions in child window here

					// Close child window
					getDriver().close();
				}
			}

			// Switch back to the parent window
			getDriver().switchTo().window(parentWindow);

			LogInFo("Switched back to parent window: " + parentWindow);

		} catch (Exception e) {
			LogInFo("Failed to switch tabs | Exception: " + e.getMessage());
			throw e;
		}
	}

	// WAIT FOR ELEMENT VISIBLE
	public void waitForElementVisible(By passLocator) {
		waitForElementVisible(passLocator, 60);
	}

	public void waitForElementVisible(By passLocator, long timeoutSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passLocator));
			highlight(getDriver(), element);
			LogInFo("Element is visible: " + passLocator);

		} catch (Exception e) {
			LogInFo("Element not visible: " + passLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}
	
	public void waitforMultipleElements(By passlocator)
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(50));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(passlocator));
	}
	public void waitForElementToInvisible(By passLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(50));
			boolean element = wait.until(ExpectedConditions.invisibilityOfElementLocated(passLocator));
			if(element==true)
			{
			LogInFo("Element is Invisible: " + passLocator);
			}
		} catch (Exception e) {
			LogInFo("Element is not Invisible: " + passLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}
	
	//Validate the values in dropdown
	public Set<String> validateAndReturnStaticDropdownValues(By PassLocator) {
		WebElement element=getDriver().findElement(PassLocator);
		SortedSet<String> dropdownSet = new TreeSet<>();
		boolean hasDuplicates = false;
		Select s = new Select(element);
		List<WebElement> dropdownvalues = s.getOptions();
		for (WebElement option : dropdownvalues) {
			String value = option.getText().trim();
			// Try to add to the set. If it already exists, Set.add() returns false.
			if (!dropdownSet.add(value)) {
				System.err.println("❌ Duplicate found in dropdown: " + value);
				hasDuplicates = true;
			}
		}
		if (hasDuplicates) {
			throw new RuntimeException("Dropdown contains duplicate values.");
		} else {
			System.out.println("✅ No duplicates found in dropdown.");
		}

		return dropdownSet;
	}
	

	//Validate select dropdown values
	public boolean validateselectdropdown(By locator, String[] expectedValues) {

	    WebElement element = getDriver().findElement(locator);
	    Select select = new Select(element);
	    List<WebElement> options = select.getOptions();

	    // Capture actual dropdown values from UI
	    List<String> actualValues = new ArrayList<>();
	    for (WebElement option : options) {
	        actualValues.add(option.getText().trim());
	    }

	    // Convert expected array to list
	    List<String> expectedList = Arrays.asList(expectedValues);

	    // Sort both lists before comparison (ignores order difference)
	    Collections.sort(actualValues);
	    Collections.sort(expectedList);

	    return actualValues.equals(expectedList);
	}


	// WAIT FOR ELEMENT TO BE CLICKABLE
	public void waitForElementToBeClickable(By passLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(passLocator));

			LogInFo("Element is clickable: " + passLocator);

		} catch (Exception e) {
			LogInFo("Element not clickable: " + passLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}
	// WAIT FOR ELEMENT LOCATED AND VISIBLE
	public void waitForElementLocated(By passLocator) {
	    try {
	        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(50));

	        // Wait until element is visible in the viewport
	        wait.until(ExpectedConditions.visibilityOfElementLocated(passLocator));

	        LogInFo("Element is visible in DOM: " + passLocator);

	    } catch (Exception e) {
	        LogInFo("Element not found or not visible in DOM: " + passLocator + " | Exception: " + e.getMessage());
	        throw e;
	    }
	}

	// BY USING JAVA SCRIPT EXECUTOR OF CLICKING BUTTON
	public void clickByJS(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

			// Wait for element to be present and visible
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(PassLocator));
			// Click via JS
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);

			LogInFo("Clicked on element using JS: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to click element using JS: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}

	// MAXIMIZE THE SCREEN
	public void MaximizeScreen() {
		try {
			getDriver().manage().window().maximize();
			LogInFo("Browser window maximized");
		} catch (Exception e) {
			LogInFo("Failed to maximize browser window | Exception: " + e.getMessage());
			throw e;
		}
	}

	// Passing random alphabetic
	public String generateRandomString(int Length) {
		return RandomStringUtils.randomAlphabetic(Length);
	}

	// Passing random numbers
	public String generateRandomNumber(int Length) {
		return RandomStringUtils.randomNumeric(Length);
	}

	// Passing alphanumeric
	public static String generateRandomAlphanumeric(int Length) {
		return RandomStringUtils.randomAlphanumeric(Length);
	}

	// --------------------------------------------------------------------------------//
	public static int Seconds;

	// Wait for some time (kept as-is)
	public void WaitForSometime(int Seconds) throws InterruptedException {
		Thread.sleep(Seconds);
	}

	// Element To Be Clickable
	public void WaitUntilElementToBeClickable(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));

			// Wait until the element is clickable
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(PassLocator));

			element.click();

			LogInFo("Clicked element after waiting to be clickable: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to wait and click element: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}
	public void WaitForEnterprisePageLoad(By loaderLocator) {
		 try {
		 WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5)); 
		 // 1. Wait for loader to appear (if it appears quickly) 
		 wait.until(ExpectedConditions.presenceOfElementLocated(loaderLocator)); 
		 // 2. Wait for loader to disappear 
		 wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator)); 
		 LogInFo("Enterprise application page fully loaded.");
		 }
		 catch (Exception e) {
		 // If loader never appears, still fine
		 LogInFo("Loader did not appear or page load wait issue | " + e.getMessage());
		 } 
		 }

	// Presence of Element Located
	public void PresenceOfElementLocated(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));

			// Wait until the element is present in DOM
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(PassLocator));

			LogInFo("Element is present in DOM: " + PassLocator + " | Text: " + element.getText());

		} catch (Exception e) {
			LogInFo("Element not present in DOM: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}
	
	

	// Capturing ScreenSHot
	public String CaptureScreenShot(String filename) throws IOException {
		File ts = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		// File srcFile=ts.getScreenshotAs(OutputType.FILE);
		String destFile = "./ExecutionResult/test-screenshots" + dt + "/" + filename + ".png";
		File Path = new File(destFile);
		FileUtils.copyFile(ts, Path);
		return destFile;

	}

	// Close the Current window
	public void CloseCurrentWindow() {
		try {
			getDriver().close();
			LogInFo("Closed the current window successfully.");
		} catch (Exception e) {
			LogInFo("Failed to close the current window | Exception: " + e.getMessage());
			throw e;
		}
	}

	// Close All Windows
	public void CloseAllWinows() {
		try {
			getDriver().quit();
			LogInFo("Closed all browser windows successfully.");
		} catch (Exception e) {
			LogInFo("Failed to close all windows | Exception: " + e.getMessage());
			throw e;
		}
	}

	// ------------------------------------------------------------------------------------------------------------//
	// SCrolling
	// I Scroll Down The Page
	public static void ScrollThePage() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,3000)"); // Vertical Scroll
			LogInFo("Scrolled the page down by 3000 pixels.");
		} catch (Exception e) {
			LogInFo("Failed to scroll the page | Exception: " + e.getMessage());
			throw e;
		}
	}

	// Scroll to a particular element
	public void ScrollParticularElement(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(PassLocator));

			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);

			LogInFo("Scrolled to element: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to scroll to element: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}

	// Click element using JavaScript Executor
	public void ClickByJavaScript_Executor(By PassLocator) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			WebElement button = wait.until(ExpectedConditions.elementToBeClickable(PassLocator));

			((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", button);

			LogInFo("Clicked element using JS Executor: " + PassLocator);

		} catch (Exception e) {
			LogInFo("Failed to click element using JS Executor: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}

	// Assertion: Check if element is present or not
	public void AssertElementIsPresentOrNot(By PassLocator, String FolderName, String ImageName) throws IOException {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(PassLocator));

			String Text = element.getAttribute("value");
			System.out.println(Text);

			if (Text.equals(ImageName)) {
				CaptureScreenShot(ImageName);
				LogInFo("Assertion passed: Element matches expected value '" + ImageName + "'");
			} else {
				System.out.println("Assertion failed: " + ImageName + " Element is not as expected.");
				CaptureScreenShot(ImageName);
				getDriver().close();
				LogInFo("Closed driver due to failed assertion.");
			}

		} catch (Exception e) {
			LogInFo("Assertion failed for element: " + PassLocator + " | Exception: " + e.getMessage());
			throw e;
		}
	}

	// I_Enter_Value_By_Using_JavaScriptExecutor
	public void EnterValueByUsingJSE(By PassLocator, String data) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			WebElement userNameTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(PassLocator));

			// Set the value using JS
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].value='" + data + "'", userNameTxt);

			// Get the value to confirm
			String text = (String) ((JavascriptExecutor) getDriver()).executeScript("return arguments[0].value",
					userNameTxt);
			LogInFo("Entered value using JS Executor: " + text);

		} catch (Exception e) {
			LogInFo("Failed to enter value using JS Executor on element: " + PassLocator + " | Exception: "
					+ e.getMessage());
			throw e;
		}
	}

	// I_Verify_Element is present or not
	public boolean VerifyElementIsPresentorNot(By PassLocator) throws InterruptedException, IOException {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
			WebElement ele = wait.until(ExpectedConditions.visibilityOfElementLocated(PassLocator));

			if (ele.isDisplayed()) {
				LogInFo("Element is present: " + PassLocator);
				return true;
			} else {
				LogInFo("Element is not present: " + PassLocator);
				return false;
			}

		} catch (Exception e) {
			LogInFo("Failed to verify element presence: " + PassLocator + " | Exception: " + e.getMessage());
			return false;
		}
	}

	// Verify the page title
	public void verifyTitleOfThePage(String expectedTitle) {
		try {
			String actualTitle = getDriver().getTitle();
			System.out.println("Expected Title: " + expectedTitle);
			System.out.println("Actual Title: " + actualTitle);

			Assert.assertEquals(actualTitle, expectedTitle, "Page title does not match!");
			LogInFo("Page title verified successfully: " + actualTitle);

		} catch (AssertionError e) {
			LogInFo("Page title verification failed. Expected: " + expectedTitle + " | Actual: " + getDriver().getTitle());
			throw e;
		} catch (Exception e) {
			LogInFo("Error while verifying page title | Exception: " + e.getMessage());
			throw e;
		}
	}

	// Zoom out the screen using Robot (~80%)
	public void screenzoomout() {
		try {
			Robot robot = new Robot();

			for (int i = 0; i < 1; i++) { // Zoom out 3 times
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_SUBTRACT); // or VK_MINUS
				robot.keyRelease(KeyEvent.VK_SUBTRACT);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				Thread.sleep(500);
			}

			LogInFo("Screen zoomed out (~80%) successfully.");

		} catch (InterruptedException | AWTException e) {
			LogInFo("Failed to zoom out screen | Exception: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	// Customer correlation zoom out (~1 iteration)
	public void CustomerCorrelationscreenzoomout() {
		try {
			Robot robot = new Robot();

			for (int i = 0; i < 1; i++) { // Zoom out once
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_SUBTRACT); // or VK_MINUS
				robot.keyRelease(KeyEvent.VK_SUBTRACT);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				Thread.sleep(500);
			}

			LogInFo("Customer correlation screen zoomed out successfully.");

		} catch (InterruptedException | AWTException e) {
			LogInFo("Failed to zoom out customer correlation screen | Exception: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void validateSelectedDropdownText(By dropdown, String expectedText) {
		Select select = new Select(getDriver().findElement(dropdown));
		String actualText = select.getFirstSelectedOption().getText().trim();
		Assert.assertEquals(actualText, expectedText, "Dropdown selected value mismatch");
	}
	public void Click_N_EnterData(By element, String Value) {
		WebElement Element=getDriver().findElement(element);
		Element.click();
		Element.sendKeys(Value);
	}
	
	
	public boolean areDropdownValuesValid(
            WebDriver driver,
            By dropdownLocator,
            String[] expectedValues) {

        WebElement dropdownElement = driver.findElement(dropdownLocator);
        Select select = new Select(dropdownElement);

        List<String> actualValues = select.getOptions()
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());

        List<String> expectedList = Arrays.stream(expectedValues)
                .map(String::trim)
                .collect(Collectors.toList());

        boolean status = true;

        // ---- Print Expected vs Actual ----
        System.out.println("Expected Dropdown Values : " + expectedList);
        System.out.println("Actual Dropdown Values   : " + actualValues);

        // ---- Check missing values ----
        List<String> missing = expectedList.stream()
                .filter(e -> !actualValues.contains(e))
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            System.out.println("❌ Missing Values : " + missing);
            status = false;
        }

        // ---- Check duplicates ----
        Set<String> unique = new HashSet<>();
        List<String> duplicates = actualValues.stream()
                .filter(v -> !unique.add(v))
                .collect(Collectors.toList());

        if (!duplicates.isEmpty()) {
            System.out.println("❌ Duplicate Values Found : " + duplicates);
            status = false;
        }

        // ---- Final result ----
        if (status) {
            System.out.println("✅ Dropdown validation PASSED");
        } else {
            System.out.println("❌ Dropdown validation FAILED");
        }

        return status;
    }
	
	public boolean areCustomDropdownValuesValid(WebDriver driver,
            By dropdownValuesLocator,
            String[] expectedValues) throws InterruptedException {
        Thread.sleep(2000);
        // Capture all dropdown values
        List<WebElement> elements = driver.findElements(dropdownValuesLocator);

        List<String> actualValues = elements.stream()
                .map(e -> e.getText().trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());

        List<String> expectedList = Arrays.stream(expectedValues)
                .map(String::trim)
                .collect(Collectors.toList());

        System.out.println("Expected : " + expectedList);
        System.out.println("Actual   : " + actualValues);

        boolean status = true;

        // Check missing values
        List<String> missing = expectedList.stream()
                .filter(e -> !actualValues.contains(e))
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            System.out.println("Missing values : " + missing);
            status = false;
        }

        // Check duplicates in actual dropdown
        Set<String> unique = new HashSet<>();
        List<String> duplicates = actualValues.stream()
                .filter(v -> !unique.add(v))
                .collect(Collectors.toList());

        if (!duplicates.isEmpty()) {
            System.out.println("Duplicate values : " + duplicates);
            status = false;
        }

        return status;
    }
	
	public  boolean validateTableColumns(WebDriver driver, By headerLocatoror, String[] expectedColumns) {

	    List<WebElement> headers = driver.findElements(headerLocatoror);
	    List<String> actualColumns = headers.stream()
	            .map(e -> e.getText().trim())
	            .toList();

	    boolean allMatched = true;

	    for (String expected : expectedColumns) {
	        if (actualColumns.contains(expected.trim())) {
	            System.out.println("MATCHED COLUMN : " + expected);
	        } else {
	            System.out.println("NOT MATCHED COLUMN : " + expected);
	            allMatched = false;
	        }
	    }

	    return allMatched;
	}

	
}
