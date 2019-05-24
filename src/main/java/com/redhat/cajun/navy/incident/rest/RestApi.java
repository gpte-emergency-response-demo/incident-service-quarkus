package com.redhat.cajun.navy.incident.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.redhat.cajun.navy.incident.service.IncidentCodec;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RestApi {

    private static Logger log = LoggerFactory.getLogger(RestApi.class);

    @Inject
    Vertx vertx;

    @Inject
    Config config;

    void onStart(@Observes StartupEvent ev) {

        Router router = Router.router(vertx);

        //health check
        router.get("/health").handler(this::healthCheck);

        router.route("/incidents").handler(BodyHandler.create());
        router.get("/incidents").handler(this::allIncidents);
        router.get("/incidents/incident/:id").handler(this::incidentById);
        router.get("/incidents/:status").handler(this::incidentsByStatus);
        router.get("/incidents/byname/:name").handler(this::incidentsByName);
        router.post("/incidents/reset").handler(this::reset);
        router.post("/incidents").handler(this::create);

        vertx.createHttpServer().requestHandler(router)
                .rxListen( config.getOptionalValue("http.server.port", Integer.class).orElse(8080))
                .subscribe(h -> log.info("Http Server started successfully"),
                        t -> log.error("Error when starting Http server", t));

        vertx.eventBus().registerCodec(new IncidentCodec());

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
                .subscribe((msg) -> {
                    JsonObject incident = msg.body().getJsonObject("incident");
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
                .subscribe((msg) -> rc.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                        .end(msg.body().getJsonArray("incidents").encode()), rc::fail);
    }

    private void incidentsByName(RoutingContext rc) {
        String name = rc.request().getParam("name");
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidentsByName");
        vertx.eventBus().<JsonObject>rxSend("incident-service", new JsonObject().put("name", name), options)
                .subscribe((msg) -> rc.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                        .end(msg.body().getJsonArray("incidents").encode()), rc::fail);
    }

    private void reset(RoutingContext rc) {
        DeliveryOptions options = new DeliveryOptions().addHeader("action", "reset");
        vertx.eventBus().rxSend("incident-service", new JsonObject(), options)
                .subscribe((msg) -> rc.response().setStatusCode(200).end(), rc::fail);
    }

    private void create(RoutingContext rc) {
        Buffer buffer = rc.getBody();
        DeliveryOptions options = new DeliveryOptions().setCodecName(new IncidentCodec().name()).addHeader("action", "createIncident");
        vertx.eventBus().rxSend("incident-service", buffer.getDelegate(), options)
                .subscribe((msg) -> rc.response().setStatusCode(200).end(), rc::fail);

    }

}
