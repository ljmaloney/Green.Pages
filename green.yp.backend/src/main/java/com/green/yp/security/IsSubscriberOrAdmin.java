package com.green.yp.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('GREENPAGES-SUBSCRIBER','GREENPAGES-SUBSCRIBERADMIN')")
public @interface IsSubscriberOrAdmin {
}

