package com.redhat.cajun.navy.incident.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.eclipse.microprofile.config.Config;

@ApplicationScoped
public class VerticleDeployer {

    private static boolean deployed = false;

    private String deploymentId;

    @Inject
    Vertx vertx;

    @Inject
    Config config;

    void onStart(@Observes StartupEvent ev) {
        KafkaProducerVerticle producerVerticle = new KafkaProducerVerticle();
        vertx.rxDeployVerticle(producerVerticle, new DeploymentOptions().setConfig(extractConfiguration("kafka.producer"))).subscribe(s -> {
            vertx.eventBus().registerCodec(new IncidentCodec());
            deploymentId = s;
            deployed = true;
        });
    }

    void onStop(@Observes ShutdownEvent ev) {
        vertx.rxUndeploy(deploymentId).subscribe(() -> vertx.close());
    }

    public static boolean deployed() {
        return deployed;
    }

    private JsonObject extractConfiguration(String prefix) {
        Iterable<String> names = config.getPropertyNames();
        JsonObject jsonConfig = new JsonObject();
        names.forEach(key -> {
            if (key.startsWith(prefix)) {
                // Extract the name
                String name = key.substring(prefix.length() + 1);
                jsonConfig.put(name, config.getValue(key, String.class));
            }
        });
        return jsonConfig;
    }

}
