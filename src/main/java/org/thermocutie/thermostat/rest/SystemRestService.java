package org.thermocutie.thermostat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.core.GlobalSystem;
import org.thermocutie.thermostat.core.ThermoSystem;
import org.thermocutie.thermostat.core.ThermoSystemRegistry;
import org.thermocutie.thermostat.rest.dto.System;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * REST service for systems management
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
@Path("/system")
public class SystemRestService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SystemRestService.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<System> getSystems() {
//        return ThermoSystemRegistry.getNames().stream()
//                .map(name -> new SystemDTO(name, ThermoSystemRegistry.get(name).getSettings().getTitle()))
//                .collect(Collectors.toList());
        List<System> systems = new ArrayList<>(ThermoSystemRegistry.getNames().size());
        for (String name : ThermoSystemRegistry.getNames())
            systems.add(new System(name, ThermoSystemRegistry.get(name).getSettings().getTitle()));
        return systems;
    }

    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{name}/{title}")
    public void addSystem(@PathParam("name") String name,
                          @PathParam("title") String title) {
        if (ThermoSystemRegistry.get(name) != null)
            throw new RuntimeException("System with name '" + name + "' already exists");

        File folder = new File(GlobalSystem.INSTANCE.getConfigFolder(), name);
        folder.mkdir();

        ThermoSystem thermoSystem = new ThermoSystem();
        thermoSystem.loadFromFolder(folder);
        thermoSystem.getSettings().setTitle(title);
        thermoSystem.save();

        ThermoSystemRegistry.register(name, thermoSystem);
    }

    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{name}/{title}")
    public void change(@PathParam("name") String name,
                       @PathParam("title") String title) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(name);
        thermoSystem.getSettings().setTitle(title);
        thermoSystem.save();
    }

    @DELETE
//    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public void removeSystem(@PathParam("name") String name) {
        if (ThermoSystemRegistry.getNames().size() == 1)
            throw new RuntimeException("Cannot remove single system");


        try {
            ThermoSystem thermoSystem = ThermoSystemRegistry.get(name);
            thermoSystem.remove();
            ThermoSystemRegistry.remove(name);
        } catch (IOException e) {
            LOGGER.error("Cannot remove system " + name, e);
            throw new RuntimeException(e);
        }
    }
}
