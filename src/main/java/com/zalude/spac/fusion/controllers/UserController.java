package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.ExerciseOption;
import com.zalude.spac.fusion.models.domain.FusionUser;
import com.zalude.spac.fusion.models.domain.UserExerciseOptionLookup;
import com.zalude.spac.fusion.models.domain.Workout;
import com.zalude.spac.fusion.models.request.*;
import com.zalude.spac.fusion.models.response.UserRemainingWorkoutUnlocksResponse;
import com.zalude.spac.fusion.models.response.UserCompletedWorkoutResponse;
import com.zalude.spac.fusion.models.response.error.ResourceNotFoundResponse;
import com.zalude.spac.fusion.services.ExerciseService;
import com.zalude.spac.fusion.services.UserService;
import com.zalude.spac.fusion.services.WorkoutService;
import lombok.NonNull;
import lombok.val;
import org.javatuples.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

  @NonNull
  private UserService userService;

  @NonNull
  private ExerciseService exerciseService;

  @NonNull
  private WorkoutService workoutService;

  @Inject
  public UserController(UserService userService, ExerciseService exerciseService, WorkoutService workoutService) {
    this.userService = userService;
    this.exerciseService = exerciseService;
    this.workoutService = workoutService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<Iterable<FusionUser>> getUsers() {
    return new ResponseEntity(userService.findAllUsers(), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
  public ResponseEntity<FusionUser> getUser(@PathVariable UUID userId) {
    return returnUserIfFound(userService.find(userId));
  }

  @RequestMapping(method = RequestMethod.GET, value = "/auth0/{auth0Id}")
  public ResponseEntity<FusionUser> getUser(@PathVariable String auth0Id) {
    return returnUserIfFound(userService.findByAuth0Id(auth0Id));
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{userId}/remaining-workout-unlocks")
  public ResponseEntity getUserRemainingWorkoutUnlocks(@PathVariable UUID userId) {
    return userService.getUserRemainingWorkoutUnlocks(userId)
        .map(result -> new ResponseEntity(
            new UserRemainingWorkoutUnlocksResponse(userId, result),
            HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<FusionUser> createUser(@RequestBody @Valid CreateUserRequest userRequest) {
    return createOrUpdateUser(userRequest, Optional.empty());
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/{userId}")
  public ResponseEntity<FusionUser> updateUser(@PathVariable UUID userId,
                                               @RequestBody @Valid UpdateUserRequest userRequest) {
    return createOrUpdateUser(userRequest, Optional.of(userId));
  }

  private ResponseEntity returnUserIfFound(Optional<FusionUser> user) {
    if (user.isPresent()) {
      return new ResponseEntity(user.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  private ResponseEntity createOrUpdateUser(UserRequest userRequest, Optional<UUID> userId) {
    try {
      val user = toDomain(userRequest, userId);
      FusionUser savedUser;
      HttpStatus successStatus;
      if (userId.isPresent()) {
        savedUser = userService.update(user);
        successStatus = HttpStatus.OK;
      } else {
        savedUser = userService.create(user);
        successStatus = HttpStatus.CREATED;
      }
      return new ResponseEntity(savedUser, successStatus);
    } catch (ResourceValidationException e) {
      return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity(e.getId(), HttpStatus.NOT_FOUND);
    }
  }

  // GET COMPLETED WORKOUTS FOR USER
  @RequestMapping(method = RequestMethod.GET, value = "/{userId}/workouts")
  public ResponseEntity getCompletedWorkouts(@PathVariable UUID userId,
                                             @RequestParam("page") Integer page,
                                             @RequestParam("pageSize") Integer pageSize) {
    val exerciseOptionLookups = userService.getCompletedWorkoutsForUser(userId, page, pageSize);
    if (exerciseOptionLookups.isPresent()) {
      return new ResponseEntity(asResponse(exerciseOptionLookups.get()), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  // GET COMPLETED WORKOUTS FOR USER
  @RequestMapping(method = RequestMethod.GET, value = "/{userId}/workouts/{workoutId}")
  public ResponseEntity getCompletedWorkout(@PathVariable UUID userId,
                                            @PathVariable UUID workoutId) {
    val exerciseOptionLookups = userService.getCompletedWorkoutForUser(userId, workoutId);
    final Optional<UserCompletedWorkoutResponse> userCompletedWorkoutResponse = exerciseOptionLookups.flatMap(userExerciseOptionLookups -> {
      return asResponse(userExerciseOptionLookups).stream().findFirst();
    });

    if (userCompletedWorkoutResponse.isPresent()) {
      return new ResponseEntity(userCompletedWorkoutResponse.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  // CREATE COMPLETED USER WORKOUT
  @RequestMapping(method = RequestMethod.POST, value = "/{userId}/workouts/{workoutId}")
  public ResponseEntity createCompletedWorkout(@PathVariable UUID userId,
                                               @PathVariable UUID workoutId,
                                               @RequestBody @Valid UserCompletedWorkoutRequest completedWorkoutRequest) {
    try {
      val userExerciseOptionLookup = toDomain(completedWorkoutRequest, workoutId, userId);
      val savedLookups = userService.saveUserExerciseOptionLookup(userExerciseOptionLookup);
      val responseForWorkoutId = asResponse(savedLookups).stream()
        .filter(userCompletedWorkoutResponse -> userCompletedWorkoutResponse.getWorkoutId().equals(workoutId))
        .findFirst();

      if (responseForWorkoutId.isPresent()) {
        return new ResponseEntity(responseForWorkoutId.get(), HttpStatus.CREATED);
      } else {
        return new ResponseEntity(new ResourceNotFoundResponse(workoutId), HttpStatus.NOT_FOUND);
      }
    } catch (ResourceValidationException | IllegalArgumentException e) {
      return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity(new ResourceNotFoundResponse(e.getId()), HttpStatus.NOT_FOUND);
    }
  }

  // UPDATE COMPLETED USER WORKOUT
  @RequestMapping(method = RequestMethod.POST, value = "/{userId}/workouts/lookup/{userExerciseOptionLookupId}")
  public ResponseEntity updateCompletedWorkout(@PathVariable UUID userId,
                                               @PathVariable UUID userExerciseOptionLookupId,
                                               @RequestBody @Valid UpdateCompletedWorkoutRequest completedWorkoutRequest) {
    val result = this.userService.updateUserExerciseOptionLookup(userId, userExerciseOptionLookupId, completedWorkoutRequest.getResult());
    if (result) {
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  private FusionUser toDomain(UserRequest userRequest, Optional<UUID> userId) throws ResourceValidationException {
    String programLevel = FusionUser.ProgramLevel.valueOf(userRequest.getProgramLevel().toUpperCase()).getName();
    return new FusionUser(userId.orElseGet(UUID::randomUUID), userRequest.getAuth0Id(), userRequest.getFirstName(), userRequest.getLastName(),
        userRequest.getUsername(), userRequest.getEmail(), userRequest.getAge(), userRequest.getHeight(), userRequest.getWeight(),
        programLevel);
  }

  private List<UserCompletedWorkoutResponse> asResponse(Iterable<UserExerciseOptionLookup> userExerciseOptionLookups) {
    List<UserExerciseOptionLookup> convertedList = (List<UserExerciseOptionLookup>) userExerciseOptionLookups;

    val lookupsByWorkout = convertedList.stream()
        .collect(Collectors.groupingBy(UserExerciseOptionLookup::getWorkout));

    return lookupsByWorkout.entrySet().stream()
        .map(asUserCompletedWorkoutResponse())
        .collect(Collectors.toList());
  }

  private Function<Map.Entry<Workout, List<UserExerciseOptionLookup>>, UserCompletedWorkoutResponse> asUserCompletedWorkoutResponse() {
    return workoutListEntry -> {
      final Workout workout = workoutListEntry.getKey();
      final List<UserCompletedWorkoutResponse.CompletedExerciseOptionResponse> responseList = workoutListEntry.getValue().stream()
          .map(asUserCompletedExerciseResponse())
          .collect(Collectors.toList());

      return new UserCompletedWorkoutResponse(workout.getId(), workout.getDuration(), workout.getWorkoutDate(), workout.getExercise().getId(),
          workout.getExercise().getName(), workout.getExercise().getInstructions(), responseList);
    };
  }

  private Function<UserExerciseOptionLookup, UserCompletedWorkoutResponse.CompletedExerciseOptionResponse> asUserCompletedExerciseResponse() {
    return lookup -> {
      ExerciseOption exerciseOption = lookup.getExerciseOption();
      return new UserCompletedWorkoutResponse.CompletedExerciseOptionResponse(lookup.getExerciseOption().getId(),
          exerciseOption.getName(), exerciseOption.getDescription(), exerciseOption.getType(),
          exerciseOption.getTargetAmount(), exerciseOption.getDuration(), lookup.getResult());
    };
  }

  private List<UserExerciseOptionLookup> toDomain(UserCompletedWorkoutRequest completedWorkoutRequest, UUID workoutId, UUID userId) throws ResourceValidationException {
    // check if user exists
    val user = userService.find(userId).orElseThrow(() -> {
      final Map<UUID, List<String>> errors = Collections.singletonMap(userId, Collections.singletonList("Could not find User."));
      return new ResourceValidationException(errors, null, null);
    });

    val workout = workoutService.findWorkout(workoutId)
        .orElseThrow(() ->
            new ResourceValidationException(Collections.singletonMap(workoutId,
                Collections.singletonList("Could not find Workout")), null, null));

    // map results and attempt to lookup the exercise option from the request objects
    List<Pair<UserCompletedWorkoutRequest.CompletedExerciseResultRequest, Optional<ExerciseOption>>> missing =
        completedWorkoutRequest.getResults().stream()
            .map(getCompletedExerciseResultRequestPairFunction())
            .filter(objects -> !objects.getValue1().isPresent())
            .collect(Collectors.toList());

    List<Pair<UserCompletedWorkoutRequest.CompletedExerciseResultRequest, Optional<ExerciseOption>>> present =
        completedWorkoutRequest.getResults().stream()
            .map(getCompletedExerciseResultRequestPairFunction())
            .filter(objects -> objects.getValue1().isPresent())
            .collect(Collectors.toList());

    // if any of the ExerciseOptions couldn't be found, throw an error.
    if (missing.size() > 0) {
      Map<UUID, List<String>> errors = Collections.emptyMap();
      for (Pair<UserCompletedWorkoutRequest.CompletedExerciseResultRequest, ?> requests : missing) {
        errors.put(requests.getValue0().getExerciseOptionId(), Arrays.asList("Could not find Exercise Option"));
      }
      throw new ResourceValidationException(errors, null, null);
    }

    // map the results into a list of user exercise option lookups
    return present.stream()
        .map(pair ->
            new UserExerciseOptionLookup(UUID.randomUUID(), user, pair.getValue1().get(), workout, pair.getValue0().getResult()))
        .collect(Collectors.toList());
  }

  private Function<UserCompletedWorkoutRequest.CompletedExerciseResultRequest, Pair<UserCompletedWorkoutRequest.CompletedExerciseResultRequest, Optional<ExerciseOption>>> getCompletedExerciseResultRequestPairFunction() {
    return resultRequest -> Pair.with(resultRequest, exerciseService.findExerciseOption(resultRequest.getExerciseOptionId()));
  }
}
