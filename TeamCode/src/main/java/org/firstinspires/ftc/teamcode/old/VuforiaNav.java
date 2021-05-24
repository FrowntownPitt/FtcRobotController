package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.teamcode.old.RobotConfiguration.MM_PER_INCH;

@TeleOp(name="VuforiaNav", group="Generic")
public class VuforiaNav extends LinearOpMode {

    private VuforiaConfiguration vuforiaConfiguration;

    private VuforiaLocalizer.CameraDirection cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
    private OpenGLMatrix robotLastKnownLocation;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VuforiaConfiguration.VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforiaLocalizer = ClassFactory.getInstance().createVuforia(parameters);

        vuforiaConfiguration = new VuforiaConfiguration(cameraDirection, vuforiaLocalizer);

        vuforiaConfiguration.LoadNavigationTargets();
        vuforiaConfiguration.EnableNavigationTargets();

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        while(opModeIsActive()) {
            boolean targetIsVisible;
            OpenGLMatrix robotLocation;

            targetIsVisible = vuforiaConfiguration.TargetIsVisible();
            if (targetIsVisible) {
                robotLocation = vuforiaConfiguration.GetRobotLocation();
                if(robotLocation != null) {
                    robotLastKnownLocation = robotLocation;
                }

                VectorF translation = robotLastKnownLocation.getTranslation();
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        translation.get(0) / MM_PER_INCH, translation.get(1) / MM_PER_INCH, translation.get(2) / MM_PER_INCH);

                Orientation rotation = Orientation.getOrientation(robotLastKnownLocation, EXTRINSIC, XYZ, DEGREES);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
            }
            else {
                telemetry.addData("Visible Target", "none");
            }

            telemetry.update();
        }

        vuforiaConfiguration.DisableNavigationTargets();
    }
}
