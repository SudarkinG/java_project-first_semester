package com.mipt.sudarkingeorgiy.config;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.UUID;

/** Бин с request
 * в каждом запросе свой экземпляр с requestId и временем старта */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {

    private final String requestId = UUID.randomUUID().toString();
    private final Instant startedAt = Instant.now();

    public String getRequestId() {
        return requestId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }
}

