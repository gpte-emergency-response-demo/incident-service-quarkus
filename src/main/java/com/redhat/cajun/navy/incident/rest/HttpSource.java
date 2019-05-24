package com.redhat.cajun.navy.incident.rest;

import java.util.concurrent.CompletableFuture;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.StartupEvent;
import io.reactivex.processors.BehaviorProcessor;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class HttpSource {

    private static Logger log = LoggerFactory.getLogger(HttpSource.class);

    @Inject
    Vertx vertx;

    @Inject
    Config config;

    private BehaviorProcessor<HttpServerRequest> processor;

    private CompletableFuture<HttpServer> future;

    void onStart(@Observes StartupEvent ev) {
        // create vertx HttpServer (use vertx web)
        Router router = Router.router(vertx);

        //health check
        router.get("/health").handler(this::healthCheck);

        //get all incidents
        router.get("/incidents").handler(this::allIncidents);
        router.get("/incidents/incident/:id").handler(this::incidentById);
        router.get("/incidents/:status").handler(this::incidentsByStatus);
        router.get("/incidents/byname/:name").handler(this::incidentsByName);
        router.post("/incidents/reset").handler(this::reset);

        vertx.createHttpServer().requestHandler(router).rxListen(8080)
            .subscribe(h -> log.info("Http Server started successfully"),
                    t -> log.error("Error when starting Http server", t));

        // handle health checks and other GET calls (use vertx eventbus)

        // handle POST with processor


    }

    private void healthCheck(RoutingContext rc) {
        rc.response().setStatusCode(200).end(new JsonObject().put("status", "ok").encode());
    }

    private void allIncidents(RoutingContext rc) {
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidents");
        vertx.eventBus().<JsonObject>rxSend("incident-service", new JsonObject(), options)
                .subscribe((json) -> rc.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                        .end(json.body().getJsonArray("incidents").encode()), rc::fail);
    }

    private void incidentById(RoutingContext rc) {
        String incidentId = rc.request().getParam("id");
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidentById");
        vertx.eventBus().<JsonObject>rxSend("incident-service", new JsonObject().put("incidentId", incidentId), options)
                .subscribe((json) -> {
                    JsonObject incident = json.body().getJsonObject("incident");
                    if (incident == null) {
                        rc.response().setStatusCode(404).end();
                    } else {
                        rc.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                                .end(incident.encode());
                    }
                }, rc::fail);

    }

    private void incidentsByStatus(RoutingContext rc) {
        String status = rc.request().getParam("status");
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidentsByStatus");
        vertx.eventBus().<JsonObject>rxSend("incident-service", new JsonObject().put("status", status), options)
                .subscribe((json) -> rc.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                        .end(json.body().getJsonArray("incidents").encode()), rc::fail);
    }

    private void incidentsByName(RoutingContext rc) {
        String name = rc.request().getParam("name");
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidentsByName");
        vertx.eventBus().<JsonObject>rxSend("incident-service", new JsonObject().put("name", name), options)
                .subscribe((json) -> rc.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                        .end(json.body().getJsonArray("incidents").encode()), rc::fail);
    }

    private void reset(RoutingContext rc) {
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "reset");
        vertx.eventBus().<JsonObject>rxSend("incident-service", new JsonObject(), options)
                .subscribe((json) -> rc.response().setStatusCode(200).end(), rc::fail);
    }

//    @Outgoing("create-incident")
//    public PublisherBuilder<Message<Incident>> source() {
//        return null;
//    }

}
