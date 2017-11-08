package org.thermocutie.thermostat.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for system properties
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
@XmlRootElement
public class System {
    private String name;
    private String title;

    public System(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
