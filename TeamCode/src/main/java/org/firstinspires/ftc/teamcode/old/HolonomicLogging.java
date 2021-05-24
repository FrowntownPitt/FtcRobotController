package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Locale;

@TeleOp(name = "Spyder Logger", group = "Spyder")
public class HolonomicLogging extends OpMode {

    private RobotConfiguration robot;
    private DataLogger localizationLogger;

    public void init() {
        robot = new RobotConfiguration(hardwareMap);
        robot.initializeIMU();

        localizationLogger = new DataLogger("Holonomic_Encoders", telemetry);
    }

    public void start() {
        robot.updateIMUAngles();
        robot.initLocalizer();
    }

    public void loop() {
        robot.updateIMUAngles();

        robot.displayIMUData(telemetry);

        if (gamepad1.dpad_left) {
            robot.resetIMUOffset();
        }

        robot.setPowerFromJoysticks(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x);

        String dataLine = formatEncoderOutput(robot.getHeading(), robot.getDriveEncoderValues());
        localizationLogger.writeToFile(dataLine);

        telemetry.update();
    }

    private String formatEncoderOutput(double heading, int[] encoderValues) {
        return (String.format(Locale.getDefault(),
                "%f,%f,%d,%d,%d,%d\n", getRuntime(), heading, encoderValues[0], encoderValues[1], encoderValues[2], encoderValues[3]));
    }

    public void stop() {
        localizationLogger.closeFile();
    }
}
