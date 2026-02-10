package com.ostafon.supportportal.knowledgebase.repo;

import com.ostafon.supportportal.knowledgebase.model.KnowledgeCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Knowledge Base categories
 */
@Repository
public interface KnowledgeCategoryRepo extends JpaRepository<KnowledgeCategoryEntity, Long> {

    /**
     * Find category by name
     */
    Optional<KnowledgeCategoryEntity> findByName(String name);

    /**
     * Check if category exists by name
     */
    boolean existsByName(String name);

    /**
     * Get all categories ordered by display order
     */
    @Query("SELECT c FROM KnowledgeCategoryEntity c ORDER BY c.displayOrder ASC, c.id ASC")
    List<KnowledgeCategoryEntity> findAllOrdered();
}

