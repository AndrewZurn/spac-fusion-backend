package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.services.WorkoutService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@RestController
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class WorkoutController {

    private final @NonNull WorkoutService workoutService;

}
