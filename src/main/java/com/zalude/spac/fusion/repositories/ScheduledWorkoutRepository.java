package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.ScheduledWorkout;
import com.zalude.spac.fusion.models.domain.ScheduledWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repository to access the {@link ScheduledWorkout}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface ScheduledWorkoutRepository extends JpaRepository<ScheduledWorkout, UUID> {

  @Query("SELECT w FROM ScheduledWorkout w WHERE w.workoutDate = current_date")
  ScheduledWorkout findTodaysWorkout();

  @Query("SELECT w FROM ScheduledWorkout w WHERE w.workoutDate BETWEEN :dateStartRange AND :dateEndRange")
  Iterable<ScheduledWorkout> findAllWorkouts(@Param("dateStartRange") LocalDate dateStartRange,
                                             @Param("dateEndRange") LocalDate dateEndRange);

  ScheduledWorkout findOneByWorkoutDate(LocalDate workoutDate);
}
