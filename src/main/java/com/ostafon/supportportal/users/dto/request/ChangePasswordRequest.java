package com.ostafon.supportportal.users.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for changing user password
 * Requires old password for security
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(description = "Change password request")
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @JsonProperty("currentPassword")
    @Schema(description = "Current password", example = "oldPassword123")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @JsonProperty("newPassword")
    @Schema(description = "New password (min 6 characters)", example = "newPassword123")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @JsonProperty("confirmPassword")
    @Schema(description = "Confirm new password", example = "newPassword123")
    private String confirmPassword;
}

