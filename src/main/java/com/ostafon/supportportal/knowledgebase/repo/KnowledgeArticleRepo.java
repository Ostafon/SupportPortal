package com.ostafon.supportportal.knowledgebase.repo;

import com.ostafon.supportportal.knowledgebase.model.KnowledgeArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Knowledge Base articles
 */
@Repository
public interface KnowledgeArticleRepo extends JpaRepository<KnowledgeArticleEntity, Long> {

    /**
     * Find all published articles with pagination
     */
    @Query("SELECT a FROM KnowledgeArticleEntity a WHERE a.status = 'PUBLISHED' ORDER BY a.isFeatured DESC, a.createdAt DESC")
    Page<KnowledgeArticleEntity> findAllPublished(Pageable pageable);

    /**
     * Find articles by category
     */
    @Query("SELECT a FROM KnowledgeArticleEntity a WHERE a.category.id = :categoryId AND a.status = 'PUBLISHED' ORDER BY a.createdAt DESC")
    Page<KnowledgeArticleEntity> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Search articles by title or content
     */
    @Query("SELECT a FROM KnowledgeArticleEntity a WHERE a.status = 'PUBLISHED' AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY a.createdAt DESC")
    Page<KnowledgeArticleEntity> searchArticles(@Param("query") String query, Pageable pageable);

    /**
     * Find articles by tag
     */
    @Query(value = "SELECT DISTINCT a.* FROM knowledge_articles a JOIN article_tags at ON a.id = at.article_id WHERE at.tag = :tag AND a.status = 'PUBLISHED' ORDER BY a.created_at DESC", nativeQuery = true)
    Page<KnowledgeArticleEntity> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * Find featured articles
     */
    @Query("SELECT a FROM KnowledgeArticleEntity a WHERE a.isFeatured = true AND a.status = 'PUBLISHED' ORDER BY a.createdAt DESC")
    List<KnowledgeArticleEntity> findFeatured();

    /**
     * Find draft articles for admin
     */
    @Query("SELECT a FROM KnowledgeArticleEntity a WHERE a.status = 'DRAFT' ORDER BY a.createdAt DESC")
    Page<KnowledgeArticleEntity> findDrafts(Pageable pageable);

    /**
     * Find articles by author
     */
    @Query("SELECT a FROM KnowledgeArticleEntity a WHERE a.author.id = :authorId ORDER BY a.createdAt DESC")
    Page<KnowledgeArticleEntity> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);

    /**
     * Count articles by category
     */
    long countByCategory_IdAndStatus(Long categoryId, String status);
}

