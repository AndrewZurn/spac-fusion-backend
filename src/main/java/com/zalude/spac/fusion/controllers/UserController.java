package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.services.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class UserController {

    private final @NonNull UserService userService;

}
