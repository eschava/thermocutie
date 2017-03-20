package org.thermocutie.thermostat.rest;

import org.thermocutie.thermostat.core.TemperatureMode;
import org.thermocutie.thermostat.core.TemperatureModeRegistry;
import org.thermocutie.thermostat.core.ThermoSystem;
import org.thermocutie.thermostat.core.ThermoSystemRegistry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * REST service for temperature modes management
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
@Path("/temperaturemode/{system}")
public class TemperatureModeRestService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<TemperatureMode> get(@PathParam("system") String system) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        return thermoSystem.getTemperatureModes();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void add(@PathParam("system") String system,
                    TemperatureMode mode) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        thermoSystem.addTemperatureMode(mode);
   	}

   	@PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(@PathParam("system") String system,
                    TemperatureMode mode) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        thermoSystem.updateTemperatureMode(mode);
   	}

   	@PUT
    @Path("/rename/{oldName}/{newName}")
    public void rename(@PathParam("system") String system,
                       @PathParam("oldName") String oldModeName,
                       @PathParam("newName") String newModeName) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        thermoSystem.renameTemperatureMode(oldModeName, newModeName);
    }

   	@DELETE
   	@Path("/{name}")
    public void remove(@PathParam("system") String system,
                       @PathParam("name") String name) throws Exception {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        thermoSystem.removeTemperatureMode(name);
   	}
}
