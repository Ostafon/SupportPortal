package com.ostafon.supportportal.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Request DTO for creating engineer group
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEngineerGroupRequest {

    @JsonProperty("name")
    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("memberIds")
    private Set<Long> memberIds;
}

