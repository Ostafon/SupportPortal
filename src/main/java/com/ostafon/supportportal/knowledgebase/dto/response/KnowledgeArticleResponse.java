package com.ostafon.supportportal.knowledgebase.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for knowledge base article
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeArticleResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("categoryName")
    private String categoryName;

    @JsonProperty("tags")
    private Set<String> tags;

    @JsonProperty("status")
    private String status; // DRAFT, PUBLISHED, ARCHIVED

    @JsonProperty("viewCount")
    private Long viewCount;

    @JsonProperty("helpfulCount")
    private Long helpfulCount;

    @JsonProperty("notHelpfulCount")
    private Long notHelpfulCount;

    @JsonProperty("isFeatured")
    private Boolean isFeatured;

    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("authorName")
    private String authorName;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("publishedAt")
    private LocalDateTime publishedAt;
}

