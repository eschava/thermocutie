package org.thermocutie.thermostat.rest;

import org.thermocutie.thermostat.core.ThermoSystem;
import org.thermocutie.thermostat.core.ThermoSystemRegistry;
import org.thermocutie.thermostat.device.DeviceSet;
import org.thermocutie.thermostat.device.IDevice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * REST service for devices management
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
@Path("/device/{system}")
public class DevicesRestService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<IDevice> getDevices(@PathParam("system") String system) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        DeviceSet deviceSet = thermoSystem.getDeviceSet();
        return deviceSet.getDevices(IDevice.class);
    }
}
