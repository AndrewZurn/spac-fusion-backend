package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

  List<Exercise> findByWorkoutId(UUID workoutId);

  List<Exercise> findByNameAndWorkoutIdNot(String name, UUID workoutId);

  List<Exercise> findByName(String name);
}
