package co.edu.icesi.votaciones.masterworker;



import co.edu.icesi.votaciones.database.DatabaseConnector;
import co.edu.icesi.votaciones.observer.ClientObserver;
import co.edu.icesi.votaciones.utils.LoggerUtil;
import co.edu.icesi.votaciones.utils.PrimeFactorizer;

import java.util.List;
import java.util.ArrayList;

import java.util.logging.Logger;

public class WorkerTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(WorkerTask.class.getName());

    private ClientObserver client;
    private List<String> documentIds;
    private DatabaseConnector dbConnector;

    public WorkerTask(ClientObserver client, List<String> documentIds) {
        this.client = client;
        this.documentIds = documentIds;
        this.dbConnector = new DatabaseConnector();
    }

    @Override
    public void run() {
        List<String> resultLogs = new ArrayList<>();

        for (String documentId : documentIds) {
            long startTime = System.currentTimeMillis();

            // Find voting table
            String votingTable = dbConnector.findVotingTable(documentId);

            // Calculate prime factors
            int primeFactorsCount = PrimeFactorizer.countPrimeFactors(Long.parseLong(documentId));
            int isPrimeFactorsPrime = PrimeFactorizer.isPrime(primeFactorsCount) ? 1 : 0;

            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            // Create log entry
            String logEntry = String.format("%s,%s,%d,%d",
                    documentId,
                    votingTable != null ? votingTable : "N/A",
                    isPrimeFactorsPrime,
                    processingTime
            );

            resultLogs.add(logEntry);

            // Notify client with result
            client.update(logEntry);
        }

        // Log results
        LoggerUtil.logClientResults(client.getClientId(), resultLogs);
    }
}
