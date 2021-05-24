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
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;
import static org.firstinspires.ftc.teamcode.old.RobotConfiguration.MM_FTC_FIELD_WIDTH;
import static org.firstinspires.ftc.teamcode.old.RobotConfiguration.MM_PER_INCH;

public class VuforiaConfiguration {
    public static final String VUFORIA_KEY = "AQmD/ND/////AAAAGc1t61ZITEyugHepydtgm1Y0FfRGgyMpDqFmHFYfkIEnJgLFu1dgPisVPl3D2PQUpn2kv58WXXGCUnryLHfbeJFapwBfVMkqlfPCbPJRXn0A1uuf4x2KiUb+DtiKqNX0flLtxt7L9azPhx6An2hOA6atHgofp68cII1ZD8vNukmQiquv3vEjX7k2olvtIKosBGYTY2ti/0Sa6Gii3NM4JPmQbusiJjz55V+m9R85+gsTwQHxoFq56QB9dtrV2gf+uhndrHhPEmVgqvvsGwrBjsYTmjlVeekAed63QER4mZMACMt7wXl4DqwxaWl/Jg0lF0rtTyNK2lMEGtBoKuewMMHe4NSkqWi3TJ2B8n7R6ghM";
    public final VuforiaLocalizer.CameraDirection CAMERA_CHOICE;
    private final VuforiaLocalizer VUFORIA_LOCALIZER;

    private static final float MM_TARGET_HEIGHT = (6) * MM_PER_INCH;

    private List<VuforiaTrackable> navigationTargets;

    private final int CAMERA_FORWARD_DISPLACEMENT  = 83;   // eg: Camera is 110 mm in front of robot center
    private final int CAMERA_VERTICAL_DISPLACEMENT = 195;   // eg: Camera is 200 mm above ground
    private final int CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

    private OpenGLMatrix phoneLocationOnRobot;
    private VuforiaTrackables roverRuckusTargets;

    public VuforiaConfiguration(VuforiaLocalizer.CameraDirection cameraDirection,
                                VuforiaLocalizer vuforiaLocalizer){
        this.VUFORIA_LOCALIZER = vuforiaLocalizer;
        this.CAMERA_CHOICE = cameraDirection;

        phoneLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        CAMERA_CHOICE == FRONT ? 90 : -90, 0, 0));
    }

    public void LoadNavigationTargets(){

        roverRuckusTargets = this.VUFORIA_LOCALIZER.loadTrackablesFromAsset("RoverRuckus");
        VuforiaTrackable blueRover = roverRuckusTargets.get(0);
        blueRover.setName("Blue-Rover");
        VuforiaTrackable redFootprint = roverRuckusTargets.get(1);
        redFootprint.setName("Red-Footprint");
        VuforiaTrackable frontCraters = roverRuckusTargets.get(2);
        frontCraters.setName("Front-Craters");
        VuforiaTrackable backSpace = roverRuckusTargets.get(3);
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

        navigationTargets = new ArrayList<>(roverRuckusTargets);

        for(VuforiaTrackable trackable : navigationTargets) {
            ((VuforiaTrackableDefaultListener)trackable.getListener())
                    .setPhoneInformation(phoneLocationOnRobot, CAMERA_CHOICE);
        }
    }

    public boolean TargetIsVisible(){
        for(VuforiaTrackable target : navigationTargets){
            if(((VuforiaTrackableDefaultListener)target.getListener()).isVisible()){
                return true;
            }
        }
        return false;
    }

    public OpenGLMatrix GetRobotLocation() {
        for(VuforiaTrackable target : navigationTargets){
            if(((VuforiaTrackableDefaultListener)target.getListener()).isVisible()){
                return ((VuforiaTrackableDefaultListener)target.getListener()).getUpdatedRobotLocation();
            }
        }

        return null;
    }

    public VuforiaTrackableDefaultListener GetVisibleTrackable() {
        for(VuforiaTrackable target : navigationTargets){
            if(((VuforiaTrackableDefaultListener)target.getListener()).isVisible()){
                return ((VuforiaTrackableDefaultListener)target.getListener());
            }
        }

        return null;
    }

    public void EnableNavigationTargets(){
        roverRuckusTargets.activate();
    }

    public void DisableNavigationTargets(){
        roverRuckusTargets.deactivate();
    }

}
