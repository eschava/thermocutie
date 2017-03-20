package org.thermocutie.thermostat.device;

import org.thermocutie.thermostat.xml.IXmlPersistable;

/**
 * Abstract device
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface IDevice extends IXmlPersistable {
    String getName();
}
