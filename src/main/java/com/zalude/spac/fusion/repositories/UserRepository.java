package com.zalude.spac.fusion.repositories;

import com.zalude.spac.fusion.models.domain.FusionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository to access the {@link FusionUser}.
 *
 * @author Andrew Zurn (azurn)
 */
@Repository
public interface UserRepository extends JpaRepository<FusionUser, UUID> {

  FusionUser findByAuth0Id(String auth0Id);
}
