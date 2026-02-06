package com.ostafon.supportportal.users.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for admin to update user role and status
 * Only accessible by ADMIN role
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(description = "Admin update user request (role and status)")
public class AdminUpdateUserRequest {

    @NotNull(message = "Role is required")
    @JsonProperty("role")
    @Schema(description = "User role", example = "AGENT")
    private UserRole role;

    @NotNull(message = "Active status is required")
    @JsonProperty("isActive")
    @Schema(description = "Account active status", example = "true")
    private Boolean isActive;
}

