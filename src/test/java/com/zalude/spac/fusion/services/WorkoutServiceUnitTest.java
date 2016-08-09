package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.repositories.WorkoutRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkoutServiceUnitTest {

  @Mock
  private WorkoutRepository workoutRepository;

  private WorkoutService workoutService = new WorkoutService(workoutRepository);

  @Test
  public void doSomething() throws Exception {

  }
}
