package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.repositories.WorkoutRepository;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @NotNull
  private WorkoutRepository workoutRepository;

  @Inject
  public WorkoutService(WorkoutRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
  }

  public Iterable<Workout> findAllWorkouts() {
    return this.workoutRepository.findAll();
  }

  public Optional<Workout> findWorkout(UUID id) {
    return Optional.ofNullable(this.workoutRepository.findOne(id));
  }

  public Optional<Workout> findTodaysWorkout() {
    return Optional.ofNullable(this.workoutRepository.findTodaysWorkout());
  }

  public Optional<Workout> findWorkoutForDate(LocalDate date) {
    return Optional.ofNullable(this.workoutRepository.findOneByWorkoutDate(date));
  }

  public Iterable<Workout> findRemainingWorkoutsForWeek() {
    LocalDate todaysDay = LocalDate.now();
    LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
    return workoutRepository.findAllWorkouts(todaysDay, endOfWeek);
  }

  public Workout create(Workout workout) throws ResourceValidationException {
    if (findWorkoutForDate(workout.getWorkoutDate()).isPresent()) {
      throw new ResourceValidationException(null,
          Collections.singletonMap("workoutDate", Collections.singletonList("Workout already exists for date: " + workout.getWorkoutDate().toString())),
          null);
    }

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
