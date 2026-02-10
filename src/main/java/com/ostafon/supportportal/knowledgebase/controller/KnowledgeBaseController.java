package com.ostafon.supportportal.knowledgebase.controller;

import com.ostafon.supportportal.common.dto.ApiResponse;
import com.ostafon.supportportal.knowledgebase.dto.request.CreateKnowledgeArticleRequest;
import com.ostafon.supportportal.knowledgebase.dto.request.CreateKnowledgeCategoryRequest;
import com.ostafon.supportportal.knowledgebase.dto.request.UpdateKnowledgeArticleRequest;
import com.ostafon.supportportal.knowledgebase.dto.response.KnowledgeArticleResponse;
import com.ostafon.supportportal.knowledgebase.dto.response.KnowledgeCategoryResponse;
import com.ostafon.supportportal.knowledgebase.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Knowledge Base
 * Provides FAQ and knowledge articles for users
 */
@RestController
@RequestMapping("/api/kb")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Knowledge Base", description = "FAQ and knowledge base articles")
@SecurityRequirement(name = "Bearer Authentication")
public class KnowledgeBaseController {

    private final KnowledgeBaseService kbService;

    // ==================== Article Endpoints ====================

    /**
     * Get all published articles
     */
    @GetMapping("/articles")
    @Operation(
            summary = "Get all articles",
            description = "Retrieve all published knowledge base articles with pagination"
    )
    public ResponseEntity<ApiResponse<Page<KnowledgeArticleResponse>>> getAllArticles(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size) {

        log.info("REST: Get all articles - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<KnowledgeArticleResponse> articles = kbService.getAllArticles(pageable);

        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * Get articles by category
     */
    @GetMapping("/categories/{categoryId}/articles")
    @Operation(
            summary = "Get articles by category",
            description = "Retrieve all published articles from a specific category"
    )
    public ResponseEntity<ApiResponse<Page<KnowledgeArticleResponse>>> getArticlesByCategory(
            @PathVariable @Parameter(description = "Category ID") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("REST: Get articles for category: {}", categoryId);

        Pageable pageable = PageRequest.of(page, size);
        Page<KnowledgeArticleResponse> articles = kbService.getArticlesByCategory(categoryId, pageable);

        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * Get article by ID
     */
    @GetMapping("/articles/{articleId}")
    @Operation(
            summary = "Get article by ID",
            description = "Retrieve a specific article and increment view count"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> getArticleById(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Get article by ID: {}", articleId);

        KnowledgeArticleResponse article = kbService.getArticleById(articleId);

        return ResponseEntity.ok(ApiResponse.success(article));
    }

    /**
     * Search articles
     */
    @GetMapping("/articles/search")
    @Operation(
            summary = "Search articles",
            description = "Search knowledge base articles by title or content"
    )
    public ResponseEntity<ApiResponse<Page<KnowledgeArticleResponse>>> searchArticles(
            @RequestParam @Parameter(description = "Search query") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("REST: Search articles with query: {}", q);

        Pageable pageable = PageRequest.of(page, size);
        Page<KnowledgeArticleResponse> articles = kbService.searchArticles(q, pageable);

        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * Get articles by tag
     */
    @GetMapping("/articles/tag/{tag}")
    @Operation(
            summary = "Get articles by tag",
            description = "Retrieve all articles with a specific tag"
    )
    public ResponseEntity<ApiResponse<Page<KnowledgeArticleResponse>>> getArticlesByTag(
            @PathVariable @Parameter(description = "Tag name") String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("REST: Get articles with tag: {}", tag);

        Pageable pageable = PageRequest.of(page, size);
        Page<KnowledgeArticleResponse> articles = kbService.getArticlesByTag(tag, pageable);

        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * Get featured articles
     */
    @GetMapping("/articles/featured")
    @Operation(
            summary = "Get featured articles",
            description = "Retrieve featured knowledge base articles"
    )
    public ResponseEntity<ApiResponse<List<KnowledgeArticleResponse>>> getFeaturedArticles() {

        log.info("REST: Get featured articles");

        List<KnowledgeArticleResponse> articles = kbService.getFeaturedArticles();

        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * Create article (ADMIN only)
     */
    @PostMapping("/articles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create article",
            description = "Create a new knowledge base article (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> createArticle(
            @Valid @RequestBody CreateKnowledgeArticleRequest request) {

        log.info("REST: Create article: {}", request.getTitle());

        KnowledgeArticleResponse article = kbService.createArticle(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Article created successfully", article));
    }

    /**
     * Update article (ADMIN only)
     */
    @PutMapping("/articles/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update article",
            description = "Update knowledge base article (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> updateArticle(
            @PathVariable @Parameter(description = "Article ID") Long articleId,
            @Valid @RequestBody UpdateKnowledgeArticleRequest request) {

        log.info("REST: Update article ID: {}", articleId);

        KnowledgeArticleResponse article = kbService.updateArticle(articleId, request);

        return ResponseEntity.ok(ApiResponse.success("Article updated successfully", article));
    }

    /**
     * Publish article (ADMIN only)
     */
    @PutMapping("/articles/{articleId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Publish article",
            description = "Publish a draft article (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> publishArticle(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Publish article ID: {}", articleId);

        KnowledgeArticleResponse article = kbService.publishArticle(articleId);

        return ResponseEntity.ok(ApiResponse.success("Article published successfully", article));
    }

    /**
     * Archive article (ADMIN only)
     */
    @PutMapping("/articles/{articleId}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Archive article",
            description = "Archive a knowledge base article (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> archiveArticle(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Archive article ID: {}", articleId);

        KnowledgeArticleResponse article = kbService.archiveArticle(articleId);

        return ResponseEntity.ok(ApiResponse.success("Article archived successfully", article));
    }

    /**
     * Feature article (ADMIN only)
     */
    @PutMapping("/articles/{articleId}/feature")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Feature article",
            description = "Mark article as featured (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> featureArticle(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Feature article ID: {}", articleId);

        KnowledgeArticleResponse article = kbService.featureArticle(articleId);

        return ResponseEntity.ok(ApiResponse.success("Article featured successfully", article));
    }

    /**
     * Unfeature article (ADMIN only)
     */
    @PutMapping("/articles/{articleId}/unfeature")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Unfeature article",
            description = "Remove article from featured (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> unfeatureArticle(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Unfeature article ID: {}", articleId);

        KnowledgeArticleResponse article = kbService.unfeatureArticle(articleId);

        return ResponseEntity.ok(ApiResponse.success("Article unfeatured successfully", article));
    }

    /**
     * Mark article as helpful
     */
    @PostMapping("/articles/{articleId}/helpful")
    @Operation(
            summary = "Mark as helpful",
            description = "Mark an article as helpful"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> markHelpful(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Mark article {} as helpful", articleId);

        KnowledgeArticleResponse article = kbService.markHelpful(articleId);

        return ResponseEntity.ok(ApiResponse.success("Thank you for feedback", article));
    }

    /**
     * Mark article as not helpful
     */
    @PostMapping("/articles/{articleId}/not-helpful")
    @Operation(
            summary = "Mark as not helpful",
            description = "Mark an article as not helpful"
    )
    public ResponseEntity<ApiResponse<KnowledgeArticleResponse>> markNotHelpful(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Mark article {} as not helpful", articleId);

        KnowledgeArticleResponse article = kbService.markNotHelpful(articleId);

        return ResponseEntity.ok(ApiResponse.success("Thank you for feedback", article));
    }

    /**
     * Get draft articles (ADMIN only)
     */
    @GetMapping("/articles/draft")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get draft articles",
            description = "Retrieve draft articles for review (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<Page<KnowledgeArticleResponse>>> getDraftArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("REST: Get draft articles");

        Pageable pageable = PageRequest.of(page, size);
        Page<KnowledgeArticleResponse> articles = kbService.getDraftArticles(pageable);

        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * Delete article (ADMIN only)
     */
    @DeleteMapping("/articles/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete article",
            description = "Delete a knowledge base article permanently (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @PathVariable @Parameter(description = "Article ID") Long articleId) {

        log.info("REST: Delete article ID: {}", articleId);

        kbService.deleteArticle(articleId);

        return ResponseEntity.ok(ApiResponse.success("Article deleted successfully", null));
    }

    // ==================== Category Endpoints ====================

    /**
     * Get all categories
     */
    @GetMapping("/categories")
    @Operation(
            summary = "Get all categories",
            description = "Retrieve all knowledge base categories with article counts"
    )
    public ResponseEntity<ApiResponse<List<KnowledgeCategoryResponse>>> getAllCategories() {

        log.info("REST: Get all categories");

        List<KnowledgeCategoryResponse> categories = kbService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * Get category by ID
     */
    @GetMapping("/categories/{categoryId}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieve a specific category with article count"
    )
    public ResponseEntity<ApiResponse<KnowledgeCategoryResponse>> getCategoryById(
            @PathVariable @Parameter(description = "Category ID") Long categoryId) {

        log.info("REST: Get category by ID: {}", categoryId);

        KnowledgeCategoryResponse category = kbService.getCategoryById(categoryId);

        return ResponseEntity.ok(ApiResponse.success(category));
    }

    /**
     * Create category (ADMIN only)
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create category",
            description = "Create a new knowledge base category (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<KnowledgeCategoryResponse>> createCategory(
            @Valid @RequestBody CreateKnowledgeCategoryRequest request) {

        log.info("REST: Create category: {}", request.getName());

        KnowledgeCategoryResponse category = kbService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    /**
     * Delete category (ADMIN only)
     */
    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete category",
            description = "Delete a knowledge base category (ADMIN only)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable @Parameter(description = "Category ID") Long categoryId) {

        log.info("REST: Delete category ID: {}", categoryId);

        kbService.deleteCategory(categoryId);

        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}

