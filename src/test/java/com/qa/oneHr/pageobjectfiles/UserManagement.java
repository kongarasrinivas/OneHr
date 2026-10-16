package com.qa.oneHr.pageobjectfiles;

import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class UserManagement {

    WebDriver driver;
    ReusableMethods reusableMethods;
    TestContextSetup testContext;

    public UserManagement(WebDriver driver, ReusableMethods reusablemethods, TestContextSetup testcontext) {
        this.driver = driver;
        this.reusableMethods = reusablemethods;
        this.testContext = testcontext;
    }

    public final By signOut =By.xpath("//*[normalize-space()='Sign out']");
    public final By searchBox =By.xpath("//input[contains(@placeholder,'Search by name, email')]");
    public final By actions =By.xpath("(//button[@aria-label='Row actions'])[1]");
    public final By deleteBtn =By.xpath("//button[text()='Delete']");
    public final By editBtn = By.xpath("//button[text()='Edit']");
    public final By saveChangesBtn = By.xpath("//button[normalize-space()='Save Changes']");
    public final By employeId =By.xpath("//table//tbody//tr//td[1]");
    public final By emailToDelete =By.xpath("//input[contains(@placeholder,'.com')]");
    public final By firstRowEmailCell = By.xpath("//table//tbody//tr//td[3]");
    public final By confirmDeleteEmailInput = By.xpath("//label[normalize-space()=\"Type the user's email to confirm\"]/following-sibling::input[1]");
    public final By confirmDeleteBtn = By.xpath("//label[normalize-space()=\"Type the user's email to confirm\"]/following::button[normalize-space()='Delete Permanently'][1]");
    public final By tableRows = By.xpath("//table//thead/following-sibling::tbody//tr");
    public final By deactivate = By.xpath("//button[text()='Deactivate']");
    // The confirmation popup's button has the same text as the Actions-menu item;
    // the popup is rendered after it in the DOM, so take the last match.
    public final By confirmDeactivateBtn = By.xpath("(//button[normalize-space()='Deactivate'])[last()]");


    private By rowNameCell(int rowIndex) {
        return By.xpath("//table//thead/following-sibling::tbody//tr[" + rowIndex + "]//td[2]");
    }

    private By rowEmailCell(int rowIndex) {
        return By.xpath("//table//thead/following-sibling::tbody//tr[" + rowIndex + "]//td[3]");
    }

    private By rowActionsBtn(int rowIndex) {
        return By.xpath("//table//thead/following-sibling::tbody//tr[" + rowIndex + "]//button[@aria-label='Actions']");
    }

    public void searchtxtBox(String searchWith)
    {
        reusableMethods.waitForElementVisible(searchBox);
        reusableMethods.enterText(searchBox,searchWith);
    }

    public void clickActions()
    {
        reusableMethods.waitForElementVisible(actions);
        reusableMethods.clickOnButton(actions);
    }

public void clickDeactivate()
{
   reusableMethods.waitForElementVisible(deactivate);
   reusableMethods.clickOnButton(deactivate);
}

    public void clickConfirmDeactivate()
    {
        reusableMethods.waitForElementVisible(confirmDeactivateBtn);
        reusableMethods.clickOnButton(confirmDeactivateBtn);
    }

    public void clickDelete()
    {
        reusableMethods.waitForElementVisible(deleteBtn);
        reusableMethods.clickOnButton(deleteBtn);
    }


    public void enterCnfrmEmialDlt(String email)
    {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Cannot confirm delete: the email fetched from the table row was empty. "
                + "Check that the row's email column locator (rowEmailCell / firstRowEmailCell, td[3]) "
                + "actually holds the email text for this row.");
        }

        reusableMethods.waitForElementVisible(confirmDeleteEmailInput);
        reusableMethods.enterText(confirmDeleteEmailInput, email);

        String typedValue = reusableMethods.getInputFieldValue(confirmDeleteEmailInput);
        if (typedValue == null || !typedValue.trim().equals(email.trim())) {
            System.out.println("Confirm-email field read back \"" + typedValue
                    + "\" instead of \"" + email + "\" - retrying once.");
            reusableMethods.enterText(confirmDeleteEmailInput, email);
            typedValue = reusableMethods.getInputFieldValue(confirmDeleteEmailInput);
        }

        if (typedValue == null || !typedValue.trim().equals(email.trim())) {
            System.out.println("Confirm-email field still shows \"" + typedValue
                    + "\" after 2 sendKeys attempts - falling back to JS value set.");
            reusableMethods.sendKeysWithJavaScript(driver, confirmDeleteEmailInput, email);
            typedValue = reusableMethods.getInputFieldValue(confirmDeleteEmailInput);
        }

        if (typedValue == null || !typedValue.trim().equals(email.trim())) {
            throw new IllegalStateException(
                "Confirm-email field still shows \"" + typedValue + "\" after typing \"" + email
                + "\" via sendKeys (twice) and JS fallback - the delete cannot be confirmed.");
        }
    }

    public void clickDeletePermanently()
    {
        reusableMethods.waitForElementVisible(confirmDeleteBtn);
        reusableMethods.clickOnButton(confirmDeleteBtn);
        reusableMethods.waitForElementToInvisible(confirmDeleteEmailInput);
    }

    public String getEmployeeId()
    {
        reusableMethods.waitForElementVisible(employeId);
        String actualEmplyId = reusableMethods.getText(employeId);

        return actualEmplyId;
    }


    public String getFirstRowEmail() {
        reusableMethods.waitForElementVisible(firstRowEmailCell);
        return reusableMethods.getText(firstRowEmailCell);
    }


    public void clickEdit() {
        reusableMethods.waitForElementVisible(editBtn);
        reusableMethods.clickOnButton(editBtn);
    }


    public void clickSaveChanges() {
        reusableMethods.waitForElementVisible(saveChangesBtn);
        reusableMethods.clickOnButton(saveChangesBtn);
    }


    public int getRowCount() {
        reusableMethods.waitForElementVisible(tableRows);
        return driver.findElements(tableRows).size();
    }


    public String getRowName(int rowIndex) {
        By nameCell = rowNameCell(rowIndex);
        reusableMethods.waitForElementVisible(nameCell);
        return reusableMethods.getText(nameCell);
    }


    public String getRowEmail(int rowIndex) {
        By emailCell = rowEmailCell(rowIndex);
        reusableMethods.waitForElementVisible(emailCell);
        return reusableMethods.getText(emailCell);
    }


    public void clickRowActions(int rowIndex) {
        By row = By.xpath("//table//thead/following-sibling::tbody//tr[" + rowIndex + "]");
        reusableMethods.waitForElementVisible(row);
        WebElement rowElement = driver.findElement(row);

        // The 3-dots button can stay hidden until the row is hovered.
        new Actions(driver).moveToElement(rowElement).perform();

        List<WebElement> buttons = rowElement.findElements(By.xpath(".//button[@aria-label='Actions']"));
        if (buttons.isEmpty()) {
            buttons = rowElement.findElements(By.xpath(".//td[last()]//button"));
        }
        if (buttons.isEmpty()) {
            System.out.println("Row " + rowIndex + " HTML: " + rowElement.getAttribute("outerHTML"));
            throw new NoSuchElementException("No 3-dots (Actions) button found in row " + rowIndex
                    + " - see the row HTML printed above.");
        }

        WebElement threeDots = buttons.get(0);
        try {
            new Actions(driver).moveToElement(threeDots).click().perform();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", threeDots);
        }
    }

    // Reads the whole text of the row holding the given email, e.g. to check its status.
    public String getRowTextByEmail(String email) {
        int rowCount = getRowCount();
        for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
            String actualEmail = getRowEmail(rowIndex);
            if (actualEmail != null && actualEmail.trim().equalsIgnoreCase(email.trim())) {
                return driver.findElement(By.xpath("//table//thead/following-sibling::tbody//tr[" + rowIndex + "]")).getText();
            }
        }
        return null;
    }


    public void deleteAllRowsInTable() {

        int rowCount = getRowCount();
        System.out.println("User Management table row count before delete-all: " + rowCount);

        for (int i = 1; i <= rowCount; i++) {

            String email = getFirstRowEmail();
            System.out.println("Deleting row " + i + " of " + rowCount + " -> Email: " + email);

            clickActions();
            clickDelete();
            enterCnfrmEmialDlt(email);
            clickDeletePermanently();
        }
    }
}
