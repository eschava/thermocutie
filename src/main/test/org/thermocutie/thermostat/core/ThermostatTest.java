package org.thermocutie.thermostat.core;

import org.easymock.EasyMock;
import org.junit.Test;
import org.thermocutie.thermostat.device.HVAC;
import org.thermocutie.thermostat.model.Temperature;

/**
 * Test for {@link Thermostat}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class ThermostatTest {

    @Test
    public void testHeating() throws Exception {
        HVAC hvac = EasyMock.createMock(HVAC.class);

        Thermostat thermostat = new Thermostat();
        thermostat.setTargetTemperature(new Temperature(20));
        thermostat.setHvac(hvac);
        thermostat.setDelta(0.5);

        EasyMock.reset(hvac);
        EasyMock.expect(hvac.isHeating()).andReturn(true);
        EasyMock.expect(hvac.isOn()).andReturn(false);
        hvac.setOn(EasyMock.eq(true)); EasyMock.expectLastCall();
        EasyMock.replay(hvac);
        thermostat.setCurrentTemperature(new Temperature(19));
        EasyMock.verify(hvac);

        EasyMock.reset(hvac);
        EasyMock.expect(hvac.isHeating()).andReturn(true);
        EasyMock.expect(hvac.isOn()).andReturn(false);
        hvac.setOn(EasyMock.eq(true)); EasyMock.expectLastCall();
        EasyMock.replay(hvac);
        thermostat.setCurrentTemperature(new Temperature(19.5));
        EasyMock.verify(hvac);

        EasyMock.reset(hvac);
        EasyMock.expect(hvac.isHeating()).andReturn(true);
        EasyMock.expect(hvac.isOn()).andReturn(false);
//        hvac.setOn(EasyMock.eq(true)); EasyMock.expectLastCall();
        EasyMock.replay(hvac);
        thermostat.setCurrentTemperature(new Temperature(19.6));
        EasyMock.verify(hvac);
    }
}