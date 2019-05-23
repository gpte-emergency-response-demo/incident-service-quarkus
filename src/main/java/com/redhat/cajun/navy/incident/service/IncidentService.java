package com.redhat.cajun.navy.incident.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.redhat.cajun.navy.incident.dao.IncidentDao;
import com.redhat.cajun.navy.incident.model.Incident;
import com.redhat.cajun.navy.incident.model.IncidentStatus;
import com.redhat.cajun.navy.incident.producer.IncidentCodec;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;

@ApplicationScoped
public class IncidentService {

    @Inject
    IncidentDao incidentDao;

    @Inject
    Vertx vertx;

    @Transactional
    public List<Incident> incidents() {
        return incidentDao.findAll().stream().map(this::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public Incident create(Incident incident) {
        incident.setId(UUID.randomUUID().toString());
        incident.setTimestamp(System.currentTimeMillis());
        com.redhat.cajun.navy.incident.entity.Incident created = incidentDao.create(toEntity(incident));

        DeliveryOptions options = new DeliveryOptions().setCodecName(new IncidentCodec().name());
        vertx.eventBus().send("kafka-message-producer", incident, options);

        return fromEntity(created);
    }

    @Transactional
    public Incident incidentByIncidentId(String incidentId) {
        return fromEntity(incidentDao.findByIncidentId(incidentId));
    }

    @Transactional
    public List<Incident> incidentsByStatus(String status) {
        return incidentDao.findByStatus(status).stream().map(this::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public List<Incident> incidentsByVictimName(String name) {
        return incidentDao.findByName(name).stream().map(this::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void reset() {
        incidentDao.deleteAll();
    }

    private Incident fromEntity(com.redhat.cajun.navy.incident.entity.Incident r) {

        if (r == null) {
            return null;
        }
        return new Incident.Builder(r.getIncidentId())
                .lat(r.getLatitude())
                .lon(r.getLongitude())
                .medicalNeeded(r.isMedicalNeeded())
                .numberOfPeople(r.getNumberOfPeople())
                .victimName(r.getVictimName())
                .victimPhoneNumber(r.getVictimPhoneNumber())
                .status(r.getStatus())
                .timestamp(r.getTimestamp())
                .build();
    }

    private com.redhat.cajun.navy.incident.entity.Incident toEntity(Incident incident) {

        return new com.redhat.cajun.navy.incident.entity.Incident.Builder()
                        .incidentId(incident.getId())
                        .latitude(incident.getLat())
                        .longitude(incident.getLon())
                        .medicalNeeded(incident.isMedicalNeeded())
                        .numberOfPeople(incident.getNumberOfPeople())
                        .victimName(incident.getVictimName())
                        .victimPhoneNumber(incident.getVictimPhoneNumber())
                        .reportedTime(incident.getTimestamp())
                        .status(IncidentStatus.REPORTED.name())
                        .build();
    }

}
