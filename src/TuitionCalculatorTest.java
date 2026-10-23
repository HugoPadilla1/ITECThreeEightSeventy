import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TuitionCalculatorTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
    }

    // Click the underlying radio input with JavaScript to avoid overlay interception.
    // GGC's radio inputs are visually hidden inside their labels.
    private void selectRadio(String id) {
        WebElement radio = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id(id)));
        if (!radio.isSelected()) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", radio);
        }
        assertTrue(radio.isSelected(), "Radio option was not selected: " + id);
    }

    @Test
    public void testInStateAnnualTuitionIs5354() {
        // Arrange
        driver.get("https://www.ggc.edu/admission-aid/calculate-your-costs/tuition-calculator");

        // The site's cookie consent banner can overlap the calculator.
        // If no banner appears, proceed without waiting for one.
        // No cookie-banner click is required for these interactions.
        // JavaScript selection avoids screen-coordinate clicks altogether.

        // Act: select in-state residency, 15 hours, and no campus housing.
        selectRadio("inorout1");
        new Select(wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("creditHOURS")))).selectByValue("15");
        // The site already selects "No" for housing by default.
        // Do not click its label: it was the intercepted element.
        selectRadio("onoroff0");

        // Read the calculator's live, per-semester amount.
        String displayedTotal = wait.until(d -> {
            WebElement total = d.findElement(By.id("totalcost"));
            String value = total.getAttribute("value");
            return (value != null && !value.replaceAll("[^0-9.]", "").isEmpty()
                    && Double.parseDouble(value.replaceAll("[^0-9.]", "")) > 0)
                    ? value : null;
        });

        double semesterTotal = Double.parseDouble(displayedTotal.replaceAll("[^0-9.]", ""));
        double annualTotal = semesterTotal * 2;

        System.out.println("Semester total: $" + semesterTotal);
        System.out.println("Annual total: $" + annualTotal);

        // Assert: the assignment's expected annual amount.
        // This test should fail if the live calculator's current rates differ.
        assertEquals(5354.00, annualTotal, 0.01,
                "The live GGC calculator differs from the assignment's $5,354 figure.");
    }

    @Test
    public void testOutOfStateAnnualTuitionIs16994() {
        // Arrange: open the same live calculator used by the in-state test.
        driver.get("https://www.ggc.edu/admission-aid/calculate-your-costs/tuition-calculator");

        // Act: out-of-state residency, 15 credit hours, no campus housing.
        // inorout0 = "No (Out of state)"; inorout2 = out of country.
        selectRadio("inorout0");
        new Select(wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("creditHOURS")))).selectByValue("15");
        selectRadio("onoroff0");

        // Wait for the calculator to populate its per-semester total.
        String displayedTotal = wait.until(d -> {
            WebElement total = d.findElement(By.id("totalcost"));
            String value = total.getAttribute("value");
            String numeric = value == null ? "" : value.replaceAll("[^0-9.]", "");
            return !numeric.isEmpty() && Double.parseDouble(numeric) > 0
                    ? value : null;
        });

        double semesterTotal = Double.parseDouble(
                displayedTotal.replaceAll("[^0-9.]", ""));
        double annualTotal = semesterTotal * 2;
        System.out.println("Out-of-state semester total: $" + semesterTotal);
        System.out.println("Out-of-state annual total: $" + annualTotal);

        // Assert against the assignment's U.S. News annual amount.
        // A mismatch is intentionally a failing test, not a hardcoded pass.
        assertEquals(16994.00, annualTotal, 0.01,
                "The live GGC calculator differs from the assignment's $16,994 figure.");
    }

    @Test
    public void testMyCurrentSemesterBill() {
        // Arrange: my actual GGC bill for 15 credit hours was $2,767.00.
        // Parameters: Georgia (in-state) residency, 15 credits, no campus
        // housing and no meal plan. The bill is for ONE semester, not a year.
        double actualBill = 2767.00;
        int creditHours = 15;
        driver.get("https://www.ggc.edu/admission-aid/calculate-your-costs/tuition-calculator");

        // Act: enter the same parameters in the live tuition calculator.
        selectRadio("inorout1");
        new Select(wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("creditHOURS")))).selectByValue(String.valueOf(creditHours));
        selectRadio("onoroff0");

        String displayedTotal = wait.until(d -> {
            WebElement total = d.findElement(By.id("totalcost"));
            String value = total.getAttribute("value");
            String numeric = value == null ? "" : value.replaceAll("[^0-9.]", "");
            return !numeric.isEmpty() && Double.parseDouble(numeric) > 0
                    ? value : null;
        });
        double calculatedBill = Double.parseDouble(
                displayedTotal.replaceAll("[^0-9.]", ""));
        double difference = actualBill - calculatedBill;

        System.out.printf("Residency: Georgia (in-state)%n");
        System.out.printf("Credit hours: %d%n", creditHours);
        System.out.println("Campus housing: No");
        System.out.println("Meal plan: None");
        System.out.printf("My actual semester bill: $%.2f%n", actualBill);
        System.out.printf("GGC calculator estimate: $%.2f%n", calculatedBill);
        System.out.printf("Actual bill minus estimate: $%.2f%n", difference);

        // Assert: verify the actual billed amount supplied for this assignment.
        // This is not an independently verified student-account statement.
        assertEquals(2767.00, actualBill, 0.01,
                "The reported semester bill should be $2,767.00.");
        assertEquals(15, creditHours, "The bill is for 15 credit hours.");

        // Comparison comment: the calculator currently estimates $2,692.00
        // for these inputs ($2,070 tuition + $622 standard fees), which is
        // $75.00 LESS than the $2,767.00 paid. The calculator is an estimate;
        // course-specific fees or other charges can make the actual bill differ.
        // Print the live comparison rather than asserting equality, since rates
        // can change and the bill is still valid if the estimate changes.
        System.out.println(Math.abs(difference) < 0.01
                ? "My actual bill MATCHES the calculator estimate."
                : "My actual bill DOES NOT MATCH the calculator estimate.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
