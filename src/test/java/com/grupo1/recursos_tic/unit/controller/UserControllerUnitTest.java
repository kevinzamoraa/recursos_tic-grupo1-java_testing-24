package com.grupo1.recursos_tic.unit.controller;

import com.grupo1.recursos_tic.integration.controller.UserControllerIntegrationTest;
import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

public class UserControllerUnitTest {

    @Mock
    private UserService resourceService;
    @Mock
    private RatingService ratingService;
    @InjectMocks
    private UserControllerIntegrationTest controller;

}
