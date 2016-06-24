package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.repositories.ExerciseRepository;
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
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class ExerciseService {

    private final @NonNull ExerciseRepository exerciseRepository;

}
