package Tools;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloSelenium {

    private static WebDriver driver;

    @BeforeAll
    public static void setUpChrome() {
        // Selenium Manager handles ChromeDriver automatically.
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void googleSearchExample() {
        // Arrange
        driver.get("https://www.google.com");

        WebElement searchBox =
                driver.findElement(By.name("q"));

        // Act
        searchBox.clear();
        searchBox.sendKeys("GGC");
        searchBox.submit();

        // Wait until the title contains the search term.
        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.titleContains("GGC"));

        // Assert
        String pageTitle = driver.getTitle();
        System.out.println("Page title is: " + pageTitle);

        assertTrue(
                pageTitle.contains("GGC"),
                "Expected GGC in the page title"
        );
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
