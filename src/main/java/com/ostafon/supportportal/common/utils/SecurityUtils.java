package com.ostafon.supportportal.common.utils;

import com.ostafon.supportportal.common.security.CustomUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for accessing current authenticated user information
 */
@UtilityClass
public class SecurityUtils {

    /**
     * Get current authenticated user ID
     * @return user ID or null if not authenticated
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        return null;
    }

    /**
     * Get current authenticated user email
     * @return user email or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername();
        }

        return null;
    }

    /**
     * Get current authenticated user role
     * @return user role or null if not authenticated
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getRole();
        }

        return null;
    }

    /**
     * Check if current user has specific role
     * @param role role to check
     * @return true if user has the role
     */
    public static boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equalsIgnoreCase(role);
    }

    /**
     * Check if current user has any of the specified roles
     * @param roles roles to check
     * @return true if user has any of the roles
     */
    public static boolean hasAnyRole(String... roles) {
        String currentRole = getCurrentUserRole();
        if (currentRole == null) {
            return false;
        }
        for (String role : roles) {
            if (currentRole.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user is authenticated
     * @return true if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }
}

