package com.grupo1.recursos_tic.acceptance.template.functional;

import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.model.UserRole;
import com.grupo1.recursos_tic.service.UserService;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ListResourceFunctionalPage {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final WebDriver driver;

    @Autowired
    public ListResourceFunctionalPage(
            WebDriver driver,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.driver = driver;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    @FindBy(tagName = "h1")
    public WebElement h1;

    //@FindBy(xpath = "//button[contains(text(), 'Crear un recurso nuevo')]")
    @FindBy(id = "btnCreateResource")
    public WebElement btnCreateResource;


    public void loginAsUser(String username, String password) {
        driver.get("http://localhost:8082/login");

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);

        driver.findElement(By.xpath("//button[contains(text(), 'Sign in')]")).click();
    }

    public void createUserInDatabase(String username, String password, String email, String role) {
        UserRole userRole = (role.trim().equalsIgnoreCase("admin"))
                ? UserRole.ADMIN
                : UserRole.AUTHOR;

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(userRole)
                .build();

        userService.save(user);
    }

}
