package com.redhat.cajun.navy.incident.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.redhat.cajun.navy.incident.message.IncidentReportedEvent;
import com.redhat.cajun.navy.incident.model.Incident;
import io.quarkus.vertx.ConsumeEvent;
import io.reactivex.processors.BehaviorProcessor;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventBusConsumer {

    private static Logger log = LoggerFactory.getLogger(EventBusConsumer.class);

    @Inject
    IncidentService service;

    private BehaviorProcessor<Incident> processor = BehaviorProcessor.create();

    private IncidentCodec codec = new IncidentCodec();

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
            case "createIncident":
                createIncident(msg);
                break;
            default:
                msg.fail(-1, "Unsupported operation");
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

    private void createIncident(Message<JsonObject> msg) {
        Incident created = service.create(codec.fromJsonObject(msg.body()));
        boolean success = false;
        while (!success) {
            success = processor.offer(created);
        }
        msg.reply(new JsonObject());
    }

    private JsonObject toJsonObject(Incident incident) {
        return codec.toJsonObject(incident);
    }

    @Outgoing("incident-reported-event")
    public PublisherBuilder<org.eclipse.microprofile.reactive.messaging.Message<String>> source() {
        return ReactiveStreams.fromPublisher(processor).flatMapCompletionStage(this::toMessage);
    }

    private CompletionStage<org.eclipse.microprofile.reactive.messaging.Message<String>> toMessage(Incident incident) {
        com.redhat.cajun.navy.incident.message.Message<IncidentReportedEvent> message
                = new com.redhat.cajun.navy.incident.message.Message.Builder<>("IncidentReportedEvent", "IncidentService",
                    new IncidentReportedEvent.Builder(incident.getId())
                        .lat(new BigDecimal(incident.getLat()))
                        .lon(new BigDecimal(incident.getLon()))
                        .medicalNeeded(incident.isMedicalNeeded())
                        .numberOfPeople(incident.getNumberOfPeople())
                        .timestamp(incident.getTimestamp())
                        .build())
                .build();
        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(message);
        log.info("Message: " + json);
        CompletableFuture<org.eclipse.microprofile.reactive.messaging.Message<String>> future = new CompletableFuture<>();
        KafkaMessage<String, String> kafkaMessage = KafkaMessage.of(incident.getId(), json);
        future.complete(kafkaMessage);
        return future;
    }
}
