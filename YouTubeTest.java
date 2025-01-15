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

public class YouTubeTest {

    private WebDriver edgeDriver;
    private static final String baseUrl = "https://www.youtube.com";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("--start-fullscreen");
        edgeOptions.setImplicitWaitTimeout(Duration.ofSeconds(30));
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
    public void testYouTubeSearch() {
        // Знаходимо поле пошуку за допомогою непрямого XPath
        WebElement searchField = edgeDriver.findElement(By.xpath("//input[@name='search_query']"));
        Assert.assertNotNull(searchField, "Поле пошуку не знайдено");
        // Клікаємо на поле пошуку
        searchField.click();
        // Вводимо дані у поле пошуку
        String searchQuery = "SadSvit";
        searchField.sendKeys(searchQuery);
        Assert.assertEquals(searchField.getAttribute("value"), searchQuery, "Дані в полі пошуку не відповідають очікуваним");
        // Імітуємо натискання клавіші Enter для виконання пошуку
        searchField.sendKeys(Keys.ENTER);
        // Перевіряємо, що результати пошуку містять очікуваний текст
        WebElement firstVideoTitle = edgeDriver.findElement(By.xpath("//a[@id='video-title']"));
        Assert.assertNotNull(firstVideoTitle, "Результати пошуку не знайдено");
        Assert.assertTrue(firstVideoTitle.getText().toLowerCase().contains("sadsvit"), "Результати пошуку не містять очікуваний текст");
    }
}
