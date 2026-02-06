package com.ostafon.supportportal.users.service;

import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.common.exception.ResourceNotFoundException;
import com.ostafon.supportportal.common.utils.SecurityUtils;
import com.ostafon.supportportal.users.dto.request.AdminUpdateUserRequest;
import com.ostafon.supportportal.users.dto.request.ChangePasswordRequest;
import com.ostafon.supportportal.users.dto.request.UpdateUserRequest;
import com.ostafon.supportportal.users.dto.response.UserResponse;
import com.ostafon.supportportal.users.mapper.UserMapper;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for user management operations
 * Handles CRUD operations and role/status management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get current authenticated user profile
     * @return user profile DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        log.debug("Fetching profile for user ID: {}", currentUserId);
        UserEntity user = getUserEntityById(currentUserId);
        return UserMapper.toResponse(user);
    }

    /**
     * Get user by ID
     * Only accessible by admin or the user themselves
     * @param userId user ID
     * @return user profile DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.debug("Fetching user by ID: {}", userId);

        // Check permissions: admin or self
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        if (!isAdmin && !userId.equals(currentUserId)) {
            log.warn("Access denied: User {} tried to access user {}", currentUserId, userId);
            throw new AccessDeniedException("You can only view your own profile");
        }

        UserEntity user = getUserEntityById(userId);
        return UserMapper.toResponse(user);
    }

    /**
     * Get all users (admin only)
     * @return list of all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users (admin operation)");

        List<UserEntity> users = userRepo.findAll();

        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all users with pagination (admin only)
     * @param pageable pagination parameters
     * @return page of users
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersPaginated(Pageable pageable) {
        log.info("Fetching users with pagination: page {}, size {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<UserEntity> users = userRepo.findAll(pageable);

        return users.map(UserMapper::toResponse);
    }

    /**
     * Get users by role (admin only)
     * @param role user role
     * @return list of users with specified role
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        log.info("Fetching users by role: {}", role);

        List<UserEntity> users = userRepo.findByRoleAndActive(role.name());

        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update current user profile
     * @param request update request
     * @return updated user profile
     */
    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        log.info("Updating profile for user ID: {}", currentUserId);

        UserEntity user = getUserEntityById(currentUserId);

        // Update fields
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());

        user = userRepo.save(user);
        log.info("User profile updated successfully for ID: {}", currentUserId);

        return UserMapper.toResponse(user);
    }

    /**
     * Admin updates user role and status
     * @param userId user ID to update
     * @param request admin update request
     * @return updated user profile
     */
    @Transactional
    public UserResponse adminUpdateUser(Long userId, AdminUpdateUserRequest request) {
        log.info("Admin updating user {}: role={}, isActive={}",
                userId, request.getRole(), request.getIsActive());

        UserEntity user = getUserEntityById(userId);

        // Prevent admin from deactivating themselves
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (userId.equals(currentUserId) && Boolean.FALSE.equals(request.getIsActive())) {
            throw new IllegalArgumentException("Cannot deactivate your own account");
        }

        // Update role and status
        user.setRole(request.getRole());
        user.setIsActive(request.getIsActive());

        user = userRepo.save(user);
        log.info("User {} updated by admin: role={}, isActive={}",
                userId, user.getRole(), user.getIsActive());

        return UserMapper.toResponse(user);
    }

    /**
     * Change current user password
     * @param request change password request
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        log.info("Password change requested for user ID: {}", currentUserId);

        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        UserEntity user = getUserEntityById(currentUserId);

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Password change failed: incorrect current password for user {}", currentUserId);
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        log.info("Password changed successfully for user ID: {}", currentUserId);
    }

    /**
     * Deactivate user (admin only)
     * @param userId user ID to deactivate
     */
    @Transactional
    public void deactivateUser(Long userId) {
        log.info("Admin deactivating user ID: {}", userId);

        // Prevent admin from deactivating themselves
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("Cannot deactivate your own account");
        }

        UserEntity user = getUserEntityById(userId);
        user.setIsActive(false);
        userRepo.save(user);

        log.info("User {} deactivated successfully", userId);
    }

    /**
     * Activate user (admin only)
     * @param userId user ID to activate
     */
    @Transactional
    public void activateUser(Long userId) {
        log.info("Admin activating user ID: {}", userId);

        UserEntity user = getUserEntityById(userId);
        user.setIsActive(true);
        userRepo.save(user);

        log.info("User {} activated successfully", userId);
    }

    // ==================== Helper Methods ====================

    /**
     * Get user entity by ID or throw exception
     * @param userId user ID
     * @return user entity
     * @throws ResourceNotFoundException if user not found
     */
    private UserEntity getUserEntityById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}

