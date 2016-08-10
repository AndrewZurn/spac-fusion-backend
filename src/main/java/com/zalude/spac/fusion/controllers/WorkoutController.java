package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.models.request.CreateOrUpdateWorkoutRequest;
import com.zalude.spac.fusion.models.response.error.BadRequestResponse;
import com.zalude.spac.fusion.services.ExerciseService;
import com.zalude.spac.fusion.services.WorkoutService;
import lombok.NonNull;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;

import java.util.*;

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

  @NonNull
  private ExerciseService exerciseService;

  @Inject
  public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService) {
    this.workoutService = workoutService;
    this.exerciseService = exerciseService;
  }

  @RequestMapping(method = GET)
  public ResponseEntity getAllWorkouts() {
    return new ResponseEntity(this.workoutService.findAllWorkouts(), HttpStatus.OK);
  }

  @RequestMapping(method = GET, value = "/{workoutId}")
  public ResponseEntity getWorkout(@PathVariable UUID workoutId) {
    return returnWorkoutIfFound(this.workoutService.findWorkout(workoutId));
  }

  @RequestMapping(method = GET, value = "/today")
  public ResponseEntity getTodaysWorkout() {
    return returnWorkoutIfFound(this.workoutService.findTodaysWorkout());
  }

  @RequestMapping(method = GET, value = "/week")
  public ResponseEntity getThisWeeksWorkouts() {
    return new ResponseEntity(workoutService.findRemainingWorkoutsForWeek(), HttpStatus.OK);
  }

  @RequestMapping(method = POST)
  public ResponseEntity createWorkout(@RequestBody @Valid CreateOrUpdateWorkoutRequest workoutRequest) {
    return saveOrUpdateWorkout(workoutRequest, Optional.empty());
  }

  @RequestMapping(method = PUT, value = "/{workoutId}")
  public ResponseEntity updateWorkout(@PathVariable UUID workoutId,
                                      @RequestBody @Valid CreateOrUpdateWorkoutRequest workoutRequest) {
    return saveOrUpdateWorkout(workoutRequest, Optional.of(workoutId));
  }

  public ResponseEntity saveOrUpdateWorkout(CreateOrUpdateWorkoutRequest workoutRequest, Optional<UUID> workoutId) {
    try {
      val workout = toDomain(workoutRequest, workoutId);

      Workout savedWorkout;
      HttpStatus successStatus;
      if (workoutId.isPresent()) {
        savedWorkout = workoutService.update(workout, workoutId.get());
        successStatus = HttpStatus.OK;
      } else {
        savedWorkout = workoutService.create(workout);
        successStatus = HttpStatus.CREATED;
      }

      return new ResponseEntity(savedWorkout, successStatus);
    } catch (ResourceValidationException e) {
      return new ResponseEntity(new BadRequestResponse(e.getErrorsById(), e.getErrorsByName(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  private ResponseEntity returnWorkoutIfFound(Optional<Workout> workout) {
    if (workout.isPresent()) {
      return new ResponseEntity(workout.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  private Workout toDomain(CreateOrUpdateWorkoutRequest workoutRequest, Optional<UUID> workoutId) throws ResourceValidationException {
    val exerciseId = workoutRequest.getExerciseId();
    val exercise = exerciseService.findExercise(exerciseId);
    if (exercise.isPresent()) {
      return new Workout(workoutId.orElseGet(UUID::randomUUID), workoutRequest.getDuration(),
          workoutRequest.getWorkoutDate(), exercise.get());
    } else {
      val errors = Collections.singletonMap(exerciseId, Collections.singletonList("Could not find Exercise."));
      throw new ResourceValidationException(errors, null, null);
    }
  }
}
