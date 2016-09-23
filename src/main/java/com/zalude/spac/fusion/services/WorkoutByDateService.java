package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.WorkoutWithDate;
import com.zalude.spac.fusion.repositories.WorkoutWithDateRepository;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Service
public class WorkoutByDateService {

  @NotNull
  private WorkoutWithDateRepository workoutRepository;

  @Inject
  public WorkoutByDateService(WorkoutWithDateRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
  }

  public Iterable<WorkoutWithDate> findAllWorkouts() {
    return this.workoutRepository.findAll();
  }

  public Optional<WorkoutWithDate> findWorkout(UUID id) {
    return Optional.ofNullable(this.workoutRepository.findOne(id));
  }

  public Optional<WorkoutWithDate> findTodaysWorkout() {
    return Optional.ofNullable(this.workoutRepository.findTodaysWorkout());
  }

  public Optional<WorkoutWithDate> findWorkoutForDate(LocalDate date) {
    return Optional.ofNullable(this.workoutRepository.findOneByWorkoutDate(date));
  }

  public Iterable<WorkoutWithDate> findRemainingWorkoutsForWeek() {
    LocalDate todaysDay = LocalDate.now();
    LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
    return workoutRepository.findAllWorkouts(todaysDay, endOfWeek);
  }

  public WorkoutWithDate create(WorkoutWithDate workoutWithDate) throws ResourceValidationException {
    if (findWorkoutForDate(workoutWithDate.getWorkoutDate()).isPresent()) {
      throw new ResourceValidationException(null,
          Collections.singletonMap("workoutDate", Collections.singletonList("Workout already exists for date: " + workoutWithDate.getWorkoutDate().toString())),
          null);
    }

    return this.workoutRepository.save(workoutWithDate);
  }

  public WorkoutWithDate update(WorkoutWithDate workoutWithDate, UUID id) throws ResourceValidationException {
    val maybeWorkout = this.findWorkout(id);
    if (maybeWorkout.isPresent()) {
      val foundWorkout = maybeWorkout.get();
      foundWorkout.setWorkoutDate(workoutWithDate.getWorkoutDate());
      foundWorkout.setWorkout(workoutWithDate.getWorkout());
      return this.workoutRepository.save(foundWorkout);
    } else {
      val errors = Collections.singletonMap(id, Collections.singletonList("Could not find Workout."));
      throw new ResourceValidationException(errors, null, null);
    }
  }
}
