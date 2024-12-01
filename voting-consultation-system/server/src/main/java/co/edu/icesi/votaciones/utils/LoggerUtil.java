package co.edu.icesi.votaciones.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
    private static final Logger LOGGER = Logger.getLogger(LoggerUtil.class.getName());
    private static final String SERVER_LOG_FILE = "server_log.csv";

    public static void logClientResults(String clientId, List<String> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SERVER_LOG_FILE, true))) {
            for (String result : results) {
                writer.println(clientId + "," + result);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to server log", e);
        }
    }

    public static void logTotalConsultations(int totalConsultations, long totalExecutionTime) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SERVER_LOG_FILE, true))) {
            writer.println("Total Consultations: " + totalConsultations +
                    ", Total Execution Time: " + totalExecutionTime + " ms");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing total consultations", e);
        }
    }
}
