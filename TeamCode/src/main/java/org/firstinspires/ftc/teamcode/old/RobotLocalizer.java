package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.Vector2;

public class RobotLocalizer {

    private DcMotor motorFront;
    private DcMotor motorRear;
    private DcMotor motorLeft;
    private DcMotor motorRight;

    private RobotConfiguration robot;

    private int[] lastEncoderValues = {0, 0, 0, 0};

    private Location fieldLocation;

    public RobotLocalizer(DcMotor motorFront, DcMotor motorRight, DcMotor motorRear, DcMotor motorLeft, RobotConfiguration robotConfiguration) {
        this.motorFront = motorFront;
        this.motorRight = motorRight;
        this.motorRear = motorRear;
        this.motorLeft = motorLeft;

        this.robot = robotConfiguration;

        this.fieldLocation = new Location(0, 0, 0);
    }

    public int[] getDriveEncoderValues() {
        int[] encoders = new int[4];

        // Invert the rear and right motor encoder values
        encoders[0] = motorFront.getCurrentPosition();
        encoders[1] = -motorRight.getCurrentPosition();
        encoders[2] = -motorRear.getCurrentPosition();
        encoders[3] = motorLeft.getCurrentPosition();

        return encoders;
    }

    public void updateLocation() {
        int[] currentEncoderValues = getDriveEncoderValues();

        fieldLocation.setHeading(robot.getHeading());

        Location fieldDelta = calculateFieldDelta(currentEncoderValues);

        updateEncoderValues(currentEncoderValues);

        fieldLocation.setX(fieldLocation.getX() + fieldDelta.getX());
        fieldLocation.setY(fieldLocation.getY() + fieldDelta.getY());
    }

    private Location calculateFieldDelta(int[] encoderValues) {
        Location robotDelta = calculateRobotDelta(encoderValues);

        double xDeltaField = robotDelta.getX() * Math.cos(Math.toRadians(fieldLocation.getHeading())) -
                robotDelta.getY() * Math.sin(Math.toRadians(fieldLocation.getHeading()));
        double yDeltaField = robotDelta.getX() * Math.sin(Math.toRadians(fieldLocation.getHeading())) +
                robotDelta.getY() * Math.cos(Math.toRadians(fieldLocation.getHeading()));

        return new Location(xDeltaField, yDeltaField);
    }

    private Location calculateRobotDelta(int[] encoderValues) {
        double[] movementDeltas = calculateMovementDeltas(encoderValues);

        double xDeltaRobot = movementDeltas[0] - movementDeltas[2];
        double yDeltaRobot = movementDeltas[3] - movementDeltas[1];
//        double xDeltaRobot = movementDeltas[3] - movementDeltas[1];
//        double yDeltaRobot = movementDeltas[0] - movementDeltas[2];

        return new Location(xDeltaRobot, yDeltaRobot);
    }

    private double[] calculateMovementDeltas(int[] encoderValues) {
        double[] deltas = calculateDeltas(encoderValues);

        double average = (deltas[0] + deltas[1] + deltas[2] + deltas[3]) / 4;
        for (int i = 0; i < 4; i++) {
            deltas[i] = deltas[i] - average;
        }

        return deltas;
    }

    private double[] calculateDeltas(int[] encoderValues) {
        double[] deltas = new double[encoderValues.length];
        for (int i = 0; i < encoderValues.length; i++) {
            deltas[i] = encoderValues[i] - lastEncoderValues[i];
        }

        return convertEncoderTicksToInches(deltas);
    }

    private double[] convertEncoderTicksToInches(double[] encoderValues){
        double[] inches = new double[encoderValues.length];
        for(int i=0; i<encoderValues.length; i++){
            inches[i] = encoderValues[i] * CONSTANTS.INCHES_PER_TICK;
        }

        return inches;
    }

    private void updateEncoderValues(int[] encoderValues) {
        lastEncoderValues = encoderValues;
    }

    public Location getFieldLocation() {
        return fieldLocation;
    }

    public Location getRobotCenter() {
        double headingRadians = Math.toRadians(fieldLocation.getHeading());
        Vector2 offset = Vector2.rotate(new Vector2(RobotConfiguration.imu_center.x(), RobotConfiguration.imu_center.y()), headingRadians);

        return new Location(fieldLocation.getX() - offset.x(), fieldLocation.getY() - offset.y(), fieldLocation.heading);
    }

    private static class CONSTANTS {
        static double TICKS_PER_REVOLUTION = 288.0;
        static double WHEEL_RADIUS_INCHES = 2;
        static double WHEEL_CIRCUMFERENCE_INCHES = 2 * WHEEL_RADIUS_INCHES * 3.14159;
        static double INCHES_PER_TICK = WHEEL_CIRCUMFERENCE_INCHES / TICKS_PER_REVOLUTION;

        static double WHEEL_BASE_INCHES = 8.75;
        static double WHEEL_BASE_CIRCUMFERENCE_INCHES = WHEEL_BASE_INCHES * 3.14159;
    }

    public static class Location {
        private double x;
        private double y;
        private double heading;

        public Location(double x, double y) {
            this.x = x;
            this.y = y;
            this.heading = 0.0;
        }

        public Location(double x, double y, double heading) {
            this.x = x;
            this.y = y;
            this.heading = heading;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getHeading() {
            return heading;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setHeading(double heading) {
            this.heading = heading;
        }
    }

}
