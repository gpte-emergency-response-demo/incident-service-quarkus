package com.redhat.cajun.navy.incident;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.redhat.cajun.navy.incident.model.Incident;
import com.redhat.cajun.navy.incident.service.IncidentService;

@Path("/incidents")
public class IncidentResource {

    @Inject
    IncidentService incidentService;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response incidents() {
        return Response.ok(incidentService.incidents()).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createIncident(Incident incident, @Context UriInfo uriInfo) {
        Incident created = incidentService.create(incident);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path("incident").path(created.getId());
        return Response.created(builder.build()).build();
    }

    @GET
    @Path("/incident/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response incidentById(@PathParam("id") String id) {
        Incident incident = incidentService.incidentByIncidentId(id);
        if (incident == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(incident).build();
    }

    @GET
    @Path("/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response incidentsByStatus(@PathParam("status") String status) {
        return Response.ok(incidentService.incidentsByStatus(status)).build();
    }

    @GET
    @Path("/byname/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response incidentsByVictimName(@PathParam("name") String name) {
        return Response.ok(incidentService.incidentsByVictimName(name)).build();
    }

    @POST
    @Path("/reset")
    public Response reset() {
        incidentService.reset();
        return Response.ok().build();
    }
}