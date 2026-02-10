package com.ostafon.supportportal.knowledgebase.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO for creating knowledge base category
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateKnowledgeCategoryRequest {

    @JsonProperty("name")
    @NotBlank(message = "Category name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("displayOrder")
    private Integer displayOrder;
}

