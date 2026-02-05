package com.ostafon.supportportal.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.ostafon.supportportal.common.enums.UserRole;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "First name required")
    @Size(min = 3, max = 50)
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "Last name required")
    @Size(min = 3, max = 50)
    @JsonProperty("lastName")
    private String lastName;


    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 255, message = "Password must be between 6 and 255 characters")
    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private UserRole role = UserRole.USER;
}