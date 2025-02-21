package org.wso2.carbon.si.metrics.icp.reporter.utiils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.istack.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static JsonArray getLogFileList() {
        String carbonHome = System.getProperty("carbon.home");
        if (carbonHome == null) {
            return null;
        }

        JsonArray logFiles = new JsonArray();
        Path logDirPath = Paths.get(carbonHome, "wso2", "server", "logs");
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(logDirPath, "*.log")) {
            for (Path path : paths) {
                JsonObject logfileObject = new JsonObject();
                logfileObject.addProperty("FileName", path.getFileName().toString());
                logfileObject.addProperty("Size", getFileSize(path.toFile()));
                logFiles.add(logfileObject);
            }
        } catch (IOException e) {
            log.error("Could not find the log directory : {}", logDirPath, e);
        }
        return logFiles;
    }


    private static String getFileSize(File file) {
        long bytes = file.length();
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static DataHandler getLogFiles(String fileName) {
        String carbonHome = System.getProperty("carbon.home");
        if (carbonHome == null) {
            return null;
        }

        Path logFilePath = Paths.get(carbonHome, "wso2", "server", "logs", fileName);
        return readFile(logFilePath.toString());
    }

    private static DataHandler readFile(String logFilePath) {
        File file = new File(logFilePath);
        if (file.exists() && !file.isDirectory()) {
            FileDataSource fileDataSource = new FileDataSource(file);
            return new DataHandler(fileDataSource);
        } else {
            log.error("Could not find the requested file : {}", logFilePath);
            return null;
        }
    }
}
