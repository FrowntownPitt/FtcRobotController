package org.firstinspires.ftc.teamcode.old;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

public class Configuration {

    public static class Vuforia {
        public static final String VUFORIA_LICENSE = "AQmD/ND/////AAAAGc1t61ZITEyugHepydtgm1Y0FfRGgyMpDqFmHFYfkIEnJgLFu1dgPisVPl3D2PQUpn2kv58WXXGCUnryLHfbeJFapwBfVMkqlfPCbPJRXn0A1uuf4x2KiUb+DtiKqNX0flLtxt7L9azPhx6An2hOA6atHgofp68cII1ZD8vNukmQiquv3vEjX7k2olvtIKosBGYTY2ti/0Sa6Gii3NM4JPmQbusiJjz55V+m9R85+gsTwQHxoFq56QB9dtrV2gf+uhndrHhPEmVgqvvsGwrBjsYTmjlVeekAed63QER4mZMACMt7wXl4DqwxaWl/Jg0lF0rtTyNK2lMEGtBoKuewMMHe4NSkqWi3TJ2B8n7R6ghM";

        static final int CAMERA_FORWARD_DISPLACEMENT  = 0;   // eg: Camera is 110 mm in front of robot center
        static final int CAMERA_VERTICAL_DISPLACEMENT = 0;   // eg: Camera is 200 mm above ground
        static final int CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        static final float MM_PER_INCH = 25.4f;
        static final float MM_FTC_FIELD_WIDTH = (12*6) * MM_PER_INCH;       // the width of the FTC field (from the center point to the outer panels)
        static final float MM_TARGET_HEIGHT = (6) * MM_PER_INCH;          // the height of the center of the target image above the floor

        static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;

        final VuforiaLocalizer.Parameters VUFORIA_PARAMETERS;
        final int cameraMonitorViewId;

        public Vuforia(int cameraMonitorViewId){
            this.cameraMonitorViewId = cameraMonitorViewId;

            VUFORIA_PARAMETERS = createVuforiaParameters(this.cameraMonitorViewId);
        }

        private VuforiaLocalizer.Parameters createVuforiaParameters(int cameraMonitorViewId) {
            VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

            parameters.vuforiaLicenseKey = VUFORIA_LICENSE;
            parameters.cameraDirection   = CAMERA_CHOICE;
            return parameters;
        }

        public OpenGLMatrix orientPhoneOnRobot() {
            return OpenGLMatrix
                    .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                    .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                            CAMERA_CHOICE == FRONT ? 90 : -90, 0, 0));
        }

        public static class Navigation {

            public final VuforiaTrackables NAVIGATION_TARGETS;

            public Navigation(VuforiaTrackables navigationTargets){
                this.NAVIGATION_TARGETS = navigationTargets;
            }

            public  List<VuforiaTrackable> setupNavigationTargets(){
                VuforiaTrackable blueRover = NAVIGATION_TARGETS.get(0);
                blueRover.setName("Blue-Rover");
                VuforiaTrackable redFootprint = NAVIGATION_TARGETS.get(1);
                redFootprint.setName("Red-Footprint");
                VuforiaTrackable frontCraters = NAVIGATION_TARGETS.get(2);
                frontCraters.setName("Front-Craters");
                VuforiaTrackable backSpace = NAVIGATION_TARGETS.get(3);
                backSpace.setName("Back-Space");

                OpenGLMatrix blueRoverLocationOnField = OpenGLMatrix
                        .translation(0, MM_FTC_FIELD_WIDTH, MM_TARGET_HEIGHT)
                        .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));
                blueRover.setLocation(blueRoverLocationOnField);

                OpenGLMatrix redFootprintLocationOnField = OpenGLMatrix
                        .translation(0, -MM_FTC_FIELD_WIDTH, MM_TARGET_HEIGHT)
                        .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));
                redFootprint.setLocation(redFootprintLocationOnField);

                OpenGLMatrix frontCratersLocationOnField = OpenGLMatrix
                        .translation(-MM_FTC_FIELD_WIDTH, 0, MM_TARGET_HEIGHT)
                        .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90));
                frontCraters.setLocation(frontCratersLocationOnField);

                OpenGLMatrix backSpaceLocationOnField = OpenGLMatrix
                        .translation(MM_FTC_FIELD_WIDTH, 0, MM_TARGET_HEIGHT)
                        .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));
                backSpace.setLocation(backSpaceLocationOnField);

                return new ArrayList<>(NAVIGATION_TARGETS);
            }

            public void updatePhoneLocation(OpenGLMatrix phoneLocationOnRobot, VuforiaLocalizer.CameraDirection cameraDirection) {
                for (VuforiaTrackable trackable : NAVIGATION_TARGETS)
                {
                    ((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, cameraDirection);
                }
            }
        }

    }
}
