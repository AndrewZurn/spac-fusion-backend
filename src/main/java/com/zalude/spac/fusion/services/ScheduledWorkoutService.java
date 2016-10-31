package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
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
  private ScheduledWorkoutRepository scheduledWorkoutRepository;

  @Inject
  public ScheduledWorkoutService(ScheduledWorkoutRepository scheduledWorkoutRepository) {
    this.scheduledWorkoutRepository = scheduledWorkoutRepository;
  }

  public Iterable<ScheduledWorkout> findAllWorkouts() {
    return this.scheduledWorkoutRepository.findAll();
  }

  public Optional<ScheduledWorkout> findWorkout(UUID id) {
    return Optional.ofNullable(this.scheduledWorkoutRepository.findOne(id));
  }

  public Optional<ScheduledWorkout> findTodaysWorkout() {
    return Optional.ofNullable(this.scheduledWorkoutRepository.findTodaysWorkout());
  }

  public Iterable<ScheduledWorkout> findWorkoutsForDate(LocalDate date) {
    return this.scheduledWorkoutRepository.findAllByWorkoutDate(date);
  }

  public Optional<ScheduledWorkout> findWorkoutForDateAndActiveStatus(boolean activeStatus, LocalDate date) {
    return Optional.ofNullable(this.scheduledWorkoutRepository.findOneByActiveAndWorkoutDate(activeStatus, date));
  }

  public Iterable<ScheduledWorkout> findRemainingWorkoutsForWeek() {
    LocalDate todaysDay = LocalDate.now();
    LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
    return scheduledWorkoutRepository.findAllWorkouts(todaysDay, endOfWeek);
  }

  public ScheduledWorkout create(ScheduledWorkout scheduledWorkout) throws ResourceValidationException {
    val foundWorkout = Optional.ofNullable(
        scheduledWorkoutRepository.findOneByActiveAndWorkoutDate(true, scheduledWorkout.getWorkoutDate()));
    if (foundWorkout.isPresent()) {
      throw new ResourceValidationException(null,
          Collections.singletonMap("workoutDate",
              Collections.singletonList("Workout already exists for date: " + scheduledWorkout.getWorkoutDate().toString() + ". " +
                  "Please consider deactivating workout in order to create a new workout for that date.")),
          null);
    }

    return this.scheduledWorkoutRepository.save(scheduledWorkout);
  }

  public ScheduledWorkout update(ScheduledWorkout scheduledWorkout, UUID id) throws ResourceValidationException {
    val maybeWorkout = this.findWorkout(id);
    if (maybeWorkout.isPresent()) {
      val foundWorkout = maybeWorkout.get();
      foundWorkout.setWorkoutDate(scheduledWorkout.getWorkoutDate());
      foundWorkout.setWorkout(scheduledWorkout.getWorkout());
      return this.scheduledWorkoutRepository.save(foundWorkout);
    } else {
      val errors = Collections.singletonMap(id, Collections.singletonList("Could not find Workout."));
      throw new ResourceValidationException(errors, null, null);
    }
  }

  /**
   * Will find the workout, and flips is active status. If it is trying to activate a workout when there is
   * already a workout active for that that date, it will return an error.
   *
   * @param scheduledWorkoutId The id of the workout in which to flip the active status for.
   * @return True if the status was updated, false otherwise.
   * @throws ResourceValidationException If a active workout for that date already exists, and the workout
   *          for the id is trying to be activated.
   * @throws ResourceNotFoundException If the workout is not found.
   */
  public void updateStatus(UUID scheduledWorkoutId) throws ResourceValidationException, ResourceNotFoundException {
    val foundWorkoutOpt = Optional.ofNullable(scheduledWorkoutRepository.findOne(scheduledWorkoutId));
    if (foundWorkoutOpt.isPresent()) {
      val foundWorkout = foundWorkoutOpt.get();

      // if the workout is not active, and there is already a workout active for that date
      if (!foundWorkout.isActive()) {
        val alreadyActiveWorkout =
            Optional.ofNullable(scheduledWorkoutRepository.findOneByActiveAndWorkoutDate(true, foundWorkout.getWorkoutDate()));
        if (alreadyActiveWorkout.isPresent()) {
          throw new ResourceValidationException("Trying to activate a workout on a date in which a workout is already active.");
        }
      }

      // update the workouts it's status
      foundWorkout.setActive(!foundWorkout.isActive());
      scheduledWorkoutRepository.save(foundWorkout);
    } else {
      throw new ResourceNotFoundException(scheduledWorkoutId, "Could not find scheduled workout.");
    }
  }
}
