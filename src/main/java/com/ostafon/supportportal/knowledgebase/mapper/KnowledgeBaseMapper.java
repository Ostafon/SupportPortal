package com.ostafon.supportportal.knowledgebase.mapper;

import com.ostafon.supportportal.knowledgebase.dto.response.KnowledgeArticleResponse;
import com.ostafon.supportportal.knowledgebase.dto.response.KnowledgeCategoryResponse;
import com.ostafon.supportportal.knowledgebase.model.KnowledgeArticleEntity;
import com.ostafon.supportportal.knowledgebase.model.KnowledgeCategoryEntity;
import lombok.experimental.UtilityClass;

/**
 * Mapper for Knowledge Base entities and DTOs
 */
@UtilityClass
public class KnowledgeBaseMapper {

    /**
     * Convert article entity to response DTO
     */
    public static KnowledgeArticleResponse articleToResponse(KnowledgeArticleEntity article) {
        if (article == null) {
            return null;
        }

        return KnowledgeArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .categoryId(article.getCategory() != null ? article.getCategory().getId() : null)
                .categoryName(article.getCategory() != null ? article.getCategory().getName() : null)
                .tags(article.getTags())
                .status(article.getStatus())
                .viewCount(article.getViewCount())
                .helpfulCount(article.getHelpfulCount())
                .notHelpfulCount(article.getNotHelpfulCount())
                .isFeatured(article.getIsFeatured())
                .authorId(article.getAuthor() != null ? article.getAuthor().getId() : null)
                .authorName(article.getAuthor() != null ? article.getAuthor().getFirstName() + " " + article.getAuthor().getLastName() : null)
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .publishedAt(article.getPublishedAt())
                .build();
    }

    /**
     * Convert category entity to response DTO
     */
    public static KnowledgeCategoryResponse categoryToResponse(KnowledgeCategoryEntity category, long articleCount) {
        if (category == null) {
            return null;
        }

        return KnowledgeCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .articleCount(articleCount)
                .build();
    }
}

