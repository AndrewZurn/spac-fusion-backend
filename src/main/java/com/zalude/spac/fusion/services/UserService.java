package com.zalude.spac.fusion.services;

import com.zalude.spac.fusion.exceptions.ResourceNotFoundException;
import com.zalude.spac.fusion.exceptions.ResourceValidationException;
import com.zalude.spac.fusion.models.domain.FusionUser;
import com.zalude.spac.fusion.models.domain.UserCompletedWorkoutLookup;
import com.zalude.spac.fusion.repositories.UserCompletedWorkoutLookupRepository;
import com.zalude.spac.fusion.repositories.UserRepository;
import lombok.NonNull;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Service
public class UserService {

  @NonNull
  private UserCompletedWorkoutLookupRepository userCompletedWorkoutLookupRepository;
  @NonNull
  private UserRepository userRepository;

  @Inject
  public UserService(UserCompletedWorkoutLookupRepository userCompletedWorkoutLookupRepository, UserRepository userRepository) {
    this.userCompletedWorkoutLookupRepository = userCompletedWorkoutLookupRepository;
    this.userRepository = userRepository;
  }

  public Iterable<FusionUser> findAllUsers() {
    return userRepository.findAll();
  }

  public Optional<FusionUser> find(UUID userId) {
    return Optional.ofNullable(userRepository.findOne(userId));
  }

  public Optional<FusionUser> findByAuth0Id(String auth0Id) {
    return Optional.ofNullable(userRepository.findByAuth0Id(auth0Id));
  }

  public FusionUser create(FusionUser user) throws ResourceValidationException {
    return userRepository.save(user);
  }

  public FusionUser update(FusionUser user) throws ResourceValidationException, ResourceNotFoundException {
    val existingUser = find(user.getId()).orElseThrow(() -> new ResourceNotFoundException(user.getId(), "User not found."));
    updateUserInstanceField(user, existingUser);
    return userRepository.save(existingUser);
  }

  public Optional<List<UserCompletedWorkoutLookup>> getCompletedWorkoutsForUser(UUID userId, Integer page, Integer pageSize) {
    val user = find(userId);
    if (!user.isPresent()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(userCompletedWorkoutLookupRepository.findAllByUserId(userId, new PageRequest(page, pageSize)));
    }
  }

  public Optional<UserCompletedWorkoutLookup> getCompletedWorkoutForUser(UUID userId, UUID workoutWithDateId) {
    val user = find(userId);
    if (!user.isPresent()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(userCompletedWorkoutLookupRepository.findByUserIdAndWorkoutWithDateId(userId, workoutWithDateId));
    }
  }

  public UserCompletedWorkoutLookup saveUserExerciseOptionLookup(UserCompletedWorkoutLookup userCompletedWorkoutLookup)
      throws ResourceValidationException, ResourceNotFoundException {
    // attempt to delete the old lookups first
    userCompletedWorkoutLookupRepository.deleteUserPreviousLookups(userCompletedWorkoutLookup.getWorkoutWithDate().getId(),
        userCompletedWorkoutLookup.getUser().getId());
    return userCompletedWorkoutLookupRepository.save(userCompletedWorkoutLookup);
  }

  public Optional<Integer> getUserRemainingWorkoutUnlocks(UUID userId) {
    return find(userId).map(fusionUser -> {
      LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
      LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);

      // get the user's completed workouts for the week and their program level
      Integer userCompleteWorkouts = userCompletedWorkoutLookupRepository.completedWorkoutsForWeek(userId, startOfWeek, endOfWeek);
      FusionUser.ProgramLevel userProgramLevel = FusionUser.ProgramLevel.valueOf(fusionUser.getProgramLevel().toUpperCase());

      return userProgramLevel.getWorkoutLimit() - userCompleteWorkouts;
    });
  }

  private void updateUserInstanceField(FusionUser user, FusionUser existingUser) {
    if (user.getFirstName() != null) {
      existingUser.setFirstName(user.getFirstName());
    }

    if (user.getLastName() != null) {
      existingUser.setLastName(user.getLastName());
    }

    if (user.getUsername() != null) {
      existingUser.setUsername(user.getUsername());
    }

    if (user.getEmail() != null) {
      existingUser.setEmail(user.getEmail());
    }

    if (user.getAge() != null) {
      existingUser.setAge(user.getAge());
    }

    if (user.getHeight() != null) {
      existingUser.setHeight(user.getHeight());
    }

    if (user.getWeight() != null) {
      existingUser.setWeight(user.getWeight());
    }

    if (user.getProgramLevel() != null) {
      existingUser.setProgramLevel(user.getProgramLevel());
    }
  }
}
