package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.repositories.ExerciseRepository;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExerciseServiceUnitTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    private ExerciseService exerciseService = new ExerciseService(exerciseRepository);


}
