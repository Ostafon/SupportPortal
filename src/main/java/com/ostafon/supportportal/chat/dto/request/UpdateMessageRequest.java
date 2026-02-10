package com.ostafon.supportportal.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMessageRequest {

    @JsonProperty("message")
    @NotBlank(message = "message is required")
    @Size(min = 1, max = 2000, message = "message must be between 1 and 2000 characters")
    private String message;
}

