package org.thermocutie.thermostat.web;

import org.thermocutie.thermostat.core.GlobalSystem;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web-server listener instantiating {@link GlobalSystem}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class GlobalSystemListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            GlobalSystem globalSystem = GlobalSystem.INSTANCE;
            globalSystem.load();
            globalSystem.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
