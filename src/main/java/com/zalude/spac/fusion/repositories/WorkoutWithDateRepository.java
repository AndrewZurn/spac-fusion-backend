package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.WorkoutWithDate;
import com.zalude.spac.fusion.models.domain.WorkoutWithDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repository to access the {@link WorkoutWithDate}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface WorkoutWithDateRepository extends JpaRepository<WorkoutWithDate, UUID> {

  @Query("SELECT w FROM WorkoutWithDate w WHERE w.workoutDate = current_date")
  WorkoutWithDate findTodaysWorkout();

  @Query("SELECT w FROM WorkoutWithDate w WHERE w.workoutDate BETWEEN :dateStartRange AND :dateEndRange")
  Iterable<WorkoutWithDate> findAllWorkouts(@Param("dateStartRange") LocalDate dateStartRange,
                                            @Param("dateEndRange") LocalDate dateEndRange);

  WorkoutWithDate findOneByWorkoutDate(LocalDate workoutDate);
}
