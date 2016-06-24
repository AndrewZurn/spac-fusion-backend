package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.services.ExerciseService;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExerciseControllerUnitTest {

    @Mock
    private ExerciseService exerciseService;

    private ExerciseController exerciseController = new ExerciseController(exerciseService);

}
