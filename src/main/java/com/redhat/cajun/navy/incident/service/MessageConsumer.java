package com.redhat.cajun.navy.incident.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.cajun.navy.incident.model.Incident;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

@ApplicationScoped
public class MessageConsumer {

    @Inject
    IncidentService service;

    @ConsumeEvent(value = "incident-service", blocking = true)
    public void consume(Message<JsonObject> msg) {
        String action = msg.headers().get("action");
        switch (action) {
            case "incidents" :
                incidents(msg);
                break;
            case "incidentById" :
                incidentById(msg);
                break;
            case "incidentsByStatus":
                incidentsByStatus(msg);
                break;
            case "incidentsByName":
                incidentsByName(msg);
                break;
            case "reset" :
                reset(msg);
                break;
        }
    }

    private void incidents(Message<JsonObject> msg) {
        List<Incident> incidents = service.incidents();
        JsonArray incidentsArray = new JsonArray(incidents.stream().map(this::toJsonObject).collect(Collectors.toList()));
        JsonObject jsonObject = new JsonObject().put("incidents", incidentsArray);
        msg.reply(jsonObject);
    }

    private void incidentById(Message<JsonObject> msg) {
        String id = msg.body().getString("incidentId");
        Incident incident = service.incidentByIncidentId(id);
        if (incident == null) {
            msg.reply(new JsonObject());
        } else {
            msg.reply(new JsonObject().put("incident", toJsonObject(incident)));
        }
    }

    private void incidentsByStatus(Message<JsonObject> msg) {
        String status = msg.body().getString("status");
        List<Incident> incidents = service.incidentsByStatus(status);
        JsonArray incidentsArray = new JsonArray(incidents.stream().map(this::toJsonObject).collect(Collectors.toList()));
        JsonObject jsonObject = new JsonObject().put("incidents", incidentsArray);
        msg.reply(jsonObject);
    }

    private void incidentsByName(Message<JsonObject> msg) {
        String name = msg.body().getString("name");
        List<Incident> incidents = service.incidentsByVictimName(name);
        JsonArray incidentsArray = new JsonArray(incidents.stream().map(this::toJsonObject).collect(Collectors.toList()));
        JsonObject jsonObject = new JsonObject().put("incidents", incidentsArray);
        msg.reply(jsonObject);
    }

    private void reset(Message<JsonObject> msg) {
        service.reset();
        msg.reply(new JsonObject());
    }

    private JsonObject toJsonObject(Incident incident) {
        return new JsonObject().put("id", incident.getId())
                .put("lat", incident.getLat()).put("lon", incident.getLon())
                .put("medicalNeeded", incident.isMedicalNeeded())
                .put("numberOfPeople", incident.getNumberOfPeople())
                .put("victimName", incident.getVictimName())
                .put("victimPhoneNumber", incident.getVictimPhoneNumber())
                .put("timeStamp", incident.getTimestamp())
                .put("status", incident.getStatus());
    }
}
