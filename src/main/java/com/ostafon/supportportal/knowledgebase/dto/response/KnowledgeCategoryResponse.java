package com.ostafon.supportportal.knowledgebase.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Response DTO for knowledge base category
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeCategoryResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("displayOrder")
    private Integer displayOrder;

    @JsonProperty("articleCount")
    private Long articleCount;
}

