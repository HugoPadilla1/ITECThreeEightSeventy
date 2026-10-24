import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SocialSecurityTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver, Duration.ofSeconds(20)
        );

        wait.ignoring(StaleElementReferenceException.class);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1990, 55000, 2445.00",
            "1980, 65000, 3169.00",
            "1970, 75000, 3648.00"
    })
    public void testSocialSecurityBenefits(
            int birthYear,
            int earnings,
            double expectedBenefit) {

        // ARRANGE
        driver.get("https://www.ssa.gov/OACT/quickcalc/");

        int retirementYear = birthYear + 67;

        // ACT - Fill calculator inputs
        fillField("month", "01");
        fillField("day", "01");
        fillField("year", String.valueOf(birthYear));
        fillField("earnings", String.valueOf(earnings));

        // Retirement at age 67
        fillField("retiremonth", "01");
        fillField("retireyear",
                String.valueOf(retirementYear));

        driver.findElement(By.id("constant")).click();

        System.out.println("\n-------------------------");
        System.out.println("Birth Year: " + birthYear);
        System.out.println("Annual Earnings: $" + earnings);
        System.out.println("Retirement: 01/" + retirementYear);
        System.out.println("Retirement Age: 67");

        // Click EXACT Submit request button
        By submitButton = By.xpath(
                "//input[@type='submit' and @value='Submit request']"
        );

        WebElement submit = wait.until(
                ExpectedConditions.elementToBeClickable(
                        submitButton
                )
        );

        // Verify this is the calculator's form
        assertEquals(
                "/cgi-bin/benefit6.cgi",
                submit.findElement(By.xpath("./ancestor::form"))
                        .getAttribute("action")
                        .replaceFirst("^https?://[^/]+", "")
        );

        submit.click();

        // Wait for navigation and retrieve fresh page text
        String resultText = wait.until(webDriver -> {
            try {
                String currentUrl = webDriver.getCurrentUrl();

                String text = webDriver.findElement(
                        By.tagName("body")
                ).getText();

                if (currentUrl.contains("usa.gov")) {
                    return text;
                }

                // Do not read the original input form
                boolean stillOnForm =
                        !webDriver.findElements(
                                submitButton
                        ).isEmpty();

                if (stillOnForm) {
                    return null;
                }

                // Wait for calculator results
                if (!text.isBlank()) {
                    return text;
                }

                return null;

            } catch (StaleElementReferenceException
                     | NoSuchElementException e) {
                return null;
            }
        });

        String resultUrl = driver.getCurrentUrl();
        System.out.println("Result URL: " + resultUrl);

        // Detect calculator or server errors
        assertFalse(
                resultUrl.contains("usa.gov")
                        || resultText.contains(
                        "Your search didn't work"),
                "SSA redirected to an error page "
                        + "after Submit request. URL: "
                        + resultUrl
        );

        // Locate monthly benefit
        Pattern pattern = Pattern.compile(
                "(?is)(?:monthly\\s+(?:retirement\\s+)?benefit"
                        + "|estimated\\s+monthly\\s+benefit"
                        + "|monthly\\s+amount)"
                        + "[^$\\n]{0,150}\\$\\s*([\\d,]+(?:\\.\\d{2})?)"
        );

        Matcher matcher = pattern.matcher(resultText);

        assertTrue(
                matcher.find(),
                "Monthly benefit not found.\n"
                        + "Results:\n" + resultText
        );

        double actualBenefit = Double.parseDouble(
                matcher.group(1).replace(",", "")
        );

        // ASSERT
        System.out.printf(
                "Expected: $%.2f%n", expectedBenefit
        );

        System.out.printf(
                "Actual: $%.2f%n", actualBenefit
        );

        assertEquals(
                expectedBenefit,
                actualBenefit,
                0.01,
                "Incorrect monthly benefit for "
                        + birthYear
        );
    }

    private void fillField(String id, String value) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id(id)
                )
        );

        element.clear();
        element.sendKeys(value);
    }

    @Test
    public void testMaximumSocialSecurityBenefit() {

        /*
         * TEST: At what income level is the maximum benefit reached?
         *
         * This test explores increasing annual incomes
         * while keeping the birthdate and retirement age
         * constant.
         *
         * Hypothesis:
         * Benefits should eventually plateau because
         * Social Security only counts earnings up to
         * the annual taxable maximum.
         *
         * My idea is that there will be a point at which
         * there are no more increases to benefits,
         * and it will instead plateau to a maximum amount.
         *
         * The observed maximum benefit occurs at $250,000
         * where the monthly benefit is $4,484.00.
         * Any additional income past this doesn't chance the result.
         */

        // ARRANGE
        int birthYear = 1990;
        int retirementYear = birthYear + 67;

        int[] incomes = {
                55000,
                75000,
                100000,
                125000,
                150000,
                175000,
                184500,
                200000,
                250000,
                300000
        };

        double highestBenefit = -1;
        int incomeAtMaximum = -1;

        // ACT
        for (int income : incomes) {

            driver.get(
                    "https://www.ssa.gov/OACT/quickcalc/"
            );

            fillField("month", "01");
            fillField("day", "01");
            fillField("year",
                    String.valueOf(birthYear));

            fillField("earnings",
                    String.valueOf(income));

            fillField("retiremonth", "01");
            fillField("retireyear",
                    String.valueOf(retirementYear));

            driver.findElement(By.id("constant")).click();

            // Click the exact Submit request button
            By submitButton = By.xpath(
                    "//input[@type='submit' "
                            + "and @value='Submit request']"
            );

            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            submitButton
                    )
            ).click();

            // Wait for the result page.
            // Ignore stale elements during navigation.
            String result = wait.until(webDriver -> {
                try {
                    String url = webDriver.getCurrentUrl();

                    if (url.contains("usa.gov")) {
                        throw new AssertionError(
                                "SSA redirected to an error page: "
                                        + url
                        );
                    }

                    if (!url.contains("benefit6.cgi")) {
                        return null;
                    }

                    String text = webDriver.findElement(
                            By.tagName("body")
                    ).getText();

                    return text.isBlank() ? null : text;

                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });

            // Extract the monthly benefit estimate
            Pattern pattern = Pattern.compile(
                    "(?is)(?:monthly\\s+(?:retirement\\s+)?benefit"
                            + "|estimated\\s+monthly\\s+benefit"
                            + "|monthly\\s+amount)"
                            + "[^$\\n]{0,150}\\$\\s*([\\d,]+(?:\\.\\d{2})?)"
            );

            Matcher matcher = pattern.matcher(result);

            assertTrue(
                    matcher.find(),
                    "Benefit not found for income $"
                            + income + "\n" + result
            );

            double benefit = Double.parseDouble(
                    matcher.group(1).replace(",", "")
            );

            System.out.printf(
                    "Income: $%,d | Monthly Benefit: $%,.2f%n",
                    income, benefit
            );

            if (benefit > highestBenefit) {
                highestBenefit = benefit;
                incomeAtMaximum = income;
            }
        }

        // ASSERT
        assertTrue(
                highestBenefit > 0,
                "No valid benefit estimates were found."
        );

        System.out.println("\n-------------------------");
        System.out.println("MAXIMUM BENEFIT EXPLORATION");

        System.out.printf(
                "Highest observed benefit: $%,.2f%n",
                highestBenefit
        );

        System.out.printf(
                "First tested income reaching this benefit: $%,d%n",
                incomeAtMaximum
        );
    }

    @Test
    public void testCareerStartAgeBenefitDifference() {

        /*
         * TEST: Does starting a career at age 20 instead of
         * age 30 make a significant difference in benefits?
         *
         * Findings:
         *
         * Scenario 1: Both work until age 67.
         * Starting at 20 = 47 working years.
         * Starting at 30 = 37 working years.
         *
         * Both have at least 35 earning years.
         * If their highest 35 indexed annual earnings
         * are identical, their benefits will be identical.
         *
         * Scenario 2: Both stop working at age 60,
         * but claim Social Security at age 67.
         *
         * Starting at 20 = 40 working years.
         * Starting at 30 = 30 working years.
         *
         * The second person has only 30 earning years,
         * so five zero-earning years enter the 35-year
         * average. This reduces their estimated benefit.
         *
         * Conclusion:
         * Starting 10 years earlier can make a significant
         * difference, especially if someone stops working
         * before accumulating 35 years of earnings.
         *
         */

        driver.get(
                "https://www.ssa.gov/benefits/retirement/"
                        + "planner/stopwork.html"
        );

        String pageText = wait.until(webDriver -> {
            try {
                String text = webDriver.findElement(
                        By.tagName("body")
                ).getText();

                return text.contains("35 years") ? text : null;

            } catch (StaleElementReferenceException e) {
                return null;
            }
        });

        assertTrue(
                pageText.contains("35 years"),
                "Could not verify SSA's 35-year rule."
        );

        int annualIncome = 65000;
        int claimingAge = 67;

        int[] careerStartAges = {20, 30};
        int[] stoppingAges = {67, 60};

        System.out.println("\n-------------------------");
        System.out.println("CAREER START AGE COMPARISON");
        System.out.println("Annual Income: $" + annualIncome);
        System.out.println("Claiming Age: " + claimingAge);

        // ACT
        for (int stoppingAge : stoppingAges) {

            double[] benefits = new double[2];

            System.out.println(
                    "\nStopping Work at Age: " + stoppingAge
            );

            for (int i = 0; i < careerStartAges.length; i++) {

                int startAge = careerStartAges[i];

                int workingYears = stoppingAge - startAge;

                // Social Security averages 35 years.
                // Missing years count as zero.
                int countedYears = Math.min(workingYears, 35);

                double averageMonthlyEarnings =
                        (countedYears * annualIncome) / 420.0;

                // Simplified 2026 PIA formula
                double benefit;

                if (averageMonthlyEarnings <= 1286) {
                    benefit = averageMonthlyEarnings * 0.90;

                } else if (averageMonthlyEarnings <= 7749) {
                    benefit = (1286 * 0.90)
                            + ((averageMonthlyEarnings - 1286)
                            * 0.32);

                } else {
                    benefit = (1286 * 0.90)
                            + ((7749 - 1286) * 0.32)
                            + ((averageMonthlyEarnings - 7749)
                            * 0.15);
                }

                benefits[i] = benefit;

                System.out.printf(
                        "Start Age: %d | Working Years: %d"
                                + " | Estimated Monthly Benefit: $%,.2f%n",
                        startAge, workingYears, benefit
                );
            }

            double difference = benefits[0] - benefits[1];

            double percentageDifference =
                    (difference / benefits[1]) * 100;

            System.out.printf(
                    "Monthly Difference: $%,.2f%n",
                    difference
            );

            System.out.printf(
                    "Percentage Difference: %.2f%%%n",
                    percentageDifference
            );

            // ASSERT
            if (stoppingAge == 67) {
                assertEquals(
                        benefits[0],
                        benefits[1],
                        0.01,
                        "Both have at least 35 equal earning years."
                );
            } else {
                assertTrue(
                        benefits[0] > benefits[1],
                        "A 40-year career should produce a higher"
                                + " benefit than a 30-year career"
                                + " with identical annual earnings."
                );
            }
        }
    }

}
