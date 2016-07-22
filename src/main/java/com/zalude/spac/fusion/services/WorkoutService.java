package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.repositories.WorkoutRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Service
public class WorkoutService {

  private
  @NonNull
  WorkoutRepository workoutRepository;

  private
  @NotNull
  ExerciseService exerciseService;

  @Inject
  public WorkoutService(WorkoutRepository workoutRepository, ExerciseService exerciseService) {
    this.workoutRepository = workoutRepository;
    this.exerciseService = exerciseService;
  }

  public Optional<Workout> findWorkout(UUID id) {
    return Optional.ofNullable(this.workoutRepository.findOne(id));
  }

  public Iterable<Workout> findAllWorkouts() {
    return this.workoutRepository.findAll();
  }

  public Workout create(Workout workout) {
    return this.workoutRepository.save(workout);
  }

  public Workout update(Workout workout, UUID id) throws ResourceValidationException {
    val maybeWorkout = this.findWorkout(id);
    if (maybeWorkout.isPresent()) {
      val foundWorkout = maybeWorkout.get();
      foundWorkout.setDuration(workout.getDuration());
      foundWorkout.setWorkoutDate(workout.getWorkoutDate());
      foundWorkout.setExercise(workout.getExercise());
      return this.workoutRepository.save(foundWorkout);
    } else {
      val errors = Collections.singletonMap(id, Collections.singletonList("Could not find Workout."));
      throw new ResourceValidationException(errors, null, null);
    }
  }
}
