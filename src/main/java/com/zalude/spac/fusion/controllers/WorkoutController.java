package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.models.domain.Exercise;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.models.request.CreateOrUpdateExerciseRequest;
import com.zalude.spac.fusion.models.request.CreateOrUpdateWorkoutRequest;
import com.zalude.spac.fusion.models.response.error.BadRequestResponse;
import com.zalude.spac.fusion.models.response.error.ResourceNotFoundResponse;
import com.zalude.spac.fusion.services.WorkoutService;
import lombok.NonNull;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@RestController
@RequestMapping("workouts")
public class WorkoutController {

  @NonNull
  private WorkoutService workoutService;

  @Inject
  public WorkoutController(WorkoutService workoutService) {
    this.workoutService = workoutService;
  }

  @RequestMapping(method = GET)
  public ResponseEntity<Iterable<Workout>> getAllWorkouts() {
    return new ResponseEntity(workoutService.findAllWorkouts(), HttpStatus.OK);
  }

  @RequestMapping(method = GET, value = "/{workoutId}")
  public ResponseEntity<Workout> getWorkout(@PathVariable UUID workoutId) {
    val workout = workoutService.findExercise(workoutId);
    if (workout.isPresent()) {
      return new ResponseEntity(workout.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(method = POST)
  public ResponseEntity createWorkout(@RequestBody @Valid CreateOrUpdateWorkoutRequest exerciseRequest) {
    try {
      return createOrUpdateWorkout(toDomain(exerciseRequest, UUID.randomUUID()), false);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(method = PUT, value = "/{workoutId}")
  public ResponseEntity updateWorkout(@PathVariable UUID exerciseId,
                                      @RequestBody @Valid CreateOrUpdateWorkoutRequest exerciseRequest) {
    try {
      val exercise = toDomain(exerciseRequest, exerciseId);
      return createOrUpdateWorkout(exercise, true);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  private ResponseEntity createOrUpdateWorkout(Workout workout, boolean isUpdate) {
    Workout savedWorkout;
    HttpStatus successStatus;
    try {
      if (isUpdate) {
        savedWorkout = workoutService.update(workout);
        successStatus = HttpStatus.OK;
      } else {
        savedWorkout = workoutService.create(workout);
        successStatus = HttpStatus.CREATED;
      }
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity(new ResourceNotFoundResponse(e.getId()), HttpStatus.NOT_FOUND);
    } catch (ResourceValidationException e) {
      return new ResponseEntity(new BadRequestResponse(e.getErrorsById(), e.getErrorsByName(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity(savedWorkout, successStatus);
  }

  private Workout toDomain(CreateOrUpdateWorkoutRequest exerciseRequest, UUID exerciseId) {
    val workout = new Workout(exerciseRequest.getName(), exerciseRequest.getInstructions(), Collections.emptyList());
    workout.setId(exerciseId);

    val exerciseList = exerciseRequest.getExercises().stream()
        .map(optionRequest -> toDomain(optionRequest, workout))
        .collect(Collectors.toList());
    workout.setExercises(exerciseList);
    return workout;
  }

  private Exercise toDomain(CreateOrUpdateExerciseRequest exerciseOptionRequest, Workout workout) {
    UUID exerciseIdToUser;
    val exerciseOptionRequestId = exerciseOptionRequest.getId();
    if (exerciseOptionRequestId != null) {
      exerciseIdToUser = exerciseOptionRequestId;
    } else {
      exerciseIdToUser = UUID.randomUUID();
    }

    // create and set the proper fields on the ExerciseOption
    val exercise = new Exercise(exerciseIdToUser, exerciseOptionRequest.getName());
    exercise.setAmount(exerciseOptionRequest.getTargetAmount());
    exercise.setWorkout(workout);

    return exercise;
  }
}
