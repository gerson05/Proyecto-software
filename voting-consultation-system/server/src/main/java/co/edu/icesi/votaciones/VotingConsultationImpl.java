package co.edu.icesi.votaciones;

import VotingSystem.*;
import com.zeroc.Ice.*;
import co.edu.icesi.votaciones.database.DatabaseConnector;
import co.edu.icesi.votaciones.utils.PrimeFactorizer;
import co.edu.icesi.votaciones.utils.LoggerUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class VotingConsultationImpl implements VotingConsultation {
    private static final Logger LOGGER = Logger.getLogger(VotingConsultationImpl.class.getName());
    private final DatabaseConnector dbConnector;
    private final ExecutorService executorService;
    private final List<ClientObserver> registeredClients;

    public VotingConsultationImpl() {
        this.dbConnector = new DatabaseConnector();
        this.executorService = Executors.newFixedThreadPool(10);
        this.registeredClients = new CopyOnWriteArrayList<>();
    }

    @Override
    public VotingTableInfo consultVotingTable(String documentId, Current current) {
        long startTime = System.currentTimeMillis();

        String votingTable = dbConnector.findVotingTable(documentId);
        long primeFactorsCount = PrimeFactorizer.countPrimeFactors(Long.parseLong(documentId));
        boolean isPrimeFactorsCountPrime = PrimeFactorizer.isPrime((int)primeFactorsCount);

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        return new VotingTableInfo(documentId,
                votingTable != null ? votingTable : "N/A",
                (int)primeFactorsCount,
                isPrimeFactorsCountPrime,
                responseTime);
    }

    @Override
    public void registerClient(String clientId, Current current) {
        LOGGER.info("Client registered: " + clientId);
        // Implement client registration logic if needed
    }

    @Override
    public void distributeConsultation(String filename, Current current) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> documentIds = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                documentIds.add(line.trim());
            }

            int clientCount = registeredClients.size();
            if (clientCount == 0) {
                processLocally(documentIds);
            } else {
                distributeToClients(documentIds);
            }
        } catch (IOException e) {
            LOGGER.severe("Error reading file: " + e.getMessage());
        }
    }

    private void processLocally(List<String> documentIds) {
        for (String documentId : documentIds) {
            VotingTableInfo result = consultVotingTable(documentId, null);
            LoggerUtil.logTotalConsultations(documentIds.size(), result.responseTime);
        }
    }

    private void distributeToClients(List<String> documentIds) {
        int totalClients = registeredClients.size();
        int documentsPerClient = documentIds.size() / totalClients;
        int extraDocuments = documentIds.size() % totalClients;

        int startIndex = 0;
        for (int i = 0; i < totalClients; i++) {
            int clientLoad = documentsPerClient + (i < extraDocuments ? 1 : 0);
            List<String> clientDocuments = documentIds.subList(startIndex, startIndex + clientLoad);

            // Notify client about its assigned documents
            registeredClients.get(i).notifyConsultationAssignment(
                    "documentIds.txt", startIndex, startIndex + clientLoad - 1, null
            );

            startIndex += clientLoad;
        }
    }

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                    "VotingConsultationAdapter",
                    "default -p 10000"
            );

            VotingConsultation votingService = new VotingConsultationImpl();
            adapter.add(votingService, Util.stringToIdentity("VotingConsultation"));

            adapter.activate();
            communicator.waitForShutdown();
        }
    }
}