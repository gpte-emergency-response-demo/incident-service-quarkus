package com.redhat.cajun.navy.incident.service;

import com.redhat.cajun.navy.incident.model.Incident;
import io.vertx.core.json.JsonObject;

public class IncidentCodec {

    public JsonObject toJsonObject(Incident incident) {
        return new JsonObject().put("id", incident.getId())
                .put("lat", incident.getLat())
                .put("lon", incident.getLon())
                .put("medicalNeeded", incident.isMedicalNeeded())
                .put("numberOfPeople", incident.getNumberOfPeople())
                .put("victimName", incident.getVictimName())
                .put("victimPhoneNumber", incident.getVictimPhoneNumber())
                .put("timeStamp", incident.getTimestamp())
                .put("status", incident.getStatus());
    }

    public Incident fromJsonObject(JsonObject jsonObject) {
        return new Incident.Builder(jsonObject.getString("id"))
                .lat(jsonObject.getDouble("lat").toString())
                .lon(jsonObject.getDouble("lon").toString())
                .medicalNeeded(jsonObject.getBoolean("medicalNeeded"))
                .numberOfPeople(jsonObject.getInteger("numberOfPeople"))
                .victimName(jsonObject.getString("victimName"))
                .victimPhoneNumber(jsonObject.getString("victimPhoneNumber"))
                .timestamp(jsonObject.getLong("timeStamp"))
                .status(jsonObject.getString("status"))
                .build();
    }
}
