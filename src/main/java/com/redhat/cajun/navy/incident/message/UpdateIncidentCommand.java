package com.redhat.cajun.navy.incident.message;

import com.redhat.cajun.navy.incident.model.Incident;

public class UpdateIncidentCommand {

    private Incident incident;

    public Incident getIncident() {
        return incident;
    }

    public static class Builder {

        private final UpdateIncidentCommand command;

        public Builder(Incident incident) {
            command = new UpdateIncidentCommand();
            command.incident = incident;
        }

        public UpdateIncidentCommand build() {
            return command;
        }

    }
}
