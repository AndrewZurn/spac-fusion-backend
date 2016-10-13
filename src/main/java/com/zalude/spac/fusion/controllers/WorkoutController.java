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
@RequestMapping(path = "/workouts")
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
  public ResponseEntity createWorkout(@RequestBody @Valid CreateOrUpdateWorkoutRequest workoutRequest) {
    try {
      return createOrUpdateWorkout(toDomain(workoutRequest, UUID.randomUUID()), false);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(method = PUT, value = "/{workoutId}")
  public ResponseEntity updateWorkout(@PathVariable UUID workoutId,
                                      @RequestBody @Valid CreateOrUpdateWorkoutRequest workoutRequest) {
    try {
      val exercise = toDomain(workoutRequest, workoutId);
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

  private Workout toDomain(CreateOrUpdateWorkoutRequest workoutRequest, UUID workoutId) {
    val workout = new Workout(workoutRequest.getName(), workoutRequest.getWorkoutType(), Collections.emptyList());
    workout.setInput(workoutRequest.getInput());
    workout.setId(workoutId);
    workout.setInstructions(workoutRequest.getInstructions());
    workout.setDuration(workoutRequest.getDuration());

    val exerciseList = workoutRequest.getExercises().stream()
        .map(optionRequest -> toDomain(optionRequest, workout))
        .collect(Collectors.toList());
    workout.setExercises(exerciseList);
    return workout;
  }

  private Exercise toDomain(CreateOrUpdateExerciseRequest exerciseRequest, Workout workout) {
    UUID exerciseIdToUser;
    val exerciseOptionRequestId = exerciseRequest.getId();
    if (exerciseOptionRequestId != null) {
      exerciseIdToUser = exerciseOptionRequestId;
    } else {
      exerciseIdToUser = UUID.randomUUID();
    }

    // create and set the proper fields on the ExerciseOption
    val exercise = new Exercise(exerciseIdToUser, exerciseRequest.getName(), exerciseRequest.getAmount());
    exercise.setInput(exerciseRequest.getInput());
    exercise.setWorkout(workout);

    return exercise;
  }
}
