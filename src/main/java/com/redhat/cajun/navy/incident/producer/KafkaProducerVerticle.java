package com.redhat.cajun.navy.incident.producer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.redhat.cajun.navy.incident.message.IncidentReportedEvent;
import com.redhat.cajun.navy.incident.model.Incident;
import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerVerticle.class);

    private KafkaProducer<String, String> kafkaProducer;

    private String topic;

    @Override
    public Completable rxStart() {

        return Completable.fromMaybe(vertx.rxExecuteBlocking(future -> {
            //init Kafka Producer
            Map<String, String> kafkaConfig = new HashMap<>();
            kafkaConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config().getString("bootstrap.servers"));
            kafkaConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config().getString("key.serializer"));
            kafkaConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config().getString("value.serializer"));
            kafkaConfig.put(ProducerConfig.ACKS_CONFIG, "1");

            kafkaProducer = KafkaProducer.create(vertx, kafkaConfig);

            topic = config().getString("topic");

            vertx.eventBus().consumer("kafka-message-producer", this::produceMessage);
            future.complete();
        }));
    }

    @Override
    public Completable rxStop() {
        kafkaProducer.close();
        return Completable.complete();
    }

    private void produceMessage(Message<Incident> in) {
        Incident incident = in.body();
        com.redhat.cajun.navy.incident.message.Message<IncidentReportedEvent> message = new com.redhat.cajun.navy.incident.message.Message.Builder<>("IncidentReportedEvent", "IncidentService",
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
        KafkaProducerRecord<String, String> record = KafkaProducerRecord.create(topic, incident.getId(), json);
        kafkaProducer.rxWrite(record).subscribe(
                r -> log.debug("Sent 'IncidentReportedEvent' message for incident " + incident.getId()),
                t -> log.error("Error sending 'IncidentReportedEvent' message for incident " + incident.getId(), t));
    }
}
