package com.green.yp.security;

import java.lang.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('GREENPAGES-ADMIN','GREENPAGES-SUBSCRIBERADMIN')")
public @interface IsSubscriberAdminOrAdmin {
}

