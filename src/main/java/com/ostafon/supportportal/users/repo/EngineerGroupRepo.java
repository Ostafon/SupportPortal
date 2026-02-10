package com.ostafon.supportportal.users.repo;

import com.ostafon.supportportal.users.model.EngineerGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Engineer Group entities
 */
@Repository
public interface EngineerGroupRepo extends JpaRepository<EngineerGroupEntity, Long> {

    /**
     * Find group by name
     * @param name group name
     * @return optional group entity
     */
    Optional<EngineerGroupEntity> findByName(String name);

    /**
     * Check if group exists by name
     * @param name group name
     * @return true if exists
     */
    boolean existsByName(String name);
}

