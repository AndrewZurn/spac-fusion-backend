package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.repositories.UserExerciseOptionLookupRepository;
import com.zalude.spac.fusion.repositories.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Service
public class UserService {

    private @NonNull UserExerciseOptionLookupRepository userExerciseOptionLookupRepository;
    private @NonNull UserRepository userRepository;

    @Inject
    public UserService(UserExerciseOptionLookupRepository userExerciseOptionLookupRepository, UserRepository userRepository) {
        this.userExerciseOptionLookupRepository = userExerciseOptionLookupRepository;
        this.userRepository = userRepository;
    }
}
