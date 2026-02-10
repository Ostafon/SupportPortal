package com.ostafon.supportportal.knowledgebase.service;

import com.ostafon.supportportal.common.exception.ResourceNotFoundException;
import com.ostafon.supportportal.common.utils.SecurityUtils;
import com.ostafon.supportportal.knowledgebase.dto.request.CreateKnowledgeArticleRequest;
import com.ostafon.supportportal.knowledgebase.dto.request.CreateKnowledgeCategoryRequest;
import com.ostafon.supportportal.knowledgebase.dto.request.UpdateKnowledgeArticleRequest;
import com.ostafon.supportportal.knowledgebase.dto.response.KnowledgeArticleResponse;
import com.ostafon.supportportal.knowledgebase.dto.response.KnowledgeCategoryResponse;
import com.ostafon.supportportal.knowledgebase.mapper.KnowledgeBaseMapper;
import com.ostafon.supportportal.knowledgebase.model.KnowledgeArticleEntity;
import com.ostafon.supportportal.knowledgebase.model.KnowledgeCategoryEntity;
import com.ostafon.supportportal.knowledgebase.repo.KnowledgeArticleRepo;
import com.ostafon.supportportal.knowledgebase.repo.KnowledgeCategoryRepo;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Knowledge Base management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseService {

    private final KnowledgeArticleRepo articleRepo;
    private final KnowledgeCategoryRepo categoryRepo;
    private final UserRepo userRepo;

    // ==================== Article Operations ====================

    /**
     * Get all published articles
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleResponse> getAllArticles(Pageable pageable) {
        log.info("Fetching all published articles");

        return articleRepo.findAllPublished(pageable)
                .map(KnowledgeBaseMapper::articleToResponse);
    }

    /**
     * Get articles by category
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleResponse> getArticlesByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching articles for category: {}", categoryId);

        // Verify category exists
        categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        return articleRepo.findByCategory(categoryId, pageable)
                .map(KnowledgeBaseMapper::articleToResponse);
    }

    /**
     * Get article by ID (increments view count)
     */
    @Transactional
    public KnowledgeArticleResponse getArticleById(Long articleId) {
        log.info("Fetching article by ID: {}", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        // Increment view count
        article.setViewCount(article.getViewCount() + 1);
        articleRepo.save(article);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Search articles
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleResponse> searchArticles(String query, Pageable pageable) {
        log.info("Searching articles with query: {}", query);

        return articleRepo.searchArticles(query, pageable)
                .map(KnowledgeBaseMapper::articleToResponse);
    }

    /**
     * Get articles by tag
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleResponse> getArticlesByTag(String tag, Pageable pageable) {
        log.info("Fetching articles with tag: {}", tag);

        return articleRepo.findByTag(tag, pageable)
                .map(KnowledgeBaseMapper::articleToResponse);
    }

    /**
     * Get featured articles
     */
    @Transactional(readOnly = true)
    public List<KnowledgeArticleResponse> getFeaturedArticles() {
        log.info("Fetching featured articles");

        return articleRepo.findFeatured().stream()
                .map(KnowledgeBaseMapper::articleToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create article (ADMIN only)
     */
    @Transactional
    public KnowledgeArticleResponse createArticle(CreateKnowledgeArticleRequest request) {
        log.info("Creating new knowledge article: {}", request.getTitle());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity author = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        KnowledgeCategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        KnowledgeArticleEntity article = KnowledgeArticleEntity.builder()
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .category(category)
                .tags(request.getTags() != null ? request.getTags() : new java.util.HashSet<>())
                .status("DRAFT")
                .author(author)
                .viewCount(0L)
                .helpfulCount(0L)
                .notHelpfulCount(0L)
                .isFeatured(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        article = articleRepo.save(article);
        log.info("Article created with ID: {}", article.getId());

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Update article (ADMIN only)
     */
    @Transactional
    public KnowledgeArticleResponse updateArticle(Long articleId, UpdateKnowledgeArticleRequest request) {
        log.info("Updating article ID: {}", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            article.setTitle(request.getTitle().trim());
        }

        if (request.getContent() != null && !request.getContent().isBlank()) {
            article.setContent(request.getContent().trim());
        }

        if (request.getCategoryId() != null) {
            KnowledgeCategoryEntity category = categoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            article.setCategory(category);
        }

        if (request.getTags() != null) {
            article.setTags(request.getTags());
        }

        article.setUpdatedAt(LocalDateTime.now());
        article = articleRepo.save(article);

        log.info("Article {} updated successfully", articleId);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Publish article (ADMIN only)
     */
    @Transactional
    public KnowledgeArticleResponse publishArticle(Long articleId) {
        log.info("Publishing article ID: {}", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        article.setStatus("PUBLISHED");
        article.setPublishedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article = articleRepo.save(article);

        log.info("Article {} published successfully", articleId);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Archive article (ADMIN only)
     */
    @Transactional
    public KnowledgeArticleResponse archiveArticle(Long articleId) {
        log.info("Archiving article ID: {}", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        article.setStatus("ARCHIVED");
        article.setUpdatedAt(LocalDateTime.now());
        article = articleRepo.save(article);

        log.info("Article {} archived successfully", articleId);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Mark article as featured (ADMIN only)
     */
    @Transactional
    public KnowledgeArticleResponse featureArticle(Long articleId) {
        log.info("Featuring article ID: {}", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        article.setIsFeatured(true);
        article.setUpdatedAt(LocalDateTime.now());
        article = articleRepo.save(article);

        log.info("Article {} featured successfully", articleId);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Unfeature article (ADMIN only)
     */
    @Transactional
    public KnowledgeArticleResponse unfeatureArticle(Long articleId) {
        log.info("Unfeaturing article ID: {}", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        article.setIsFeatured(false);
        article.setUpdatedAt(LocalDateTime.now());
        article = articleRepo.save(article);

        log.info("Article {} unfeatured successfully", articleId);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Mark article as helpful
     */
    @Transactional
    public KnowledgeArticleResponse markHelpful(Long articleId) {
        log.info("Marking article {} as helpful", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        article.setHelpfulCount(article.getHelpfulCount() + 1);
        article = articleRepo.save(article);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Mark article as not helpful
     */
    @Transactional
    public KnowledgeArticleResponse markNotHelpful(Long articleId) {
        log.info("Marking article {} as not helpful", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        article.setNotHelpfulCount(article.getNotHelpfulCount() + 1);
        article = articleRepo.save(article);

        return KnowledgeBaseMapper.articleToResponse(article);
    }

    /**
     * Delete article (ADMIN only)
     */
    @Transactional
    public void deleteArticle(Long articleId) {
        log.info("Deleting article ID: {}", articleId);

        KnowledgeArticleEntity article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        articleRepo.delete(article);
        log.info("Article {} deleted successfully", articleId);
    }

    /**
     * Get draft articles (ADMIN only)
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleResponse> getDraftArticles(Pageable pageable) {
        log.info("Fetching draft articles");

        return articleRepo.findDrafts(pageable)
                .map(KnowledgeBaseMapper::articleToResponse);
    }

    // ==================== Category Operations ====================

    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<KnowledgeCategoryResponse> getAllCategories() {
        log.info("Fetching all categories");

        return categoryRepo.findAllOrdered().stream()
                .map(cat -> {
                    long articleCount = articleRepo.countByCategory_IdAndStatus(cat.getId(), "PUBLISHED");
                    return KnowledgeBaseMapper.categoryToResponse(cat, articleCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public KnowledgeCategoryResponse getCategoryById(Long categoryId) {
        log.info("Fetching category by ID: {}", categoryId);

        KnowledgeCategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        long articleCount = articleRepo.countByCategory_IdAndStatus(categoryId, "PUBLISHED");

        return KnowledgeBaseMapper.categoryToResponse(category, articleCount);
    }

    /**
     * Create category (ADMIN only)
     */
    @Transactional
    public KnowledgeCategoryResponse createCategory(CreateKnowledgeCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());

        if (categoryRepo.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        KnowledgeCategoryEntity category = KnowledgeCategoryEntity.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 999)
                .build();

        category = categoryRepo.save(category);
        log.info("Category created with ID: {}", category.getId());

        return KnowledgeBaseMapper.categoryToResponse(category, 0);
    }

    /**
     * Delete category (ADMIN only)
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category ID: {}", categoryId);

        KnowledgeCategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepo.delete(category);
        log.info("Category {} deleted successfully", categoryId);
    }
}

