package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.ScheduledWorkout;
import com.zalude.spac.fusion.models.request.CreateOrUpdateScheduledWorkoutRequest;
import com.zalude.spac.fusion.models.response.error.BadRequestResponse;
import com.zalude.spac.fusion.services.WorkoutService;
import com.zalude.spac.fusion.services.ScheduledWorkoutService;
import lombok.NonNull;
import lombok.val;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@RestController
@RequestMapping(path = "/scheduled-workouts")
public class ScheduledWorkoutController {

  @NonNull
  private ScheduledWorkoutService scheduledWorkoutService;

  @NonNull
  private WorkoutService workoutService;

  @Inject
  public ScheduledWorkoutController(ScheduledWorkoutService scheduledWorkoutService, WorkoutService workoutService) {
    this.scheduledWorkoutService = scheduledWorkoutService;
    this.workoutService = workoutService;
  }

  @RequestMapping(method = GET)
  public ResponseEntity<List<ScheduledWorkout>> getAllWorkouts() {
    List<ScheduledWorkout> scheduledWorkouts = (List<ScheduledWorkout>) this.scheduledWorkoutService.findAllWorkouts();
    val workoutsResponse = scheduledWorkouts.stream()
        .collect(Collectors.toList());
    return new ResponseEntity(workoutsResponse, HttpStatus.OK);
  }

  @RequestMapping(method = GET, value = "/{scheduledWorkoutId}")
  public ResponseEntity getWorkout(@PathVariable UUID scheduledWorkoutId) {
    return returnWorkoutIfFound(this.scheduledWorkoutService.findWorkout(scheduledWorkoutId));
  }

  @RequestMapping(method = GET, value = "/today")
  public ResponseEntity getTodaysWorkout() {
    return returnWorkoutIfFound(this.scheduledWorkoutService.findTodaysWorkout());
  }

  @RequestMapping(method = GET, value = "/week")
  public ResponseEntity getThisWeeksWorkouts() {
    return new ResponseEntity(scheduledWorkoutService.findRemainingWorkoutsForWeek(), HttpStatus.OK);
  }

  @RequestMapping(method = GET, value = "/by-date/{workoutDate}")
  public ResponseEntity getWorkoutByDate(@PathVariable @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate workoutDate,
                                         @RequestParam(name = "active", required = false) String active) {
    if (!StringUtils.isEmpty(active)) {
      val activeStatus = Boolean.valueOf(active);
      return returnWorkoutIfFound(scheduledWorkoutService.findWorkoutForDateAndActiveStatus(activeStatus, workoutDate));
    } else {
      return new ResponseEntity(scheduledWorkoutService.findWorkoutsForDate(workoutDate), HttpStatus.OK);
    }
  }

  @RequestMapping(method = POST)
  public ResponseEntity createWorkout(@RequestBody @Valid CreateOrUpdateScheduledWorkoutRequest workoutRequest) {
    return saveOrUpdateWorkout(workoutRequest, Optional.empty());
  }

  @RequestMapping(method = PUT, value = "/{scheduledWorkoutId}")
  public ResponseEntity updateWorkout(@PathVariable UUID scheduledWorkoutId,
                                      @RequestBody @Valid CreateOrUpdateScheduledWorkoutRequest workoutRequest) {
    return saveOrUpdateWorkout(workoutRequest, Optional.of(scheduledWorkoutId));
  }

  /**
   * Will switch the status of the given workout to it's opposite (active an unactive scheduled workout,
   * and deactive an active workout). Only one workout can be active at a given time.
   *
   * @param scheduledWorkoutId The id of the scheduled workout in which to alter.
   * @return 204 if updated, otherwise an error.
   */
  @RequestMapping(method = POST, value = "/{scheduledWorkoutId}/update-status")
  public ResponseEntity updateScheduledWorkoutStatus(@PathVariable UUID scheduledWorkoutId) {
    try {
      scheduledWorkoutService.updateStatus(scheduledWorkoutId);
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    } catch (ResourceValidationException e) {
      return new ResponseEntity(new BadRequestResponse(e.getErrorsById(), e.getErrorsByName(), e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  private ResponseEntity<ScheduledWorkout> saveOrUpdateWorkout(CreateOrUpdateScheduledWorkoutRequest workoutRequest, Optional<UUID> workoutId) {
    try {
      val workout = toDomain(workoutRequest, workoutId);

      ScheduledWorkout savedScheduledWorkout;
      HttpStatus successStatus;
      if (workoutId.isPresent()) {
        savedScheduledWorkout = scheduledWorkoutService.update(workout, workoutId.get());
        successStatus = HttpStatus.OK;
      } else {
        savedScheduledWorkout = scheduledWorkoutService.create(workout);
        successStatus = HttpStatus.CREATED;
      }

      return new ResponseEntity(savedScheduledWorkout, successStatus);
    } catch (ResourceValidationException e) {
      return new ResponseEntity(new BadRequestResponse(e.getErrorsById(), e.getErrorsByName(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  private ResponseEntity<ScheduledWorkout> returnWorkoutIfFound(Optional<ScheduledWorkout> workout) {
    if (workout.isPresent()) {
      return new ResponseEntity(workout.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  private ScheduledWorkout toDomain(CreateOrUpdateScheduledWorkoutRequest workoutRequest, Optional<UUID> scheduledWorkoutId)
          throws ResourceValidationException {
    val exerciseId = workoutRequest.getWorkoutId();
    val exercise = workoutService.findExercise(exerciseId);
    if (exercise.isPresent()) {
      return new ScheduledWorkout(scheduledWorkoutId.orElseGet(UUID::randomUUID),
          workoutRequest.getWorkoutDate(), true, exercise.get());
    } else {
      val errors = Collections.singletonMap(exerciseId, Collections.singletonList("Could not find Exercise."));
      throw new ResourceValidationException(errors, null, null);
    }
  }
}
