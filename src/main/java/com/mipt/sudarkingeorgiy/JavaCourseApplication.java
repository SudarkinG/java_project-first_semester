package com.mipt.sudarkingeorgiy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/** Точка входа Spring Boot */
@SpringBootApplication
@EnableAspectJAutoProxy
public class JavaCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaCourseApplication.class, args);
    }
}

