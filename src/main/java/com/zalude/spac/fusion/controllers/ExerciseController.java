package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.models.domain.Exercise;
import com.zalude.spac.fusion.models.domain.ExerciseOption;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.models.request.CreateOrUpdateExerciseOptionRequest;
import com.zalude.spac.fusion.models.request.CreateOrUpdateExerciseRequest;
import com.zalude.spac.fusion.models.response.error.BadRequestResponse;
import com.zalude.spac.fusion.models.response.error.ResourceNotFoundResponse;
import com.zalude.spac.fusion.services.ExerciseService;
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
@RequestMapping("/exercises")
public class ExerciseController {

  @NonNull
  private ExerciseService exerciseService;

  @Inject
  public ExerciseController(ExerciseService exerciseService) {
    this.exerciseService = exerciseService;
  }

  @RequestMapping(method = GET)
  public ResponseEntity<Iterator<Workout>> getAllExercises() {
    return new ResponseEntity(exerciseService.findAllExercises(), HttpStatus.OK);
  }

  @RequestMapping(method = GET, value = "/{exerciseId}")
  public ResponseEntity getExercises(@PathVariable UUID exerciseId) {
    val exercise = exerciseService.findExercise(exerciseId);
    if (exercise.isPresent()) {
      return new ResponseEntity(exercise.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(method = POST)
  public ResponseEntity createExercise(@RequestBody @Valid CreateOrUpdateExerciseRequest exerciseRequest) {
    val exercise = toDomain(exerciseRequest, UUID.randomUUID());
    return createOrUpdateExercise(exercise, false);
  }

  @RequestMapping(method = PUT, value = "/{exerciseId}")
  public ResponseEntity updateExercise(@PathVariable UUID exerciseId,
                                       @RequestBody @Valid CreateOrUpdateExerciseRequest exerciseRequest) {
    val exercise = toDomain(exerciseRequest, exerciseId);
    return createOrUpdateExercise(exercise, true);
  }

  private ResponseEntity createOrUpdateExercise(Exercise exercise, boolean isUpdate) {
    Exercise savedExercise;
    HttpStatus successStatus;
    try {
      if (isUpdate) {
        savedExercise = exerciseService.update(exercise);
        successStatus = HttpStatus.OK;
      } else {
        savedExercise = exerciseService.create(exercise);
        successStatus = HttpStatus.CREATED;
      }
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity(new ResourceNotFoundResponse(e.getId()), HttpStatus.NOT_FOUND);
    } catch (ResourceValidationException e) {
      return new ResponseEntity(new BadRequestResponse(e.getErrorsById(), e.getErrorsByName(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity(savedExercise, successStatus);
  }

  private Exercise toDomain(CreateOrUpdateExerciseRequest exerciseRequest, UUID exerciseId) {
    val exercise = new Exercise(exerciseRequest.getName(), exerciseRequest.getDescription(), Collections.emptyList());
    exercise.setId(exerciseId);

    val exerciseOptions = exerciseRequest.getExerciseOptions().stream()
        .map(optionRequest -> toDomain(optionRequest, exercise))
        .collect(Collectors.toList());
    exercise.setExerciseOptions(exerciseOptions);
    return exercise;
  }

  private ExerciseOption toDomain(CreateOrUpdateExerciseOptionRequest exerciseOptionRequest, Exercise exercise) {
    UUID exericeOptionIdToUse;
    val exerciseOptionRequestId = exerciseOptionRequest.getId();
    if (exerciseOptionRequestId != null) {
      exericeOptionIdToUse = exerciseOptionRequestId;
    } else {
      exericeOptionIdToUse = UUID.randomUUID();
    }

    val exerciseOption = new ExerciseOption(exericeOptionIdToUse, exerciseOptionRequest.getName(),
        exerciseOptionRequest.getType(), exerciseOptionRequest.getTargetAmount());

    exerciseOption.setDescription(exerciseOptionRequest.getDescription());
    exerciseOption.setExercise(exercise);

    val alternativeOptionRequest = exerciseOptionRequest.getAlternativeExerciseOption();
    ExerciseOption alternativeExerciseOption = null;
    if (alternativeOptionRequest != null) {
      UUID alternateExerciseOptionId;
      if (alternativeOptionRequest.getId() != null) {
        alternateExerciseOptionId = alternativeOptionRequest.getId();
      } else {
        alternateExerciseOptionId = UUID.randomUUID();
      }

      alternativeExerciseOption = new ExerciseOption(alternateExerciseOptionId, alternativeOptionRequest.getName(),
          alternativeOptionRequest.getType(), alternativeOptionRequest.getTargetAmount());
    }
    exerciseOption.setAlternativeExerciseOption(alternativeExerciseOption); // set the child

    return exerciseOption;
  }
}
