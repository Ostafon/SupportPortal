package com.ostafon.supportportal.admin.service;

import com.ostafon.supportportal.admin.dto.CreateEngineerGroupRequest;
import com.ostafon.supportportal.admin.dto.EngineerGroupResponse;
import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.common.exception.ResourceNotFoundException;
import com.ostafon.supportportal.users.model.EngineerGroupEntity;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.EngineerGroupRepo;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing engineer groups
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EngineerGroupService {

    private final EngineerGroupRepo groupRepo;
    private final UserRepo userRepo;

    /**
     * Create new engineer group
     * @param request create request
     * @return created group
     */
    @Transactional
    public EngineerGroupResponse createGroup(CreateEngineerGroupRequest request) {
        log.info("Creating new engineer group: {}", request.getName());

        // Check if group with same name exists
        if (groupRepo.existsByName(request.getName())) {
            throw new IllegalArgumentException("Group with name '" + request.getName() + "' already exists");
        }

        Set<UserEntity> members = new HashSet<>();
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            members = request.getMemberIds().stream()
                    .map(id -> {
                        UserEntity user = userRepo.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

                        // Verify user is engineer or admin
                        if (user.getRole() != UserRole.ENGINEER && user.getRole() != UserRole.ADMIN) {
                            throw new IllegalArgumentException("Only engineers and admins can be added to groups");
                        }

                        return user;
                    })
                    .collect(Collectors.toSet());
        }

        EngineerGroupEntity group = EngineerGroupEntity.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .members(members)
                .build();

        group = groupRepo.save(group);
        log.info("Engineer group created with ID: {}", group.getId());

        return toResponse(group);
    }

    /**
     * Get all engineer groups
     * @return list of groups
     */
    @Transactional(readOnly = true)
    public List<EngineerGroupResponse> getAllGroups() {
        log.info("Fetching all engineer groups");

        return groupRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get group by ID
     * @param groupId group ID
     * @return group response
     */
    @Transactional(readOnly = true)
    public EngineerGroupResponse getGroupById(Long groupId) {
        log.info("Fetching group by ID: {}", groupId);

        EngineerGroupEntity group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Engineer Group", "id", groupId));

        return toResponse(group);
    }

    /**
     * Update engineer group
     * @param groupId group ID
     * @param request update request
     * @return updated group
     */
    @Transactional
    public EngineerGroupResponse updateGroup(Long groupId, CreateEngineerGroupRequest request) {
        log.info("Updating engineer group ID: {}", groupId);

        EngineerGroupEntity group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Engineer Group", "id", groupId));

        // Check if new name conflicts with another group
        if (!group.getName().equals(request.getName()) && groupRepo.existsByName(request.getName())) {
            throw new IllegalArgumentException("Group with name '" + request.getName() + "' already exists");
        }

        group.setName(request.getName().trim());
        group.setDescription(request.getDescription());

        if (request.getMemberIds() != null) {
            Set<UserEntity> members = request.getMemberIds().stream()
                    .map(id -> {
                        UserEntity user = userRepo.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

                        if (user.getRole() != UserRole.ENGINEER && user.getRole() != UserRole.ADMIN) {
                            throw new IllegalArgumentException("Only engineers and admins can be added to groups");
                        }

                        return user;
                    })
                    .collect(Collectors.toSet());

            group.setMembers(members);
        }

        group = groupRepo.save(group);
        log.info("Engineer group {} updated successfully", groupId);

        return toResponse(group);
    }

    /**
     * Add member to group
     * @param groupId group ID
     * @param userId user ID
     * @return updated group
     */
    @Transactional
    public EngineerGroupResponse addMember(Long groupId, Long userId) {
        log.info("Adding user {} to group {}", userId, groupId);

        EngineerGroupEntity group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Engineer Group", "id", groupId));

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getRole() != UserRole.ENGINEER && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only engineers and admins can be added to groups");
        }

        group.getMembers().add(user);
        group = groupRepo.save(group);

        log.info("User {} added to group {}", userId, groupId);

        return toResponse(group);
    }

    /**
     * Remove member from group
     * @param groupId group ID
     * @param userId user ID
     * @return updated group
     */
    @Transactional
    public EngineerGroupResponse removeMember(Long groupId, Long userId) {
        log.info("Removing user {} from group {}", userId, groupId);

        EngineerGroupEntity group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Engineer Group", "id", groupId));

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        group.getMembers().remove(user);
        group = groupRepo.save(group);

        log.info("User {} removed from group {}", userId, groupId);

        return toResponse(group);
    }

    /**
     * Delete group
     * @param groupId group ID
     */
    @Transactional
    public void deleteGroup(Long groupId) {
        log.info("Deleting engineer group ID: {}", groupId);

        EngineerGroupEntity group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Engineer Group", "id", groupId));

        groupRepo.delete(group);
        log.info("Engineer group {} deleted successfully", groupId);
    }

    /**
     * Convert entity to response DTO
     * @param group group entity
     * @return group response
     */
    private EngineerGroupResponse toResponse(EngineerGroupEntity group) {
        Set<Long> memberIds = group.getMembers().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());

        Set<String> memberNames = group.getMembers().stream()
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .collect(Collectors.toSet());

        return EngineerGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .memberCount(group.getMembers().size())
                .memberIds(memberIds)
                .memberNames(memberNames)
                .build();
    }
}

