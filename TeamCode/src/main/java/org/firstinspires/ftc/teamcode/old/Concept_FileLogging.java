package org.firstinspires.ftc.teamcode.old;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import static org.firstinspires.ftc.teamcode.old.Concept_FileLogging.OP_MODE_NAME;

@TeleOp(name = OP_MODE_NAME, group = "Concept")
public class Concept_FileLogging extends OpMode {
    static final String OP_MODE_NAME = "Concept File Logger";

    private File dataFile;
    private FileOutputStream dataFileStream;

    @Override
    public void init() {
        try {
            createFile(OP_MODE_NAME);
        } catch (FileNotFoundException e) {
            telemetry.addData("File exception", "cannot open file: " + e.getMessage());
        } catch (IOException e) {
            telemetry.addData("File exception", "cannot open file: " + e.getMessage());
        }
        telemetry.addData("File location", dataFile.getAbsolutePath());
        telemetry.update();
    }

    @Override
    public void loop() {
        try {
            String data = getRuntime() + "\n";
            writeToFile(data);
            telemetry.addData("File write", "Success", data);
        } catch (IOException e) {
            telemetry.addData("File exception", "cannot write to file" + e.getMessage());
            telemetry.update();
        }
    }

    @Override
    public void stop() {
        try {
            closeFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close file");
        }
    }

    private void closeFile() throws IOException {
        dataFileStream.close();
    }

    private void createFile(String filename) throws IOException {
        AppUtil.getInstance().ensureDirectoryExists(AppUtil.ROBOT_DATA_DIR);

        int fileNumber = 0;
        dataFile = new File(AppUtil.ROBOT_DATA_DIR,
                String.format(Locale.getDefault(), "%s-%d.csv", filename, fileNumber));
        while (dataFile.exists()) {
            fileNumber += 1;
            dataFile = new File(AppUtil.ROBOT_DATA_DIR,
                    String.format(Locale.getDefault(), "%s-%d.csv", filename, fileNumber));
        }

        dataFile.createNewFile();

        dataFileStream = new FileOutputStream(dataFile);
    }

    private void writeToFile(String data) throws IOException {
        dataFileStream.write(data.getBytes());
    }
}
