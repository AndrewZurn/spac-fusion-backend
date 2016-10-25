package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.ScheduledWorkout;
import com.zalude.spac.fusion.models.domain.ScheduledWorkout;
import com.zalude.spac.fusion.repositories.ScheduledWorkoutRepository;
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
public class ScheduledWorkoutService {

  @NotNull
  private ScheduledWorkoutRepository workoutRepository;

  @Inject
  public ScheduledWorkoutService(ScheduledWorkoutRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
  }

  public Iterable<ScheduledWorkout> findAllWorkouts() {
    return this.workoutRepository.findAll();
  }

  public Optional<ScheduledWorkout> findWorkout(UUID id) {
    return Optional.ofNullable(this.workoutRepository.findOne(id));
  }

  public Optional<ScheduledWorkout> findTodaysWorkout() {
    return Optional.ofNullable(this.workoutRepository.findTodaysWorkout());
  }

  public Optional<ScheduledWorkout> findWorkoutForDate(LocalDate date) {
    return Optional.ofNullable(this.workoutRepository.findOneByWorkoutDate(date));
  }

  public Iterable<ScheduledWorkout> findRemainingWorkoutsForWeek() {
    LocalDate todaysDay = LocalDate.now();
    LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
    return workoutRepository.findAllWorkouts(todaysDay, endOfWeek);
  }

  public ScheduledWorkout create(ScheduledWorkout scheduledWorkout) throws ResourceValidationException {
    if (findWorkoutForDate(scheduledWorkout.getWorkoutDate()).isPresent()) {
      throw new ResourceValidationException(null,
          Collections.singletonMap("workoutDate", Collections.singletonList("Workout already exists for date: " + scheduledWorkout.getWorkoutDate().toString())),
          null);
    }

    return this.workoutRepository.save(scheduledWorkout);
  }

  public ScheduledWorkout update(ScheduledWorkout scheduledWorkout, UUID id) throws ResourceValidationException {
    val maybeWorkout = this.findWorkout(id);
    if (maybeWorkout.isPresent()) {
      val foundWorkout = maybeWorkout.get();
      foundWorkout.setWorkoutDate(scheduledWorkout.getWorkoutDate());
      foundWorkout.setWorkout(scheduledWorkout.getWorkout());
      return this.workoutRepository.save(foundWorkout);
    } else {
      val errors = Collections.singletonMap(id, Collections.singletonList("Could not find Workout."));
      throw new ResourceValidationException(errors, null, null);
    }
  }
}
