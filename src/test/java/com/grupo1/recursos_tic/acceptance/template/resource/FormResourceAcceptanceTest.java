package com.grupo1.recursos_tic.acceptance.template.resource;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FormResourceAcceptanceTest {

    private WebDriver driver;
    private FormResourceAcceptancePage page;
}
