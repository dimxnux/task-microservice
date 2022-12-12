package com.internship.microservice.config.db;

import com.internship.microservice.service.RoutingDataSourceService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RoutingDataSourcePostConfig {
    private final RoutingDataSourceService routingDataSourceService;

    public RoutingDataSourcePostConfig(RoutingDataSourceService routingDataSourceService) {
        this.routingDataSourceService = routingDataSourceService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void configureRoutingDataSourceAfterApplicationReady() {
        routingDataSourceService.refreshTargetDataSources();
    }
}
