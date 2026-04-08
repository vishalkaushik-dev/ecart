package com.JVM.eCart.common.utils;

import com.JVM.eCart.security.jwt.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UtilsHelper {

    public UserPrincipal getCurrentUserPrincipal() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new RuntimeException("Invalid authentication principal");
        }

        return userPrincipal;
    }

}
