package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.services.ExerciseService;
import com.zalude.spac.fusion.services.WorkoutService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkoutControllerUnitTest {

  @Mock
  private WorkoutService workoutService;

  @Mock
  private ExerciseService exerciseService;

  private WorkoutController workoutController = new WorkoutController(workoutService, exerciseService);

  @Test
  public void doSomething() throws Exception {

  }

}
