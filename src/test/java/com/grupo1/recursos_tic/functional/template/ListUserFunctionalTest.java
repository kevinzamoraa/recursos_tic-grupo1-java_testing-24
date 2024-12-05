package com.grupo1.recursos_tic.functional.template;

import com.grupo1.recursos_tic.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ListUserFunctionalTest {

    private final String url = "http://localhost:8082/users";

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ListUserFunctionalPage page;
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        //driver = new ChromeDriver(); // Activar para Chrome, desactivar Firefox
        driver = new FirefoxDriver(); // Activar para Firefox, desactivar Chrome
        driver.get(url);
        driver.manage().window().maximize();
        page = new ListUserFunctionalPage(driver, userService, passwordEncoder);
    }
    @AfterEach
    void tearDown() {
        //driver.quit(); // TODO: Quitar el comentario cuando se complete el test
    }

    @Test
    @DisplayName("Comprobar etiqueta <title>")
    void title() {
        assertEquals("Usuarios", page.getTitle());
    }

    @Test
    @DisplayName("Comprobar la etiqueta <h1>")
    void h1() {
        assertEquals("Usuarios", page.h1.getText());
    }

    @Test
    @DisplayName("Comprobar que NO existe el botón «Crear nuevo usuario» " +
            "hasta que se haya registrado un usuario con el role ADMIN")
    void buttonCreateUser() {
        page.createUserInDatabase("admin", "Admin1234", "admin@admin.es","admin");
        try {
            assertFalse(page.btnCreateUser.isDisplayed());
        } catch (NoSuchElementException e) {
            assertTrue(true); // El elemento no existe
        }
        page.loginAsUser("admin", "Admin1234");
        driver.get(url);
        assertTrue(page.btnCreateUser.isDisplayed());
    }

}
