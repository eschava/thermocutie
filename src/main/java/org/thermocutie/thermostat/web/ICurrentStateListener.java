package org.thermocutie.thermostat.web;

/**
 * Listener for {@link CurrentState}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface ICurrentStateListener {
    void onCurrentStateChanged(CurrentState currentState);
}
