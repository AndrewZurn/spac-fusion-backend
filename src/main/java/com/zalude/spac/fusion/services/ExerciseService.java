package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.Exercise;
import com.zalude.spac.fusion.models.domain.ExerciseOption;
import com.zalude.spac.fusion.repositories.ExerciseOptionRepository;
import com.zalude.spac.fusion.repositories.ExerciseRepository;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that interacts with the Repository layer and contains the main extend of the Business Logic around
 * {@link Exercise} and {@link ExerciseOption}.
 *
 * @author Andrew Zurn (azurn)
 */
@Service
public class ExerciseService {

  @NonNull
  private ExerciseRepository exerciseRepository;

  @NotNull
  private ExerciseOptionRepository exerciseOptionRepository;

  @Inject
  public ExerciseService(ExerciseRepository exerciseRepository, ExerciseOptionRepository exerciseOptionRepository) {
    this.exerciseRepository = exerciseRepository;
    this.exerciseOptionRepository = exerciseOptionRepository;
  }

  public Iterable<Exercise> findAllExercises() {
    return exerciseRepository.findAll();
  }

  public Iterable<Exercise> findAllExercises(List<UUID> exerciseIds) {
    return exerciseRepository.findAll(exerciseIds);
  }

  public Optional<Exercise> findExercise(UUID exerciseId) {
    return Optional.ofNullable(exerciseRepository.findOne(exerciseId));
  }

  public Exercise create(Exercise exercise) throws ResourceNotFoundException, ResourceValidationException {
    validateExercise(exercise);
    validateExerciseOptions(exercise.getExerciseOptions());
    return this.exerciseRepository.save(exercise);
  }

  public Exercise update(Exercise exercise) throws ResourceNotFoundException, ResourceValidationException {
    validateExercise(exercise);
    validateExerciseOptions(exercise.getExerciseOptions());

    val existingExercise = this.exerciseRepository.findOne(exercise.getId());
    existingExercise.getExerciseOptions().clear();
    existingExercise.getExerciseOptions().addAll(exercise.getExerciseOptions());
    existingExercise.setName(exercise.getName());
    existingExercise.setDescription(exercise.getDescription());

    return this.exerciseRepository.save(existingExercise);
  }

  public Optional<ExerciseOption> findExerciseOption(UUID exerciseOptionId) {
    return Optional.ofNullable(exerciseOptionRepository.findOne(exerciseOptionId));
  }

  private void validateExerciseOptions(List<ExerciseOption> exerciseOptions) throws ResourceNotFoundException, ResourceValidationException {
    // validate our exercise options are valid
    val errors = validateExerciseOptionFields(exerciseOptions);

    if (errors.size() > 0) {
      throw new ResourceValidationException(null, errors,
          String.format("Validation errors encountered during create operation for ExerciseOptions with names '%s'",
              exerciseOptions.stream()
                  .map(ExerciseOption::getName)
                  .collect(Collectors.joining(", "))));
    }
  }

  private Map<String, List<String>> validateExerciseOptionFields(List<ExerciseOption> exerciseOptions) {
    Map<String, List<String>> errors = new HashMap<>();

    exerciseOptions.forEach(option -> {
      if (exerciseOptionRepository.findByNameAndExerciseIdNot(option.getName(), option.getExercise().getId()).size() > 0) {
        final String error = String.format("Exercise Option with name '%s' already exists in Exercise: '%s'.",
            option.getName(), option.getExercise().getName());
        List<String> errorList = errors.get(option.getName());
        if (errorList != null) {
          errorList.add(error);
          errors.put(option.getName(), errorList);
        } else {
          errors.put(option.getName(), Collections.singletonList(error));
        }

      }
    });

    return errors;
  }

  private void validateExerciseExists(UUID exerciseId) throws ResourceNotFoundException {
    if (!exerciseRepository.exists(exerciseId)) {
      throw new ResourceNotFoundException(exerciseId, String.format("Exercise for Id: %s did not exist.", exerciseId));
    }
  }

  private void validateExercise(Exercise exercise) throws ResourceValidationException {
    val name = exercise.getName();
    if (exerciseRepository.findByNameAndIdNot(name, exercise.getId()).size() > 0) {
      val error = Collections.singletonMap(name, Collections.singletonList(String.format("Exercise with name: '%s' already exists.", name)));
      throw new ResourceValidationException(null, error, null);
    }
  }
}
