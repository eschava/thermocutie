package org.thermocutie.thermostat.model;

/**
 * Represents temperature
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class Temperature {
    private double value;

    public Temperature(double value) {
        this.value = value;
    }

    public Temperature() {
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
