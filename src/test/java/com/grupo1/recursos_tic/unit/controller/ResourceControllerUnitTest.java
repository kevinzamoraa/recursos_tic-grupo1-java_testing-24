package com.grupo1.recursos_tic.unit.controller;

import com.grupo1.recursos_tic.integration.controller.ResourceControllerIntegrationTest;
import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.ResourceListsService;
import com.grupo1.recursos_tic.service.ResourceService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerUnitTest {

    @Mock
    private ResourceService resourceService;
    @Mock
    private ResourceListsService resourceListsService;
    @Mock
    private RatingService ratingService;
    @InjectMocks
    private ResourceControllerIntegrationTest controller;

}

