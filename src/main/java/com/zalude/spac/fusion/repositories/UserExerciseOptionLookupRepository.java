package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.UserExerciseOptionLookup;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository to access the {@link com.zalude.spac.fusion.models.domain.UserExerciseOptionLookup}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface UserExerciseOptionLookupRepository extends PagingAndSortingRepository<UserExerciseOptionLookup, UUID> {

}
