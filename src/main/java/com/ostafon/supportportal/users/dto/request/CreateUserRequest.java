package com.ostafon.supportportal.users.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "Full name is required")
    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("role")
    private UserRole role = UserRole.USER;
}