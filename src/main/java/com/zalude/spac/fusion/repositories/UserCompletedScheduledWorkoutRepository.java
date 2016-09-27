package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.UserCompletedScheduledWorkout;
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
 * Repository to access the {@link UserCompletedScheduledWorkout}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface UserCompletedScheduledWorkoutRepository extends JpaRepository<UserCompletedScheduledWorkout, UUID> {

  List<UserCompletedScheduledWorkout> findAllByUserId(UUID userId, Pageable pageable);

  UserCompletedScheduledWorkout findByUserIdAndScheduledWorkoutId(UUID userId, UUID scheduledWorkoutId);

  @Query("SELECT u FROM UserCompletedScheduledWorkout u " +
      "WHERE u.scheduledWorkout.workoutDate BETWEEN :startOfWeek AND :endOfWeek AND u.user.id = :userId")
  Iterable<UserCompletedScheduledWorkout> findAllByUserIdForWeek(@Param("userId") UUID userId,
                                                                 @Param("startOfWeek") LocalDate startOfWeek,
                                                                 @Param("endOfWeek") LocalDate endOfWeek);

  @Query("SELECT count(distinct u.scheduledWorkout.id) FROM UserCompletedScheduledWorkout u " +
      "WHERE u.scheduledWorkout.workoutDate BETWEEN :startOfWeek AND :endOfWeek AND u.user.id = :userId")
  Integer completedWorkoutsForWeek(@Param("userId") UUID userId,
                                   @Param("startOfWeek") LocalDate startOfWeek,
                                   @Param("endOfWeek") LocalDate endOfWeek);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserCompletedScheduledWorkout u WHERE u.scheduledWorkout.id = :scheduledWorkoutId AND u.user.id = :userId")
  void deleteUserPreviousLookups(@Param("scheduledWorkoutId") UUID scheduledWorkoutId,
                                 @Param("userId") UUID userId);
}
