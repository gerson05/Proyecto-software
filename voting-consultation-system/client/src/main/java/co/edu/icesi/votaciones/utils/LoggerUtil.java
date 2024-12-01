package co.edu.icesi.votaciones.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
    private static final Logger LOGGER = Logger.getLogger(LoggerUtil.class.getName());
    private static final String CLIENT_LOG_FILE = "client_log.csv";

    public static void logClientQuery(String clientId, String documentId,
                                      String votingTable, long processingTime) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENT_LOG_FILE, true))) {
            writer.println(String.format("%s,%s,%s,%d",
                    clientId, documentId, votingTable, processingTime));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to client log", e);
        }
    }
}
