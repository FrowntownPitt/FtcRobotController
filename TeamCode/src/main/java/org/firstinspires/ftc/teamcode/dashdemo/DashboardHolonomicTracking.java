package org.firstinspires.ftc.teamcode.dashdemo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.old.DataLogger;
import org.firstinspires.ftc.teamcode.old.RobotConfiguration;
import org.firstinspires.ftc.teamcode.old.RobotLocalizer;

import java.util.Locale;

@TeleOp(name = "Spyder Odometry Dashboard", group = "Dashboard")
public class DashboardHolonomicTracking extends OpMode {
    public static final String VUFORIA_LICENSE_KEY = "AQmD/ND/////AAAAGc1t61ZITEyugHepydtgm1Y0FfRGgyMpDqFmHFYfkIEnJgLFu1dgPisVPl3D2PQUpn2kv58WXXGCUnryLHfbeJFapwBfVMkqlfPCbPJRXn0A1uuf4x2KiUb+DtiKqNX0flLtxt7L9azPhx6An2hOA6atHgofp68cII1ZD8vNukmQiquv3vEjX7k2olvtIKosBGYTY2ti/0Sa6Gii3NM4JPmQbusiJjz55V+m9R85+gsTwQHxoFq56QB9dtrV2gf+uhndrHhPEmVgqvvsGwrBjsYTmjlVeekAed63QER4mZMACMt7wXl4DqwxaWl/Jg0lF0rtTyNK2lMEGtBoKuewMMHe4NSkqWi3TJ2B8n7R6ghM";
    private static final double RADIUS = 1;
    private static final String BLACK = "black";

    private RobotConfiguration robot;
    private DataLogger localizationLogger;
    private FtcDashboard dashboard;

    public void init() {
        robot = new RobotConfiguration(hardwareMap);
        robot.initializeIMU();

        localizationLogger = new DataLogger("Holonomic_Tracking", telemetry);

        dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, dashboardTelemetry);

        msStuckDetectStop = 2500;

        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        vuforiaParams.vuforiaLicenseKey = VUFORIA_LICENSE_KEY;
        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(vuforiaParams);

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);
    }

    public void start() {
        robot.updateIMUAngles();
        robot.initLocalizer();
    }

    public void loop() {
        robot.updateIMUAngles();
        robot.updateLocalizer();


        if (gamepad1.dpad_left) {
            robot.resetIMUOffset();
        }

        robot.setPowerFromJoysticks(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x);


        String dataLine = formatLocalizationOutput(robot.getHeading(), robot.getDriveEncoderValues(), robot.getFieldLocation());
        localizationLogger.writeToFile(dataLine);

        robot.displayIMUData(telemetry);
        robot.displayFieldLocation(telemetry);

        RobotLocalizer.Location fieldLocation = robot.getFieldLocation();
        RobotLocalizer.Location robotCenter = robot.getRobotCenter();
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("heading", robot.getHeading());
        packet.put("offset", robot.getHeadingOffset());
        packet.put("X", robotCenter.getX());
        packet.put("Y", robotCenter.getY());
        packet.put("imu x", fieldLocation.getX());
        packet.put("imu y", fieldLocation.getY());

        packet.fieldOverlay()
                .setStrokeWidth(1)
                .strokeCircle(fieldLocation.getX(), fieldLocation.getY(), RADIUS)
                .setFill(BLACK);

        dashboard.sendTelemetryPacket(packet);

    }

    private String formatLocalizationOutput(double heading, int[] encoderValues, RobotLocalizer.Location fieldLocation) {
        return (String.format(Locale.getDefault(),
                "%f,%f,%d,%d,%d,%d, %f,%f\n", getRuntime(), heading,
                encoderValues[0], encoderValues[1], encoderValues[2], encoderValues[3],
                fieldLocation.getX(), fieldLocation.getY()));
    }

    public void stop() {
        localizationLogger.closeFile();
    }
}
