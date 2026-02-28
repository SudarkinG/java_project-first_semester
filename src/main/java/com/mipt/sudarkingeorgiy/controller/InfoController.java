package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.config.ApplicationInfoService;
import com.mipt.sudarkingeorgiy.config.PrototypeScopedBean;
import com.mipt.sudarkingeorgiy.config.RequestScopedBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** Контроллер для демонстрации @Value и scope бинов */
@RestController
@RequestMapping("/api")
public class InfoController {

    private final ApplicationInfoService applicationInfoService;
    private final RequestScopedBean requestScopedBean;
    private final ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory;

    public InfoController(ApplicationInfoService applicationInfoService,
                          RequestScopedBean requestScopedBean,
                          ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory) {
        this.applicationInfoService = applicationInfoService;
        this.requestScopedBean = requestScopedBean;
        this.prototypeScopedBeanFactory = prototypeScopedBeanFactory;
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("appName", applicationInfoService.getAppName());
        result.put("appVersion", applicationInfoService.getAppVersion());
        result.put("requestId", requestScopedBean.getRequestId());
        result.put("requestStartedAt", requestScopedBean.getStartedAt().toString());
        result.put("prototypeId", prototypeScopedBeanFactory.getObject().getId());
        return result;
    }
}
