package com.restaurant.restaurantManagement.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationEvent {

    @EventListener
    public void onFailure(AuthorizationDeniedEvent deniedEvent) {
        log.warn("Access denied for user '{}'. Reason: {}",
                deniedEvent.getAuthentication().get().getName(),
                deniedEvent.getAuthorizationResult());
    }

}
