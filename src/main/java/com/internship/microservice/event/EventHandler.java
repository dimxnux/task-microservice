package com.internship.microservice.event;

import com.internship.microservice.service.RoutingDataSourceService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventHandler {
    private final RoutingDataSourceService routingDataSourceService;

    public EventHandler(RoutingDataSourceService routingDataSourceService) {
        this.routingDataSourceService = routingDataSourceService;
    }

    @EventListener
    public void onRefreshTargetDataSources(DatabasesUpdateEvent databasesUpdateEvent) {
        routingDataSourceService.refreshTargetDataSources();
    }
}
