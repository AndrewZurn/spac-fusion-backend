package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.ExerciseOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * TODO: DESCRIPTION OF CLASS HERE
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface ExerciseOptionRepository extends JpaRepository<ExerciseOption, UUID> {

  List<ExerciseOption> findByExerciseId(UUID exerciseId);

  List<ExerciseOption> findByNameAndExerciseIdNot(String name, UUID exerciseId);

  List<ExerciseOption> findByName(String name);
}
