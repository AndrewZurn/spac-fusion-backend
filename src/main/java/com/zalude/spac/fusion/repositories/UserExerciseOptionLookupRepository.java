package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.UserExerciseOptionLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository to access the {@link com.zalude.spac.fusion.models.domain.UserExerciseOptionLookup}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface UserExerciseOptionLookupRepository extends JpaRepository<UserExerciseOptionLookup, UUID> {

  @Modifying
  @Transactional
  @Query("UPDATE UserExerciseOptionLookup u SET u.amountCompleted = :amountCompleted where u.id = :userExerciseOptionLookupId")
  void updateLookup(@Param("userExerciseOptionLookupId") UUID userExerciseOptionLookupId,
                    @Param("amountCompleted") String amountCompleted);

  Iterable<UserExerciseOptionLookup> findAllByUserId(UUID userId);

  Iterable<UserExerciseOptionLookup> findAllByUserIdAndWorkoutId(UUID userId, UUID workoutId);

  @Query("SELECT u FROM UserExerciseOptionLookup u " +
      "WHERE u.workout.workoutDate BETWEEN :startOfWeek AND :endOfWeek " +
      "AND u.user.id = :userId")
  Iterable<UserExerciseOptionLookup> findAllByUserIdForWeek(@Param("userId") UUID userId,
                                                            @Param("startOfWeek") LocalDate startOfWeek,
                                                            @Param("endOfWeek") LocalDate endOfWeek);
}
