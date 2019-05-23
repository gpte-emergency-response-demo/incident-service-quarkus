package com.redhat.cajun.navy.incident.consumer;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import com.redhat.cajun.navy.incident.message.Message;
import com.redhat.cajun.navy.incident.message.UpdateIncidentCommand;
import com.redhat.cajun.navy.incident.message.UpdateIncidentCommandMessageAdapter;
import com.redhat.cajun.navy.incident.model.Incident;
import com.redhat.cajun.navy.incident.service.IncidentService;
import io.smallrye.reactive.messaging.kafka.ReceivedKafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class IncidentCommandMessageSource {

    private final static Logger log = LoggerFactory.getLogger(IncidentCommandMessageSource.class);

    private static final String UPDATE_INCIDENT_COMMAND = "UpdateIncidentCommand";
    private static final String[] ACCEPTED_MESSAGE_TYPES = {UPDATE_INCIDENT_COMMAND};

    @Inject
    IncidentService incidentService;

    @Incoming("incident-command")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<ReceivedKafkaMessage<String, String>> processMessage(ReceivedKafkaMessage<String, String> message) {
        try {
            acceptMessageType(message.getPayload()).ifPresent(m -> processUpdateIncidentCommand(message.getPayload()));
        } catch (Exception e) {
            log.error("Error processing msg " + message.getPayload(), e);
        }
        return message.ack().toCompletableFuture().thenApply(x -> message);
    }

    @SuppressWarnings("unchecked")
    private void processUpdateIncidentCommand(String messageAsJson) {

        Message<UpdateIncidentCommand> message;

        JsonbConfig config = new JsonbConfig().withAdapters(new UpdateIncidentCommandMessageAdapter());
        Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();
        message = jsonb.fromJson(messageAsJson, Message.class);
        Incident incident = message.getBody().getIncident();

        log.debug("Processing '" + UPDATE_INCIDENT_COMMAND + "' message for incident '" + incident.getId() + "'");
        incidentService.updateIncident(incident);
    }

    private Optional<String> acceptMessageType(String messageAsJson) {
        try {
            JsonObject jsonReader = Json.createReader(new StringReader(messageAsJson)).readObject();
            String messageType = jsonReader.getString("messageType");
            if (Arrays.asList(ACCEPTED_MESSAGE_TYPES).contains(messageType)) {
                return Optional.of(messageType);
            }
            log.debug("Message with type '" + messageType + "' is ignored");
        } catch (Exception e) {
            log.warn("Unexpected message which is not JSON or without 'messageType' field.");
            log.warn("Message: " + messageAsJson);
        }
        return Optional.empty();
    }

}
