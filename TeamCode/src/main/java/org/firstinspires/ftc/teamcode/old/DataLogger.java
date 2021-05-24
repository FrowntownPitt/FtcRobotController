package org.firstinspires.ftc.teamcode.old;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class DataLogger {
    private File dataFile;
    private FileOutputStream dataFileStream;

    private Telemetry telemetry;

    public DataLogger(String filename, Telemetry telemetry) {
        this.telemetry = telemetry;

        try {
            createFile(filename);
        } catch (IOException e) {
            telemetry.addData("File exception", "cannot open file: " + e.getMessage());
        }
        telemetry.addData("File location", dataFile.getAbsolutePath());
        telemetry.update();
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

    public void writeToFile(String data) {
        try {
            dataFileStream.write(data.getBytes());
        } catch (IOException e) {
            telemetry.addData("File exception", "cannot write to file" + e.getMessage());
        }
    }

    public void closeFile() {
        try {
            dataFileStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close file");
        }
    }
}
