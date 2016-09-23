package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.WorkoutWithDate;
import com.zalude.spac.fusion.models.request.CreateOrUpdateWorkoutWithDateRequest;
import com.zalude.spac.fusion.models.response.error.BadRequestResponse;
import com.zalude.spac.fusion.services.WorkoutService;
import com.zalude.spac.fusion.services.WorkoutByDateService;
import lombok.NonNull;
import lombok.val;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(path = "/workouts")
public class WorkoutWithDateController {

  @NonNull
  private WorkoutByDateService workoutByDateService;

  @NonNull
  private WorkoutService workoutService;

  @Inject
  public WorkoutWithDateController(WorkoutByDateService workoutByDateService, WorkoutService workoutService) {
    this.workoutByDateService = workoutByDateService;
    this.workoutService = workoutService;
  }

  @RequestMapping(method = GET)
  public ResponseEntity<List<WorkoutWithDate>> getAllWorkouts() {
    List<WorkoutWithDate> workoutWithDates = (List<WorkoutWithDate>) this.workoutByDateService.findAllWorkouts();
    val workoutsResponse = workoutWithDates.stream()
        .collect(Collectors.toList());
    return new ResponseEntity(workoutsResponse, HttpStatus.OK);
  }

  @RequestMapping(method = GET, value = "/{workoutId}")
  public ResponseEntity getWorkout(@PathVariable UUID workoutId) {
    return returnWorkoutIfFound(this.workoutByDateService.findWorkout(workoutId));
  }

  @RequestMapping(method = GET, value = "/today")
  public ResponseEntity getTodaysWorkout() {
    return returnWorkoutIfFound(this.workoutByDateService.findTodaysWorkout());
  }

  @RequestMapping(method = GET, value = "/week")
  public ResponseEntity getThisWeeksWorkouts() {
    return new ResponseEntity(workoutByDateService.findRemainingWorkoutsForWeek(), HttpStatus.OK);
  }

  @RequestMapping(method = GET, value = "/by-date/{workoutDate}")
  public ResponseEntity getThisWeeksWorkouts(@PathVariable @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate workoutDate) {
    return returnWorkoutIfFound(workoutByDateService.findWorkoutForDate(workoutDate));
  }

  @RequestMapping(method = POST)
  public ResponseEntity createWorkout(@RequestBody @Valid CreateOrUpdateWorkoutWithDateRequest workoutRequest) {
    return saveOrUpdateWorkout(workoutRequest, Optional.empty());
  }

  @RequestMapping(method = PUT, value = "/{workoutId}")
  public ResponseEntity updateWorkout(@PathVariable UUID workoutId,
                                      @RequestBody @Valid CreateOrUpdateWorkoutWithDateRequest workoutRequest) {
    return saveOrUpdateWorkout(workoutRequest, Optional.of(workoutId));
  }

  private ResponseEntity<WorkoutWithDate> saveOrUpdateWorkout(CreateOrUpdateWorkoutWithDateRequest workoutRequest, Optional<UUID> workoutId) {
    try {
      val workout = toDomain(workoutRequest, workoutId);

      WorkoutWithDate savedWorkoutWithDate;
      HttpStatus successStatus;
      if (workoutId.isPresent()) {
        savedWorkoutWithDate = workoutByDateService.update(workout, workoutId.get());
        successStatus = HttpStatus.OK;
      } else {
        savedWorkoutWithDate = workoutByDateService.create(workout);
        successStatus = HttpStatus.CREATED;
      }

      return new ResponseEntity(savedWorkoutWithDate, successStatus);
    } catch (ResourceValidationException e) {
      return new ResponseEntity(new BadRequestResponse(e.getErrorsById(), e.getErrorsByName(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  private ResponseEntity<WorkoutWithDate> returnWorkoutIfFound(Optional<WorkoutWithDate> workout) {
    if (workout.isPresent()) {
      return new ResponseEntity(workout.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  private WorkoutWithDate toDomain(CreateOrUpdateWorkoutWithDateRequest workoutRequest, Optional<UUID> workoutWithDateId) throws ResourceValidationException {
    val exerciseId = workoutRequest.getWorkoutId();
    val exercise = workoutService.findExercise(exerciseId);
    if (exercise.isPresent()) {
      return new WorkoutWithDate(workoutWithDateId.orElseGet(UUID::randomUUID),
          workoutRequest.getWorkoutDate(), exercise.get());
    } else {
      val errors = Collections.singletonMap(exerciseId, Collections.singletonList("Could not find Exercise."));
      throw new ResourceValidationException(errors, null, null);
    }
  }
}
