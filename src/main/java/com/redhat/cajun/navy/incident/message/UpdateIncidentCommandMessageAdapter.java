package com.redhat.cajun.navy.incident.message;

import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

import com.redhat.cajun.navy.incident.model.Incident;

public class UpdateIncidentCommandMessageAdapter implements JsonbAdapter<Message, JsonObject> {

    @Override
    public JsonObject adaptToJson(Message obj) throws Exception {
        return null;
    }

    @Override
    public Message<UpdateIncidentCommand> adaptFromJson(JsonObject adapted) throws Exception {
        JsonObject bodyJson = adapted.getJsonObject("body");
        JsonObject incidentJson = bodyJson.getJsonObject("incident");
        Incident incident = new Incident.Builder(incidentJson.getString("id"))
                .lat(incidentJson.containsKey("lat") ? incidentJson.getString("lat") : null)
                .lon(incidentJson.containsKey("lon") ? incidentJson.getString("lon") : null)
                .medicalNeeded(incidentJson.containsKey("medicalNeeded") ? incidentJson.getBoolean("medicalNeeded") : null)
                .numberOfPeople(incidentJson.containsKey("numberOfPeople") ? incidentJson.getInt("numberOfPeople") : null)
                .victimName(incidentJson.containsKey("victimName") ? incidentJson.getString("victimName") : null)
                .victimPhoneNumber(incidentJson.containsKey("victimPhoneNumber") ? incidentJson.getString("victimPhoneNumber") : null)
                .status(incidentJson.containsKey("status") ? incidentJson.getString("status") : null)
                .build();
        UpdateIncidentCommand command = new UpdateIncidentCommand.Builder(incident).build();
        return new Message.Builder<>(adapted.getString("messageType"), adapted.getString("invokingService"), command)
                .id(adapted.getString("id")).timestamp(adapted.getJsonNumber("timestamp").longValue()).build();
    }
}
