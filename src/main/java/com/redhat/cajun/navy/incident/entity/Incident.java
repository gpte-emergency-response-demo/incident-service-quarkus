package com.redhat.cajun.navy.incident.entity;

import java.time.Instant;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Access(AccessType.FIELD)
@SequenceGenerator(name="ReportedIncidentSeq", sequenceName="REPORTED_INCIDENT_SEQ", allocationSize = 10)
@Table(name = "reported_incident")
@NamedQueries({
        @NamedQuery(name = "Incident.findAll", query = "SELECT i from Incident i"),
        @NamedQuery(name = "Incident.byIncidentId", query = "SELECT i FROM Incident i WHERE i.incidentId = :incidentId"),
        @NamedQuery(name = "Incident.byStatus", query = "SELECT i from Incident i WHERE i.status = :status"),
        @NamedQuery(name = "Incident.findByName", query = "SELECT i from Incident i WHERE LOWER(i.victimName) LIKE :pattern"),
        @NamedQuery(name = "Incident.deleteAll", query = "DELETE FROM Incident")
})
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="ReportedIncidentSeq")
    private long id;

    @Column(name = "incident_id")
    private String incidentId;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    @Column(name = "medical_needed")
    private boolean medicalNeeded;

    @Column(name = "victim_name")
    private String victimName;

    @Column(name = "victim_phone")
    private String victimPhoneNumber;

    @Basic
    @Column(name = "reported_time")
    private Instant reportedTime;

    @Column(name = "incident_status")
    private String status;

    @Column(name = "version")
    @Version
    private long version;

    public long getId() {
        return id;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public Boolean isMedicalNeeded() {
        return medicalNeeded;
    }

    public String getVictimName() {
        return victimName;
    }

    public String getVictimPhoneNumber() {
        return victimPhoneNumber;
    }

    public long getTimestamp() {
        return reportedTime.toEpochMilli();
    }

    public Instant getReportedTime() {
        return reportedTime;
    }

    public String getStatus() {
        return status;
    }

    public long getVersion() {
        return version;
    }

    public static class Builder {

        private final Incident incident;

        public Builder() {
            incident = new Incident();
        }

        public Builder(long id, long version) {
            incident = new Incident();
            incident.id = id;
            incident.version = version;
        }

        public Builder incidentId(String incidentId) {
            incident.incidentId = incidentId;
            return this;
        }

        public Builder latitude(String latitude) {
            incident.latitude = latitude;
            return this;
        }

        public Builder longitude(String longitude) {
            incident.longitude = longitude;
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

        public Builder reportedTime(long timestamp) {
            incident.reportedTime = Instant.ofEpochMilli(timestamp);
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
