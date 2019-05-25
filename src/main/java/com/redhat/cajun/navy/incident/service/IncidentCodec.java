package com.redhat.cajun.navy.incident.service;

import com.redhat.cajun.navy.incident.model.Incident;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class IncidentCodec implements MessageCodec<Buffer, Incident> {


    @Override
    public void encodeToWire(Buffer buffer, Buffer incident) {
        buffer.appendInt(incident.length());
        buffer.appendBuffer(incident);
    }

    @Override
    public Incident decodeFromWire(int position, Buffer buffer) {
        int _pos = position;
        int length = buffer.getInt(_pos);
        String jsonStr = buffer.getString(_pos+=4, _pos+=length);
        JsonObject jsonObject = new JsonObject(jsonStr);
        return fromJsonObject(jsonObject);
    }

    @Override
    public Incident transform(Buffer incident) {
        JsonObject jsonObject = new JsonObject(incident);
        return fromJsonObject(jsonObject);
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

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
