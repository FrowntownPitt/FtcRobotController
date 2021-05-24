package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.dashdemo.SpyderConfig;
import org.firstinspires.ftc.teamcode.util.Vector2;

public class RobotConfiguration {
    public static final float MM_PER_INCH = 25.4f;
    public static final float MM_FTC_FIELD_WIDTH = (12 * 6) * MM_PER_INCH;

    static final float ROBOT_LENGTH = 25.5f;
    static final float ROBOT_WIDTH = 25.5f;
    public static final Vector2 imu_center = new Vector2(0, 3);

    public Collector collector;

    public final BNO055IMU IMU;
    public final String IMU_NAME = "imu";

    private DcMotor motorFront;
    private DcMotor motorRear;
    private DcMotor motorLeft;
    private DcMotor motorRight;


    private DcMotor.RunMode defaultDriveRunMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;

    private float offsetZ = 0;
    private float offsetY = 0;
    private float offsetX = 0;

    private Orientation imu_angles;
    private RobotLocalizer localizer;

    public RobotConfiguration(HardwareMap hardwareMap) {
        IMU = hardwareMap.get(BNO055IMU.class, "imu");

        motorFront = hardwareMap.dcMotor.get("frontMotor");
        motorRear = hardwareMap.dcMotor.get("rearMotor");
        motorLeft = hardwareMap.dcMotor.get("leftMotor");
        motorRight = hardwareMap.dcMotor.get("rightMotor");

        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorFront.setDirection(DcMotor.Direction.REVERSE);

        resetDriveEncoders();

        collector = new Collector(hardwareMap);
    }

    public void initLocalizer(){
        localizer = new RobotLocalizer(motorFront, motorRight, motorRear, motorLeft, this);
        resetDriveEncoders();
    }

    public void updateLocalizer() {
        localizer.updateLocation();
    }

    private void resetDriveEncoders(){
        motorFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFront.setMode(defaultDriveRunMode);
        motorRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRear.setMode(defaultDriveRunMode);
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(defaultDriveRunMode);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(defaultDriveRunMode);
    }

    public void updateIMUAngles() {
        imu_angles = getOrientation();
    }

    public void initializeIMU() {
        BNO055IMU.Parameters imuparams = new BNO055IMU.Parameters();
        imuparams.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imuparams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imuparams.calibrationDataFile = "BNO055IMUCalibration.json";
        imuparams.loggingEnabled = true;
        imuparams.loggingTag = "IMU";

        IMU.initialize(imuparams);
    }

    public Orientation getOrientation() {
        return IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
    }

    public double getHeading() {
        return imu_angles.firstAngle - offsetZ;
    }

    public void resetIMUOffset() {
        offsetZ = imu_angles.firstAngle;
        offsetY = imu_angles.secondAngle;
        offsetX = imu_angles.thirdAngle;
    }

    public void setPowerFromAngle(double angle, double power, double rotation) {
        double leftPower = power * Math.sin(angle) + rotation;
        double rightPower = power * Math.sin(angle) - rotation;
        double frontPower = power * Math.cos(angle) + rotation;
        double rearPower = power * Math.cos(angle) - rotation;

        motorLeft.setPower(leftPower);
        motorRight.setPower(rightPower);
        motorFront.setPower(frontPower);
        motorRear.setPower(rearPower);
    }

    public void setPowerFromJoysticks(double x, double y, double r) {
        double power = Math.hypot(x, y);

        double field_centric_offset = 0;

        if (SpyderConfig.IS_FIELD_CENTRIC) {
            field_centric_offset = (Math.toRadians(getHeading() - offsetZ));
        }

        double targetAngle = Math.atan2(y, x) - field_centric_offset;

        setPowerFromAngle(targetAngle, power, r);
    }

    public int[] getDriveEncoderValues(){
        int[] encoders = new int[4];

        encoders[0] = motorFront.getCurrentPosition();
        encoders[1] = motorRight.getCurrentPosition();
        encoders[2] = motorRear.getCurrentPosition();
        encoders[3] = motorLeft.getCurrentPosition();

        return encoders;
    }

    public void moveCollectorFromGamepad(Gamepad gamepad) {
        if (gamepad.right_bumper) {
            collector.lift(CollectorMovement.UP);
        } else if (gamepad.right_trigger > 0) {
            collector.lift(CollectorMovement.DOWN);
        }

        if (gamepad.left_bumper) {
            collector.grab(CollectorMovement.RELEASE);
        } else if (gamepad.left_trigger > 0) {
            collector.grab(CollectorMovement.GRAB);
        }
    }

    public RobotLocalizer.Location getFieldLocation() {
        return localizer.getFieldLocation();
    }

    public RobotLocalizer.Location getRobotCenter() {
        return localizer.getRobotCenter();
    }

    public void displayIMUData(Telemetry telemetry) {
        updateIMUAngles();

        telemetry.addData("Heading ", getHeading());
        telemetry.addData("Roll    ", imu_angles.secondAngle - offsetY);
        telemetry.addData("Pitch   ", imu_angles.thirdAngle - offsetX);
    }

    public void displayFieldLocation(Telemetry telemetry) {
        RobotLocalizer.Location fieldLocation = getFieldLocation();
        telemetry.addData("Field Location (X, Y, Heading)",
                "(%f, %f, %f)",
                fieldLocation.getX(), fieldLocation.getY(), fieldLocation.getHeading());
    }

    public float getHeadingOffset() {
        return offsetZ;
    }

    public RobotLocalizer getLocalizer() {
        return localizer;
    }

    public class Collector {
        private Servo claw;
        private Servo lift;

        private final float LIFT_POSITION_UP = 0.73f;
        private final float LIFT_POSITION_DOWN = 0.42f;
        private final float LIFT_SPEED = 0.01f;

        private final float CLAW_GRAB = 0.23f;
        private final float CLAW_RELEASE = 0.58f;
        private final float CLAW_SPEED = 0.01f;

        private float claw_position = 0;
        private float lift_position = 0;

        public Collector(HardwareMap hardwareMap) {
            claw = hardwareMap.get(Servo.class, "claw");
            lift = hardwareMap.get(Servo.class, "lift");
        }

        double getClawPosition() {
            return claw.getPosition();
        }

        double getLiftPosition() {
            return lift.getPosition();
        }

        void lift(CollectorMovement movement) {
            switch (movement) {
                case UP:
                    lift_position = LIFT_POSITION_UP;
                    break;
                case DOWN:
                    lift_position = LIFT_POSITION_DOWN;
                    break;
                default:
                    break;
            }

            lift.setPosition(lift_position);
        }

        void grab(CollectorMovement movement) {
            switch (movement) {
                case GRAB:
                    claw_position = CLAW_GRAB;
                    break;
                case RELEASE:
                    claw_position = CLAW_RELEASE;
                    break;
            }

            claw.setPosition(claw_position);
        }

        public void displayPositionData(Telemetry telemetry) {
            telemetry.addData("Claw", getClawPosition());
            telemetry.addData("Lift", getLiftPosition());
        }
    }

    public enum CollectorMovement {
        UP,
        DOWN,
        GRAB,
        RELEASE
    }
}