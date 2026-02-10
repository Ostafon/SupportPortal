package com.ostafon.supportportal.knowledgebase.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Request DTO for creating knowledge base article
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateKnowledgeArticleRequest {

    @JsonProperty("title")
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "Content is required")
    @Size(min = 20, message = "Content must be at least 20 characters")
    private String content;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("tags")
    private Set<String> tags;
}

