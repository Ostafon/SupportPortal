package com.ostafon.supportportal.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(description = "User registration request")
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @JsonProperty("email")
    @Schema(description = "User email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @JsonProperty("firstName")
    @Schema(description = "User first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @JsonProperty("lastName")
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @JsonProperty("password")
    @Schema(description = "User password (min 6 characters)", example = "securePassword123")
    private String password;

    @JsonProperty("role")
    @Schema(description = "User role (defaults to USER)", example = "USER")
    private UserRole role;
}