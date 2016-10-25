package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository to access the {@link Workout}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

  /**
   * Find Exercises where the name matches the given name and the id does not match the given name.
   * This might be useful for such operations as finding if a name already exists but does not belong to the
   * given Exercise for the given id.
   *
   * @param name The name of the Exercise to match on.
   * @param id The id of the Exercise to exclude from the results.
   * @return The Exercises that don't match the given id and that match the given name.
   */
  List<Workout> findByNameAndIdNot(String name, UUID id);
}
