package com.ostafon.supportportal.users.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Full name is required")
    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("role")
    private UserRole role;
}