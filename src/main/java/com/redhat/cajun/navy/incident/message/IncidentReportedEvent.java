package com.redhat.cajun.navy.incident.message;

import java.math.BigDecimal;

public class IncidentReportedEvent {

    private String id;

    private BigDecimal lat;

    private BigDecimal lon;

    private int numberOfPeople;

    private boolean medicalNeeded;

    private long timestamp;

    public String getId() {
        return id;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public boolean isMedicalNeeded() {
        return medicalNeeded;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static class Builder {

        private IncidentReportedEvent ire;

        public Builder(String id) {
            ire = new IncidentReportedEvent();
            ire.id = id;
        }

        public Builder lat(BigDecimal lat) {
            ire.lat = lat;
            return this;
        }

        public Builder lon(BigDecimal lon) {
            ire.lon = lon;
            return this;
        }

        public Builder numberOfPeople(int numberOfPeople) {
            ire.numberOfPeople = numberOfPeople;
            return this;
        }

        public Builder medicalNeeded(boolean medicalNeeded) {
            ire.medicalNeeded = medicalNeeded;
            return this;
        }

        public Builder timestamp(long timestamp) {
            ire.timestamp = timestamp;
            return this;
        }

        public IncidentReportedEvent build() {
            return ire;
        }
    }
}
