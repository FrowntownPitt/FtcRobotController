package org.firstinspires.ftc.teamcode.util;

public class Vector2 {
    private double x;
    private double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2 rotate(Vector2 vector, double radians) {
        double newX;
        double newY;

        newX = Math.cos(radians) * vector.x - Math.sin(radians) * vector.y;
        newY = Math.sin(radians) * vector.x + Math.cos(radians) * vector.y;

        return new Vector2(newX, newY);
    }


    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }
}
