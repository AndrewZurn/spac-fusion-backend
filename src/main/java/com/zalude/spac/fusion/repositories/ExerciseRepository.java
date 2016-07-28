package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Repository to access the {@link com.zalude.spac.fusion.models.domain.Exercise}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

  /**
   * Find Exercises where the name matches the given name and the id does not match the given name.
   * This might be useful for such operations as finding if a name already exists but does not belong to the
   * given Exercise for the given id.
   *
   * @param name The name of the Exercise to match on.
   * @param id The id of the Exercise to exclude from the results.
   * @return The Exercises that don't match the given id and that match the given name.
   */
  List<Exercise> findByNameAndIdNot(String name, UUID id);
}
