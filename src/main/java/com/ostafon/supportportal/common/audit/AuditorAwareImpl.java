package com.ostafon.supportportal.common.audit;

import com.ostafon.supportportal.common.utils.SecurityUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor provider for JPA auditing
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        return Optional.ofNullable(currentUserEmail);
    }
}

