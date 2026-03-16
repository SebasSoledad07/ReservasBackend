package com.devsenior.soledad.reservas_backend.security;

import com.devsenior.soledad.reservas_backend.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves current tenant company id from the security context or request metadata.
 */
@Component
public class TenantContextResolver {

    /**
     * Resolves company id for the current request.
     *
     * @param request current HTTP request
     * @return company identifier
     * @throws BadRequestException when company id is missing or invalid
     */
    public Long resolveCompanyId(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtPrincipal principal) {
            Long companyId = principal.getCompanyId();
            if (companyId != null) {
                return companyId;
            }
        }

        Object attr = request.getAttribute("companyId");
        if (attr instanceof Long value) {
            return value;
        }
        if (attr instanceof Integer value) {
            return value.longValue();
        }

        String header = request.getHeader("X-Company-Id");
        if (header != null && !header.isBlank()) {
            try {
                return Long.valueOf(header.trim());
            } catch (NumberFormatException ex) {
                throw new BadRequestException("Invalid X-Company-Id header value");
            }
        }

        throw new BadRequestException("Company id missing in token or header 'X-Company-Id'");
    }
}

