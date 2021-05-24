package org.firstinspires.ftc.teamcode.dashdemo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@TeleOp(name = "Dashboard Demo", group = "Demo")
public class DashboardDemo extends OpMode {

    public static final String VUFORIA_LICENSE_KEY = "AQmD/ND/////AAAAGc1t61ZITEyugHepydtgm1Y0FfRGgyMpDqFmHFYfkIEnJgLFu1dgPisVPl3D2PQUpn2kv58WXXGCUnryLHfbeJFapwBfVMkqlfPCbPJRXn0A1uuf4x2KiUb+DtiKqNX0flLtxt7L9azPhx6An2hOA6atHgofp68cII1ZD8vNukmQiquv3vEjX7k2olvtIKosBGYTY2ti/0Sa6Gii3NM4JPmQbusiJjz55V+m9R85+gsTwQHxoFq56QB9dtrV2gf+uhndrHhPEmVgqvvsGwrBjsYTmjlVeekAed63QER4mZMACMt7wXl4DqwxaWl/Jg0lF0rtTyNK2lMEGtBoKuewMMHe4NSkqWi3TJ2B8n7R6ghM";

    private long lastTime;
    private DcMotor motor1;


    @Override
    public void init() {
        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, dashboardTelemetry);

        motor1 = hardwareMap.get(DcMotor.class, "frontMotor");

        msStuckDetectStop = 2500;

        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        vuforiaParams.vuforiaLicenseKey = VUFORIA_LICENSE_KEY;
        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(vuforiaParams);

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);
    }

    @Override
    public void loop() {
        motor1.setPower(gamepad1.left_stick_x);

        long currentTime = System.currentTimeMillis();
        long diff = currentTime - lastTime;

        lastTime = currentTime;
        telemetry.addData("time", diff);
        telemetry.update();
    }
}
