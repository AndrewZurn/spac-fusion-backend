package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository to access the {@link com.zalude.spac.fusion.models.domain.Workout}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

}
