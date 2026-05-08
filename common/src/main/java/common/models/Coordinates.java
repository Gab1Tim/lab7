package common.models;

import java.io.Serializable;

/**
 * Координаты организации.
 */
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x;
    private Integer y;

    /** Создаёт координаты. */
    public Coordinates(double x, Integer y) {
        if (x <= -915) {
            throw new IllegalArgumentException("X coordinate must be bigger than -915");
        }
        if (y == null) {
            throw new IllegalArgumentException("Y coordinate must not be null");
        }
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates {x=" + x + ", y=" + y + "}";
    }
}