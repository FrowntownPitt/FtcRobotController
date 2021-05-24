package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;

import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.teamcode.old.Configuration.Vuforia.MM_PER_INCH;

@TeleOp(name = "Holonomic with Vuforia Tracking", group = "Spyder")
public class HolonomicVuforiaNav extends OpMode {
    private OpenGLMatrix lastLocation = null;
    private boolean targetVisible = false;

    private VuforiaLocalizer vuforia;
    private List<VuforiaTrackable> allTrackables;

    private RobotConfiguration robot;

    @Override
    public void init() {
        robot = new RobotConfiguration(hardwareMap);
        robot.initializeIMU();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        Configuration.Vuforia vuforiaConfiguration = new Configuration.Vuforia(cameraMonitorViewId);
        OpenGLMatrix phoneLocationOnRobot = vuforiaConfiguration.orientPhoneOnRobot();

        vuforia = ClassFactory.getInstance().createVuforia(vuforiaConfiguration.VUFORIA_PARAMETERS);

        Configuration.Vuforia.Navigation navigationConfiguration = new Configuration.Vuforia.Navigation(this.vuforia.loadTrackablesFromAsset("RoverRuckus"));

        allTrackables = navigationConfiguration.setupNavigationTargets();

        navigationConfiguration.updatePhoneLocation(phoneLocationOnRobot, vuforiaConfiguration.VUFORIA_PARAMETERS.cameraDirection);

        navigationConfiguration.NAVIGATION_TARGETS.activate();
    }

    @Override
    public void start() {
        robot.updateIMUAngles();
    }

    @Override
    public void loop() {
        robot.updateIMUAngles();

        if(gamepad1.dpad_left){
            robot.resetIMUOffset();
        }

        robot.displayIMUData(telemetry);

        double rotation_adjust = 1;
        if(gamepad1.right_bumper) rotation_adjust = 0.3;

        double power_adjust = 1;
        if(gamepad1.left_bumper) power_adjust = 0.3;

        robot.setPowerFromJoysticks(power_adjust * gamepad1.left_stick_x, power_adjust * -gamepad1.left_stick_y, rotation_adjust * gamepad1.right_stick_x);


        // check all the trackable target to see which one (if any) is visible.
        targetVisible = false;
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                telemetry.addData("Visible Target", trackable.getName());
                targetVisible = true;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                break;
            }
        }

        // Provide feedback as to where the robot is located (if we know).
        if (targetVisible) {
            // express position (translation) of robot in inches.
            VectorF translation = lastLocation.getTranslation();
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / MM_PER_INCH, translation.get(1) / MM_PER_INCH, translation.get(2) / MM_PER_INCH);

            // express the rotation of the robot in degrees.
            Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
            telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
        }
        else {
            telemetry.addData("Visible Target", "none");
        }
        telemetry.update();
    }
}
