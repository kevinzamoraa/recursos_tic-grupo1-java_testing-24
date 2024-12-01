package com.grupo1.recursos_tic.acceptance.template.functional;

import com.grupo1.recursos_tic.service.UserService;
import org.junit.jupiter.api.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ListResourceFunctionalTest {

    private final String url = "http://localhost:8082/resources";

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ListResourceFunctionalPage page;
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        //resourceService.deleteAll();
        //driver = new ChromeDriver(); // Activar para Chrome, desactivar Firefox
        driver = new FirefoxDriver(); // Activar para Firefox, desactivar Chrome
        driver.get(url);
        driver.manage().window().maximize();
        page = new ListResourceFunctionalPage(driver, userService, passwordEncoder);
    }
    @AfterEach
    void tearDown() {
        //driver.quit(); // TODO: Quitar el comentario cuando se complete el test
    }

    @Test
    @DisplayName("Comprobar etiqueta <title>")
    void title() {
        assertEquals("Recursos", page.getTitle());
    }

    @Test
    @DisplayName("Comprobar la etiqueta <h1>")
    void h1() {
        assertEquals("Recursos", page.h1.getText());
    }

    @Test
    @DisplayName("Comprobar que NO existe el botón «Crear nuevo recurso» " +
            "hasta que se haya registrado un usuario")
    void buttonCreateResource() {
        page.createUserInDatabase("admin", "Admin1234", "admin@admin.es","admin");
        try {
            assertFalse(page.btnCreateResource.isDisplayed());
        } catch (NoSuchElementException e) {
            assertTrue(true); // El elemento no existe
        }
        page.loginAsUser("admin", "Admin1234");
        driver.get(url);
        assertTrue(page.btnCreateResource.isDisplayed());
    }



}
