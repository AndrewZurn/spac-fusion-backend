package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.models.domain.User;
import com.zalude.spac.fusion.repositories.UserExerciseOptionLookupRepository;
import com.zalude.spac.fusion.repositories.UserRepository;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceUnitTest {

    @Mock
    private UserExerciseOptionLookupRepository userExerciseOptionLookupRepository;

    @Mock
    private UserRepository userRepository;

    private UserService userService = new UserService(userExerciseOptionLookupRepository, userRepository);

}
