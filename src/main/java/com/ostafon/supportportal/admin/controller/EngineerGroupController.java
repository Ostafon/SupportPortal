package com.ostafon.supportportal.admin.controller;

import com.ostafon.supportportal.admin.dto.CreateEngineerGroupRequest;
import com.ostafon.supportportal.admin.dto.EngineerGroupResponse;
import com.ostafon.supportportal.admin.service.EngineerGroupService;
import com.ostafon.supportportal.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for engineer group management
 */
@RestController
@RequestMapping("/api/admin/engineer-groups")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Engineer Groups", description = "Engineer group management endpoints (admin only)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class EngineerGroupController {

    private final EngineerGroupService groupService;

    /**
     * Create new engineer group
     */
    @PostMapping
    @Operation(
            summary = "Create engineer group",
            description = "Create a new engineer group with optional members. Admin only."
    )
    public ResponseEntity<ApiResponse<EngineerGroupResponse>> createGroup(
            @Valid @RequestBody CreateEngineerGroupRequest request) {

        log.info("REST: Create engineer group: {}", request.getName());

        EngineerGroupResponse group = groupService.createGroup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Engineer group created successfully", group));
    }

    /**
     * Get all engineer groups
     */
    @GetMapping
    @Operation(
            summary = "Get all engineer groups",
            description = "Retrieve list of all engineer groups with members. Admin only."
    )
    public ResponseEntity<ApiResponse<List<EngineerGroupResponse>>> getAllGroups() {
        log.info("REST: Get all engineer groups");

        List<EngineerGroupResponse> groups = groupService.getAllGroups();

        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    /**
     * Get group by ID
     */
    @GetMapping("/{groupId}")
    @Operation(
            summary = "Get engineer group by ID",
            description = "Retrieve specific engineer group with details. Admin only."
    )
    public ResponseEntity<ApiResponse<EngineerGroupResponse>> getGroupById(
            @PathVariable @Parameter(description = "Group ID") Long groupId) {

        log.info("REST: Get engineer group by ID: {}", groupId);

        EngineerGroupResponse group = groupService.getGroupById(groupId);

        return ResponseEntity.ok(ApiResponse.success(group));
    }

    /**
     * Update engineer group
     */
    @PutMapping("/{groupId}")
    @Operation(
            summary = "Update engineer group",
            description = "Update engineer group name, description, and members. Admin only."
    )
    public ResponseEntity<ApiResponse<EngineerGroupResponse>> updateGroup(
            @PathVariable @Parameter(description = "Group ID") Long groupId,
            @Valid @RequestBody CreateEngineerGroupRequest request) {

        log.info("REST: Update engineer group ID: {}", groupId);

        EngineerGroupResponse group = groupService.updateGroup(groupId, request);

        return ResponseEntity.ok(ApiResponse.success("Engineer group updated successfully", group));
    }

    /**
     * Add member to group
     */
    @PostMapping("/{groupId}/members/{userId}")
    @Operation(
            summary = "Add member to group",
            description = "Add an engineer or admin to the group. Admin only."
    )
    public ResponseEntity<ApiResponse<EngineerGroupResponse>> addMember(
            @PathVariable @Parameter(description = "Group ID") Long groupId,
            @PathVariable @Parameter(description = "User ID") Long userId) {

        log.info("REST: Add user {} to group {}", userId, groupId);

        EngineerGroupResponse group = groupService.addMember(groupId, userId);

        return ResponseEntity.ok(ApiResponse.success("Member added to group successfully", group));
    }

    /**
     * Remove member from group
     */
    @DeleteMapping("/{groupId}/members/{userId}")
    @Operation(
            summary = "Remove member from group",
            description = "Remove a member from the engineer group. Admin only."
    )
    public ResponseEntity<ApiResponse<EngineerGroupResponse>> removeMember(
            @PathVariable @Parameter(description = "Group ID") Long groupId,
            @PathVariable @Parameter(description = "User ID") Long userId) {

        log.info("REST: Remove user {} from group {}", userId, groupId);

        EngineerGroupResponse group = groupService.removeMember(groupId, userId);

        return ResponseEntity.ok(ApiResponse.success("Member removed from group successfully", group));
    }

    /**
     * Delete group
     */
    @DeleteMapping("/{groupId}")
    @Operation(
            summary = "Delete engineer group",
            description = "Delete an engineer group. Admin only."
    )
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable @Parameter(description = "Group ID") Long groupId) {

        log.info("REST: Delete engineer group ID: {}", groupId);

        groupService.deleteGroup(groupId);

        return ResponseEntity.ok(ApiResponse.success("Engineer group deleted successfully", null));
    }
}

