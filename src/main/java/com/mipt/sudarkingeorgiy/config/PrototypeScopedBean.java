package com.mipt.sudarkingeorgiy.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Бин с prototype
 * при каждом обращении создаётся новый экземпляр с новым id */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeScopedBean {

    private final String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }
}

