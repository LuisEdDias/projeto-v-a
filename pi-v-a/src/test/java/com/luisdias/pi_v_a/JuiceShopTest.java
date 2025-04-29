package com.luisdias.pi_v_a;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JuiceShopTest {
    @Test
    public void chromeWebDriverTest() {
        System.setProperty("webdriver.chrome.driver", "C://Users//edu_v//Desktop//file//chromedriver-win64//chromedriver.exe");

        String juiceShopUrl = "http://localhost:3000";
        String outputDirPath = "./evidencias";

        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));

            driver.get(juiceShopUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#mat-mdc-dialog-0 .close-dialog"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.id("navbarAccount"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.id("navbarLoginButton"))).click();

            Thread.sleep(2000);

            driver.findElement(By.id("email")).sendKeys("' OR 1=1--@test.com");
            driver.findElement(By.id("password")).sendKeys("123456");

            takeScreenshot(driver, outputDirPath + "/1-pre-login.png");
            System.out.println("🖼️ Screenshot antes do login salvo.");

            driver.findElement(By.id("loginButton")).click();
            Thread.sleep(5000);

            takeScreenshot(driver, outputDirPath + "/2-post-login.png");
            System.out.println("🖼️ Screenshot depois do login salvo.");

            String currentUrl = driver.getCurrentUrl();
            System.out.println("🌐 URL atual: " + currentUrl);

            WebElement notification = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".notificationMessage"))
            );

            String notificationText = notification.getText();

            assertTrue(notificationText.contains("You successfully solved a challenge: Login Admin"),
                    "A notificação esperada não foi exibida!");
        } catch (Exception e) {
            System.out.println("Erro durante execução: " + e.getMessage());
        } finally {
            driver.quit();
        }

    }

    private static void takeScreenshot(WebDriver driver, String path) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File srcFile = ts.getScreenshotAs(OutputType.FILE);
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(java.nio.file.Files.readAllBytes(srcFile.toPath()));
        } catch (IOException e) {
            System.out.println("Erro ao salvar screenshot: " + e.getMessage());
        }
    }
}