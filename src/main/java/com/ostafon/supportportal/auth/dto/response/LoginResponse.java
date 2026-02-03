package com.ostafon.supportportal.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponse {

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("tokenType")
    private String tokenType = "Bearer";
}