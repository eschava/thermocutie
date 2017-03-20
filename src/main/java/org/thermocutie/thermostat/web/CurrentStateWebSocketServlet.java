package org.thermocutie.thermostat.web;

import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Factory for {@link CurrentStateWebSocket}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class CurrentStateWebSocketServlet extends org.eclipse.jetty.websocket.servlet.WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(60000);
        factory.register(CurrentStateWebSocket.class);
    }
}
