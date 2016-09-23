package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.UserCompletedWorkoutLookup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository to access the {@link UserCompletedWorkoutLookup}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface UserCompletedWorkoutLookupRepository extends JpaRepository<UserCompletedWorkoutLookup, UUID> {

  List<UserCompletedWorkoutLookup> findAllByUserId(UUID userId, Pageable pageable);

  UserCompletedWorkoutLookup findByUserIdAndWorkoutWithDateId(UUID userId, UUID workoutWithDateId);

  @Query("SELECT u FROM UserCompletedWorkoutLookup u " +
      "WHERE u.workoutWithDate.workoutDate BETWEEN :startOfWeek AND :endOfWeek AND u.user.id = :userId")
  Iterable<UserCompletedWorkoutLookup> findAllByUserIdForWeek(@Param("userId") UUID userId,
                                                              @Param("startOfWeek") LocalDate startOfWeek,
                                                              @Param("endOfWeek") LocalDate endOfWeek);

  @Query("SELECT count(distinct u.workout.id) FROM UserCompletedWorkoutLookup u " +
      "WHERE u.workoutWithDate.workoutDate BETWEEN :startOfWeek AND :endOfWeek AND u.user.id = :userId")
  Integer completedWorkoutsForWeek(@Param("userId") UUID userId,
                                   @Param("startOfWeek") LocalDate startOfWeek,
                                   @Param("endOfWeek") LocalDate endOfWeek);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserCompletedWorkoutLookup u WHERE u.workoutWithDate.id = :workoutId AND u.user.id = :userId")
  void deleteUserPreviousLookups(@Param("workoutWithDateId") UUID workoutWithDateId,
                                 @Param("userId") UUID userId);
}
