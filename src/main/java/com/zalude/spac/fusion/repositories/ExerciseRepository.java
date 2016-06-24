package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.Exercise;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository to access the {@link com.zalude.spac.fusion.models.domain.Exercise}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface ExerciseRepository extends PagingAndSortingRepository<Exercise, UUID> {

}
