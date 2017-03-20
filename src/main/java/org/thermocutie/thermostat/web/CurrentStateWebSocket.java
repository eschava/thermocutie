package org.thermocutie.thermostat.web;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.thermocutie.thermostat.core.ThermoSystem;
import org.thermocutie.thermostat.core.ThermoSystemRegistry;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * {@link CurrentState} web-socket listener
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class CurrentStateWebSocket extends WebSocketAdapter implements ICurrentStateListener {
    private static final Timer PingTimer = new Timer(CurrentStateWebSocket.class.getSimpleName() + " ping timer", true);

    private CurrentStateObserver currentStateObserver;

    @Override
    public void onCurrentStateChanged(CurrentState currentState) {
        try {
            getRemote().sendString(currentState.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);

        PingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    RemoteEndpoint remote = getRemote();
                    if (remote != null)
                        remote.sendPong(null);
                    else
                        cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 30_000, 30_000);
    }

    @Override
    public void onWebSocketText(String message) {
        //noinspection UnnecessaryLocalVariable
        String system = message;
        ThermoSystem thermoSystem = ThermoSystemRegistry.get(system);
        if (thermoSystem != null)
            currentStateObserver = new CurrentStateObserver(thermoSystem, this);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        if (currentStateObserver != null)
            currentStateObserver.unsubscribe();

        System.out.println("Disconnected: " + reason);
        super.onWebSocketClose(statusCode, reason);
    }
}
