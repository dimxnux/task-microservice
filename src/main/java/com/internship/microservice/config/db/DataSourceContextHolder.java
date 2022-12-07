package com.internship.microservice.config.db;

import org.springframework.stereotype.Component;

@Component
public class DataSourceContextHolder {
    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static void setContext(String value) {
        context.set(value);
    }

    public static String getContext() {
        return context.get();
    }

    public static void clearContext() {
        context.remove();
    }
}
