package com.ostafon.supportportal.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

/**
 * Response DTO for engineer group
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineerGroupResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("memberCount")
    private Integer memberCount;

    @JsonProperty("memberIds")
    private Set<Long> memberIds;

    @JsonProperty("memberNames")
    private Set<String> memberNames;
}

