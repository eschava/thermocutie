package org.thermocutie.thermostat.rest;

import org.thermocutie.thermostat.core.ThermoSystem;
import org.thermocutie.thermostat.core.ThermoSystemRegistry;
import org.thermocutie.thermostat.schedule.ISchedule;
import org.thermocutie.thermostat.schedule.ScheduleSet;
import org.thermocutie.thermostat.schedule.WeekSchedule;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST service for schedules management
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
@Path("/schedule/{system}")
public class ScheduleRestService {
    @GET
    @Path("/{schedule}")
    @Produces(MediaType.APPLICATION_JSON)
    public ISchedule get(@PathParam("system") String system,
                         @PathParam("schedule") String schedule) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        ScheduleSet scheduleSet = thermoSystem.getScheduleSet();
        return scheduleSet.getSchedule(schedule);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateWeekSchedule(@PathParam("system") String system,
                                   WeekSchedule schedule) {
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        thermoSystem.updateSchedule(schedule);
    }
}
