package com.redhat.cajun.navy.incident.producer;

import com.redhat.cajun.navy.incident.model.Incident;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class IncidentCodec implements MessageCodec<Incident, Incident> {


    @Override
    public void encodeToWire(Buffer buffer, Incident incident) {
        JsonObject jsonObject = new JsonObject().put("id", incident.getId())
                .put("lat", incident.getLat()).put("lon", incident.getLon())
                .put("medicalNeeded", incident.isMedicalNeeded())
                .put("numberOfPeople", incident.getNumberOfPeople())
                .put("victimName", incident.getVictimName())
                .put("victimPhoneNumber", incident.getVictimPhoneNumber())
                .put("timeStamp", incident.getTimestamp())
                .put("status", incident.getStatus());
        String jsonStr = jsonObject.encode();
        int length = jsonStr.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonStr);
    }

    @Override
    public Incident decodeFromWire(int position, Buffer buffer) {
        int _pos = position;
        int length = buffer.getInt(_pos);
        String jsonStr = buffer.getString(_pos+=4, _pos+=length);
        JsonObject jsonObject = new JsonObject(jsonStr);
        return new Incident.Builder(jsonObject.getString("id"))
                .lat(jsonObject.getString("lat"))
                .lon(jsonObject.getString("lon"))
                .medicalNeeded(jsonObject.getBoolean("medicalNeeded"))
                .numberOfPeople(jsonObject.getInteger("numberOfPeople"))
                .victimName(jsonObject.getString("victimName"))
                .victimPhoneNumber(jsonObject.getString("victimPhoneNumber"))
                .timestamp(jsonObject.getLong("timeStamp"))
                .status(jsonObject.getString("status"))
                .build();
    }

    @Override
    public Incident transform(Incident incident) {
        return incident;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
