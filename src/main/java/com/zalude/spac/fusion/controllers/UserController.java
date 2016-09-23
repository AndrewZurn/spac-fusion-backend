package com.zalude.spac.fusion.controllers;

import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.*;
import com.zalude.spac.fusion.models.request.*;
import com.zalude.spac.fusion.models.response.UserRemainingWorkoutUnlocksResponse;
import com.zalude.spac.fusion.models.response.UserCompletedWorkoutResponse;
import com.zalude.spac.fusion.models.response.error.ResourceNotFoundResponse;
import com.zalude.spac.fusion.services.WorkoutService;
import com.zalude.spac.fusion.services.UserService;
import com.zalude.spac.fusion.services.WorkoutByDateService;
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
  private WorkoutByDateService workoutByDateService;

  @Inject
  public UserController(UserService userService, WorkoutByDateService workoutByDateService) {
    this.userService = userService;
    this.workoutByDateService = workoutByDateService;
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
    val userCompletedWorkoutLookups = userService.getCompletedWorkoutsForUser(userId, page, pageSize);
    if (userCompletedWorkoutLookups.isPresent()) {
      val userCompletedWorkoutResponses = userCompletedWorkoutLookups.get().stream()
          .map(this::asResponse)
          .collect(Collectors.toList());
      return new ResponseEntity(userCompletedWorkoutResponses, HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  // GET COMPLETED WORKOUTS FOR USER
  @RequestMapping(method = RequestMethod.GET, value = "/{userId}/workouts/{workoutWithDateId}")
  public ResponseEntity getCompletedWorkout(@PathVariable UUID userId,
                                            @PathVariable UUID workoutId) {
    val userCompletedWorkoutLookup = userService.getCompletedWorkoutForUser(userId, workoutId);
    final Optional<UserCompletedWorkoutResponse> userCompletedWorkoutResponse =
        userCompletedWorkoutLookup.map(this::asResponse);

    if (userCompletedWorkoutResponse.isPresent()) {
      return new ResponseEntity(userCompletedWorkoutResponse.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  // CREATE COMPLETED USER WORKOUT
  @RequestMapping(method = RequestMethod.POST, value = "/{userId}/workouts/{workoutWithDateId}")
  public ResponseEntity<UserCompletedWorkoutResponse> createOrUpdateCompletedWorkout(@PathVariable UUID userId,
                                                                                     @PathVariable UUID workoutWithDateId,
                                                                                     @RequestBody @Valid UserCompletedWorkoutRequest completedWorkoutRequest) {
    try {
      val userCompletedWorkoutLookup = toDomain(completedWorkoutRequest, workoutWithDateId, userId);
      val savedLookup = asResponse(userService.saveUserExerciseOptionLookup(userCompletedWorkoutLookup));

      return new ResponseEntity(savedLookup, HttpStatus.CREATED);
    } catch (ResourceValidationException | IllegalArgumentException e) {
      return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity(new ResourceNotFoundResponse(e.getId()), HttpStatus.NOT_FOUND);
    }
  }

  private FusionUser toDomain(UserRequest userRequest, Optional<UUID> userId) throws ResourceValidationException {
    String programLevel = FusionUser.ProgramLevel.valueOf(userRequest.getProgramLevel().toUpperCase()).getName();
    return new FusionUser(userId.orElseGet(UUID::randomUUID), userRequest.getAuth0Id(), userRequest.getFirstName(), userRequest.getLastName(),
        userRequest.getUsername(), userRequest.getEmail(), userRequest.getAge(), userRequest.getHeight(), userRequest.getWeight(),
        programLevel);
  }

  private UserCompletedWorkoutResponse asResponse(UserCompletedWorkoutLookup userCompletedWorkoutLookup) {
    return asUserCompletedWorkoutResponse(userCompletedWorkoutLookup);
  }

  private UserCompletedWorkoutResponse asUserCompletedWorkoutResponse(UserCompletedWorkoutLookup lookup) {
    return new UserCompletedWorkoutResponse(lookup.getId(), lookup.getWorkoutWithDate(), lookup.getResult());
  }

  private UserCompletedWorkoutLookup toDomain(UserCompletedWorkoutRequest completedWorkoutRequest, UUID workoutId, UUID userId) throws ResourceValidationException {
    // check if user exists
    // TODO: these database-type of interactions should probably be validated in the service layer
    val user = userService.find(userId).orElseThrow(() -> {
      final Map<UUID, List<String>> errors = Collections.singletonMap(userId, Collections.singletonList("Could not find User."));
      return new ResourceValidationException(errors, null, null);
    });

    val workout = workoutByDateService.findWorkout(workoutId)
        .orElseThrow(() ->
            new ResourceValidationException(Collections.singletonMap(workoutId, Collections.singletonList("Could not find Workout")), null, null));

    UUID lookupIdToSave;
    UUID id = completedWorkoutRequest.getWorkoutId();
    if (id != null) {
      lookupIdToSave = id;
    } else {
      lookupIdToSave = UUID.randomUUID();
    }

    return new UserCompletedWorkoutLookup(lookupIdToSave, user, workout, completedWorkoutRequest.getResult());
  }
}
