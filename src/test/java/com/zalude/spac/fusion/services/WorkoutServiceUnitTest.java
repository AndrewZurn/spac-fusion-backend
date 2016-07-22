package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.repositories.WorkoutRepository;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkoutServiceUnitTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseService exerciseService;

    private WorkoutService workoutService = new WorkoutService(workoutRepository, exerciseService);
}
