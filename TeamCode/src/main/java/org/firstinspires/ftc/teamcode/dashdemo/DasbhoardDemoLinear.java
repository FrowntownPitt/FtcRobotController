package org.firstinspires.ftc.teamcode.dashdemo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedList;
import java.util.List;

@TeleOp(name="Dashboard Demo Linear", group = "Demo")
public class DasbhoardDemoLinear extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();
//        dashboardTelemetry.setMsTransmissionInterval(100);
        telemetry = new MultipleTelemetry(telemetry, dashboardTelemetry);

        long lastTime = System.currentTimeMillis();
        List<Long> times = new LinkedList<>();
        int sum = 0;

        for (int i=0; i<10; i++) {
            times.add(0L);
        }

        waitForStart();

        while (opModeIsActive()) {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - lastTime;

            Long pop = times.remove(0);
            sum -= pop;
            sum += diff;
            times.add(diff);

            lastTime = currentTime;
            telemetry.addData("time", diff);
            telemetry.addData("averageTime", sum / times.size());
            telemetry.update();
        }
    }
}
