package com.redhat.cajun.navy.incident.rest;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Liveness
@ApplicationScoped
public class ApplicationLivenessHealthCheck implements HealthCheck {


    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Liveness health check").up().build();
    }
}