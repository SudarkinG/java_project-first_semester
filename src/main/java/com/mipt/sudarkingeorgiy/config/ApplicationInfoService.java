package com.mipt.sudarkingeorgiy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Берёт имя и версию приложения из конфига через @Value */
@Service
public class ApplicationInfoService {

    private final String appName;
    private final String appVersion;

    public ApplicationInfoService(@Value("${app.name}") String appName,
                                  @Value("${app.version}") String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }
}

