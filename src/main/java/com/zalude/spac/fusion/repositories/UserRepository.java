package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.FusionUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository to access the {@link FusionUser}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<FusionUser, UUID> {

}
