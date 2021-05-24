package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by frown on 6/30/2017.
 */

@TeleOp(name="Spyder Claw", group="Spyder")
public class HolonomicClaw extends OpMode {

    private RobotConfiguration robot;

    public void init(){
        robot = new RobotConfiguration(hardwareMap);

        robot.initializeIMU();
    }

    public void start(){
        robot.updateIMUAngles();
    }

    public void loop(){
        robot.updateIMUAngles();

        if(gamepad1.dpad_left){
            robot.resetIMUOffset();
            telemetry.addData("IMU", "RESET");
        }

        robot.moveCollectorFromGamepad(gamepad1);
        robot.setPowerFromJoysticks(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x);

        robot.displayIMUData(telemetry);
        robot.collector.displayPositionData(telemetry);
        telemetry.update();
    }
}
