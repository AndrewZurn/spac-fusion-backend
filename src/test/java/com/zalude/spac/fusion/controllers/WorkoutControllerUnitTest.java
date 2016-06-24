package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.services.WorkoutService;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkoutControllerUnitTest {

    @Mock
    private WorkoutService workoutService;

    private WorkoutController workoutController = new WorkoutController(workoutService);

}
