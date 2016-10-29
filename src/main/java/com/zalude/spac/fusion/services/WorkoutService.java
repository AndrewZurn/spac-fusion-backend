package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.Exercise;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.repositories.ExerciseRepository;
import com.zalude.spac.fusion.repositories.WorkoutRepository;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that interacts with the Repository layer and contains the main extend of the Business Logic around
 * {@link Workout} and {@link Exercise}.
 *
 * @author Andrew Zurn (azurn)
 */
@Service
public class WorkoutService {

  @NonNull
  private WorkoutRepository workoutRepository;

  @NotNull
  private ExerciseRepository exerciseRepository;

  @Inject
  public WorkoutService(WorkoutRepository workoutRepository, ExerciseRepository exerciseOptionRepository) {
    this.workoutRepository = workoutRepository;
    this.exerciseRepository = exerciseOptionRepository;
  }

  public Iterable<Workout> findAllWorkouts() {
    return workoutRepository.findAll();
  }

  public Iterable<Workout> findAllWorkouts(List<UUID> workoutIds) {
    return workoutRepository.findAll(workoutIds);
  }

  public Optional<Workout> findExercise(UUID workoutId) {
    return Optional.ofNullable(workoutRepository.findOne(workoutId));
  }

  public Workout create(Workout workout) throws ResourceNotFoundException, ResourceValidationException {
    validatedWorkout(workout);
    validateExercises(workout.getExercises());

    return workoutRepository.save(workout);
  }


  public Workout update(Workout workout) throws ResourceNotFoundException, ResourceValidationException {
    validatedWorkout(workout);
    validateExercises(workout.getExercises());

    val existingWorkout = this.workoutRepository.findOne(workout.getId());
    existingWorkout.getExercises().clear();
    existingWorkout.getExercises().addAll(workout.getExercises());
    existingWorkout.setName(workout.getName());
    existingWorkout.setInstructions(workout.getInstructions());

    return this.workoutRepository.save(existingWorkout);
  }

  public Optional<Exercise> findExerciseOption(UUID exerciseOptionId) {
    return Optional.ofNullable(exerciseRepository.findOne(exerciseOptionId));
  }

  private void validateExercises(List<Exercise> exercises) throws ResourceNotFoundException, ResourceValidationException {
    // validate our exercise options are valid
    val errors = validateExerciseFields(exercises);

    if (errors.size() > 0) {
      throw new ResourceValidationException(null, errors,
          String.format("Validation errors encountered during create operation for ExerciseOptions with names '%s'",
              exercises.stream()
                  .map(Exercise::getName)
                  .collect(Collectors.joining(", "))));
    }
  }

  private Map<String, List<String>> validateExerciseFields(List<Exercise> exercises) {
    Map<String, List<String>> errors = new HashMap<>();

    exercises.forEach(option -> {
      if (exerciseRepository.findByNameAndWorkoutIdNot(option.getName(), option.getWorkout().getId()).size() > 0) {
        final String error = String.format("Exercise Option with name '%s' already exists in Exercise: '%s'.",
            option.getName(), option.getWorkout().getName());
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

  private void validatedWorkout(Workout workout) throws ResourceValidationException {
    val name = workout.getName();
    if (workoutRepository.findByNameAndIdNot(name, workout.getId()).size() > 0) {
      val error = Collections.singletonMap(name, Collections.singletonList(String.format("Exercise with name: '%s' already exists.", name)));
      throw new ResourceValidationException(null, error, null);
    }
  }
}
