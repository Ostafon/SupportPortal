package com.ostafon.supportportal.knowledgebase.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CreateKnowledgeBaseRequest {

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("category")
    private String category;

    @JsonProperty("createdBy")
    private Long createdBy;
}