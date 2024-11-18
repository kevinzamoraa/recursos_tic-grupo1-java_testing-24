package com.grupo1.recursos_tic.selenium.resource;

import com.grupo1.recursos_tic.service.ResourceListsService;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class list {

    @Autowired
    private ResourceListsService resourceListsService;

    WebDriver driver;

    @BeforeEach
    void setUp() {
        //driver = new ChromeDriver(); // Activar para Chrome, desactivar FirefoxDriver()
        driver = new FirefoxDriver();
        driver.get("http://localhost:8082/resources");
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    @DisplayName("Comprobar etiqueta <title>")
    void title() {
        String title = driver.getTitle();
        System.out.println(title);
        assertEquals("Listado de recursos", title);
    }

    @Test
    @DisplayName("Comprobar la etiqueta <h1>")
    void h1() {
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("Listado de recursos", h1.getText());
    }

}
