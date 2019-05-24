package com.redhat.cajun.navy.incident.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Incident {

    private String id;

    private String lat;

    private String lon;

    private Integer numberOfPeople;

    private Boolean medicalNeeded;

    private String victimName;

    private String victimPhoneNumber;

    private Long timestamp;

    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Boolean isMedicalNeeded() {
        return medicalNeeded;
    }

    public void setMedicalNeeded(Boolean medicalNeeded) {
        this.medicalNeeded = medicalNeeded;
    }

    public String getVictimName() {
        return victimName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }

    public String getVictimPhoneNumber() {
        return victimPhoneNumber;
    }

    public void setVictimPhoneNumber(String victimPhoneNumber) {
        this.victimPhoneNumber = victimPhoneNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Builder {

        private final Incident incident;

        public Builder() {
            incident = new Incident();
        }

        public Builder(String id) {
            incident = new Incident();
            incident.id = id;
        }

        public Builder lat(String lat) {
            incident.lat = lat;
            return this;
        }

        public Builder lon(String lon) {
            incident.lon = lon;
            return this;
        }

        public Builder numberOfPeople(Integer numberOfPeople) {
            incident.numberOfPeople = numberOfPeople;
            return this;
        }

        public Builder medicalNeeded(Boolean medicalNeeded) {
            incident.medicalNeeded = medicalNeeded;
            return this;
        }

        public Builder victimName(String victimName) {
            incident.victimName = victimName;
            return this;
        }

        public Builder victimPhoneNumber(String victimPhoneNumber) {
            incident.victimPhoneNumber = victimPhoneNumber;
            return this;
        }

        public Builder timestamp(Long timestamp) {
            incident.timestamp = timestamp;
            return this;
        }

        public Builder status(String status) {
            incident.status = status;
            return this;
        }

        public Incident build() {
            return incident;
        }

    }
}
