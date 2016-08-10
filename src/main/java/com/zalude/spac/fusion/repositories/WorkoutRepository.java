package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repository to access the {@link com.zalude.spac.fusion.models.domain.Workout}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

  @Query("SELECT w FROM Workout w WHERE w.workoutDate = current_date")
  Workout findTodaysWorkout();

  @Query("SELECT w FROM Workout w WHERE w.workoutDate BETWEEN :dateStartRange AND :dateEndRange")
  Iterable<Workout> findAllWorkouts(@Param("dateStartRange") LocalDate dateStartRange,
                                    @Param("dateEndRange") LocalDate dateEndRange);
}
