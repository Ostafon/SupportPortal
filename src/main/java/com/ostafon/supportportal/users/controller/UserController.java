package com.ostafon.supportportal.users.controller;

import com.ostafon.supportportal.common.dto.ApiResponse;
import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.users.dto.request.AdminUpdateUserRequest;
import com.ostafon.supportportal.users.dto.request.ChangePasswordRequest;
import com.ostafon.supportportal.users.dto.request.UpdateUserRequest;
import com.ostafon.supportportal.users.dto.response.UserResponse;
import com.ostafon.supportportal.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for user management
 * Provides endpoints for user profile, admin operations, and password management
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User profile and administration endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    // ==================== User Profile Endpoints ====================

    /**
     * Get current authenticated user profile
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Returns profile of currently authenticated user"
    )
    public ResponseEntity<UserResponse> getCurrentUser() {
        log.debug("GET /api/users/me - Get current user profile");
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/me")
    @Operation(
            summary = "Update current user profile",
            description = "Update first name and last name of current user"
    )
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("PUT /api/users/me - Update current user profile");
        UserResponse user = userService.updateCurrentUser(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
    }

    /**
     * Change current user password
     */
    @PutMapping("/me/password")
    @Operation(
            summary = "Change password",
            description = "Change password for current user (requires current password)"
    )
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("PUT /api/users/me/password - Change password");
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    // ==================== Admin & Agent Endpoints ====================

    /**
     * Get user by ID (Admin or self)
     */
    @GetMapping("/{userId}")
    @Operation(
            summary = "Get user by ID",
            description = "Get user profile by ID (admin can view any user, users can view only themselves)"
    )
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId
    ) {
        log.debug("GET /api/users/{} - Get user by ID", userId);
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Get list of all users (admin only)"
    )
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/users - Get all users (admin)");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get users with pagination (Admin only)
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Get users paginated",
            description = "Get paginated list of users (admin only)"
    )
    public ResponseEntity<Page<UserResponse>> getAllUsersPaginated(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort by field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction (ASC or DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        log.info("GET /api/users/paginated - page={}, size={}", page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponse> users = userService.getAllUsersPaginated(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role (Admin only)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Get users by role",
            description = "Get all users with specified role (admin only)"
    )
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @Parameter(description = "User role", example = "AGENT")
            @PathVariable UserRole role
    ) {
        log.info("GET /api/users/role/{} - Get users by role", role);
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Admin update user role and status
     */
    @PutMapping("/{userId}/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Admin update user",
            description = "Update user role and active status (admin only)"
    )
    public ResponseEntity<ApiResponse<UserResponse>> adminUpdateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,

            @Valid @RequestBody AdminUpdateUserRequest request
    ) {
        log.info("PUT /api/users/{}/admin - Admin update user", userId);
        UserResponse user = userService.adminUpdateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }

    /**
     * Deactivate user (Admin only)
     */
    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Deactivate user",
            description = "Deactivate user account (admin only)"
    )
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId
    ) {
        log.info("POST /api/users/{}/deactivate - Deactivate user", userId);
        userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }

    /**
     * Activate user (Admin only)
     */
    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Activate user",
            description = "Activate user account (admin only)"
    )
    public ResponseEntity<ApiResponse<Void>> activateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId
    ) {
        log.info("POST /api/users/{}/activate - Activate user", userId);
        userService.activateUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", null));
    }
}

