package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import java.time.Duration;

public class SecondLabTest {

    private WebDriver edgeDriver;

    private static final String baseUrl = "https://www.nmu.org.ua/ua/";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("--start-fullscreen");
        edgeOptions.setImplicitWaitTimeout(Duration.ofSeconds(15));
        this.edgeDriver = new EdgeDriver(edgeOptions);
    }

    @BeforeMethod
    public void preconditions() {
        edgeDriver.get(baseUrl);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        edgeDriver.quit();
    }

    @Test
    public void testHeaderExists() {
        WebElement header = edgeDriver.findElement(By.id("header"));
        Assert.assertNotNull(header);
    }
    @Test
    public void testClickOnForStudent() {
        WebElement forStudentButton = edgeDriver.findElement(By.xpath("/html/body/center/div[4]/div/div[1]/ul/li[4]/a"));
        Assert.assertNotNull((forStudentButton));
        forStudentButton.click();
        Assert.assertNotEquals(edgeDriver.getCurrentUrl(), baseUrl);
    }
    @Test
    public void testSearchFieldOnForStudentPage() {

        String studentPageUrl = "content/student_life/students/";
        edgeDriver.get(baseUrl + studentPageUrl);
        WebElement searchField = edgeDriver.findElement(By.tagName("input"));
        Assert.assertNotNull(searchField);

        System.out.println(String.format("Name attribute: %s", searchField.getAttribute("name")) +
                String.format("\nID attribute: %s", searchField.getAttribute("id")) +
                String.format("\nType attribute: %s", searchField.getAttribute("type")) +
                String.format("\nValue attribute: %s", searchField.getAttribute("value")) +
                String.format("\nPosition: (%d,%d)", searchField.getLocation().x, searchField.getLocation().y) +
                String.format("\nSize: %dx%d", searchField.getSize().height, searchField.getSize().width));

        String inputValue = "I need info";
        searchField.sendKeys(inputValue);

        Assert.assertEquals(searchField.getText(), inputValue);
        searchField.sendKeys(Keys.ENTER);
        Assert.assertNotEquals(edgeDriver.getCurrentUrl(), studentPageUrl);
    }
    @Test
    public void testSlider() {

        WebElement nextButton = edgeDriver.findElement(By.className("next"));
        WebElement nextButtonByCss = edgeDriver.findElement(By.cssSelector("a.next"));

        Assert.assertEquals(nextButton, nextButtonByCss);

        WebElement previousButton = edgeDriver.findElement(By.className("prev"));

        for (int i = 0; i < 20; i++) {
            if (nextButton.getAttribute("class").contains("disabled")) {
                previousButton.click();
                Assert.assertTrue(previousButton.getAttribute("class").contains("disabled"));
                Assert.assertFalse(nextButton.getAttribute("class").contains("disabled"));
            } else {
                nextButton.click();
                Assert.assertTrue(nextButton.getAttribute("class").contains("disabled"));
                Assert.assertFalse(previousButton.getAttribute("class").contains("disabled"));
            }
        }
    }
}