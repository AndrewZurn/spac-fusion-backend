package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.services.UserService;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FusionUserControllerUnitTest {

    @Mock
    private UserService userService;

    private UserController userController = new UserController(userService);

}
