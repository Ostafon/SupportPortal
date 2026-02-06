package com.ostafon.supportportal.users.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for user profile response
 * Never expose password or sensitive data
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(description = "User profile information")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @JsonProperty("email")
    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @JsonProperty("firstName")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @JsonProperty("lastName")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @JsonProperty("fullName")
    @Schema(description = "Full name", example = "John Doe")
    private String fullName;

    @JsonProperty("role")
    @Schema(description = "User role", example = "USER")
    private UserRole role;

    @JsonProperty("isActive")
    @Schema(description = "Account active status", example = "true")
    private Boolean isActive;

    @JsonProperty("createdAt")
    @Schema(description = "Account creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "Last update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}

