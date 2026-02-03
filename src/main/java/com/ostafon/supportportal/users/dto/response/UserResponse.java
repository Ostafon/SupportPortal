package com.ostafon.supportportal.users.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.UserRole;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("role")
    private UserRole role;

    @JsonProperty("createdAt")
    private String createdAt;  // Можно сериализовать как строку
}