package com.zalude.spac.fusion.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.*;
import com.zalude.spac.fusion.models.request.*;
import com.zalude.spac.fusion.models.response.UserRemainingWorkoutUnlocksResponse;
import com.zalude.spac.fusion.models.response.UserCompletedWorkoutResponse;
import com.zalude.spac.fusion.models.response.error.ResourceNotFoundResponse;
import com.zalude.spac.fusion.services.UserService;
import com.zalude.spac.fusion.services.ScheduledWorkoutService;
import lombok.NonNull;
import lombok.val;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/users")
public class UserController {

  @NonNull
  private UserService userService;

  @NonNull
  private ScheduledWorkoutService scheduledWorkoutService;

  @Inject
  public UserController(UserService userService, ScheduledWorkoutService scheduledWorkoutService) {
    this.userService = userService;
    this.scheduledWorkoutService = scheduledWorkoutService;
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
  @RequestMapping(method = RequestMethod.GET, value = "/{userId}/scheduled-workouts")
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
  @RequestMapping(method = RequestMethod.GET, value = "/{userId}/scheduled-workouts/{workoutDate}")
  public ResponseEntity getCompletedWorkout(@PathVariable UUID userId,
                                            @PathVariable @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate workoutDate) {
    val userCompletedWorkoutLookup = userService.getCompletedWorkoutForUser(userId, workoutDate);
    final Optional<UserCompletedWorkoutResponse> userCompletedWorkoutResponse =
        userCompletedWorkoutLookup.map(this::asResponse);

    if (userCompletedWorkoutResponse.isPresent()) {
      return new ResponseEntity(userCompletedWorkoutResponse.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  // CREATE COMPLETED USER WORKOUT
  @RequestMapping(method = RequestMethod.POST, value = "/{userId}/scheduled-workouts/{scheduledWorkoutId}")
  public ResponseEntity<UserCompletedWorkoutResponse> createOrUpdateCompletedWorkout(@PathVariable UUID userId,
                                                                                     @PathVariable UUID scheduledWorkoutId,
                                                                                     @RequestBody @Valid UserCompletedScheduledWorkoutRequest completedWorkoutRequest) {
    try {
      val userCompletedWorkoutLookup = toDomain(completedWorkoutRequest, scheduledWorkoutId, userId);
      val savedLookup = asResponse(userService.saveUserCompletedScheduledWorkoutLookup(userCompletedWorkoutLookup));

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

  private UserCompletedWorkoutResponse asResponse(UserCompletedScheduledWorkout userCompletedScheduledWorkout) {
    return asUserCompletedWorkoutResponse(userCompletedScheduledWorkout);
  }

  private UserCompletedWorkoutResponse asUserCompletedWorkoutResponse(UserCompletedScheduledWorkout workout) {
    JsonNode workoutResult;
    val result = workout.getResult();
    val mapper = new ObjectMapper();
    try {
      workoutResult = mapper.readTree(result);
    } catch (IOException e) {
      e.printStackTrace();
      workoutResult = new TextNode("Unknown Result");
    }

    return new UserCompletedWorkoutResponse(workout.getId(), workout.getScheduledWorkout(), workoutResult);
  }

  private UserCompletedScheduledWorkout toDomain(UserCompletedScheduledWorkoutRequest completedWorkoutRequest, UUID scheduledWorkoutId, UUID userId) throws ResourceValidationException {
    // check if user exists
    // TODO: these database-type of interactions should probably be validated in the service layer
    val user = userService.find(userId).orElseThrow(() -> {
      final Map<UUID, List<String>> errors = Collections.singletonMap(userId, Collections.singletonList("Could not find User."));
      return new ResourceValidationException(errors, null, null);
    });

    val workout = scheduledWorkoutService.findWorkout(scheduledWorkoutId)
        .orElseThrow(() ->
            new ResourceValidationException(Collections.singletonMap(scheduledWorkoutId, Collections.singletonList("Could not find Workout")), null, null));

    return new UserCompletedScheduledWorkout(scheduledWorkoutId, user, workout, completedWorkoutRequest.getResult().toString());
  }
}
