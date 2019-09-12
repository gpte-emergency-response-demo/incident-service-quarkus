package com.redhat.cajun.navy.incident.rest;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Readiness
@ApplicationScoped
public class ApplicationReadinessHealthCheck implements HealthCheck {


    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Readiness health check").up().build();
    }
}