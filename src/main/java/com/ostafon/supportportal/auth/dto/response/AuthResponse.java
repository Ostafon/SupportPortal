package com.ostafon.supportportal.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(description = "Authentication response containing user info and JWT token")
public class AuthResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @JsonProperty("email")
    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @JsonProperty("role")
    @Schema(description = "User role", example = "USER")
    private String role;

    @JsonProperty("accessToken")
    @Schema(description = "JWT access token")
    private String accessToken;

    @JsonProperty("tokenType")
    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";
}