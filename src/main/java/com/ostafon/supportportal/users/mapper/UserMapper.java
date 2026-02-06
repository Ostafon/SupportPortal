package com.ostafon.supportportal.users.mapper;

import com.ostafon.supportportal.users.dto.response.UserResponse;
import com.ostafon.supportportal.users.model.UserEntity;
import lombok.experimental.UtilityClass;

/**
 * Mapper for converting UserEntity to UserResponse DTO
 * Never expose entity directly in API responses
 */
@UtilityClass
public class UserMapper {

    /**
     * Convert UserEntity to UserResponse DTO
     * @param user entity from database
     * @return DTO for API response
     */
    public static UserResponse toResponse(UserEntity user) {
        if (user == null) {
            return null;
        }

        String fullName = user.getFirstName() + " " + user.getLastName();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(fullName)
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

