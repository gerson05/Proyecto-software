package co.edu.icesi.votaciones;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import co.edu.icesi.votaciones.utils.LoggerUtil;
import VotingSystem.VotingConsultation;
import VotingSystem.VotingTableInfo;
import VotingSystem.ClientObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;



public class VotingConsultationClient implements ClientObserver {
    private static final Logger LOGGER = Logger.getLogger(VotingConsultationClient.class.getName());

    private final String clientId;
    private final VotingConsultation votingService;
    private final ExecutorService executorService;

    public VotingConsultationClient(Communicator communicator) {
        this.clientId = UUID.randomUUID().toString();
        this.executorService = Executors.newFixedThreadPool(10);

        ObjectPrx base = communicator.stringToProxy("VotingConsultation:default -h localhost -p 10000");
        this.votingService = VotingConsultation.checkedCast(base);

        if (this.votingService == null) {
            throw new RuntimeException("Invalid proxy");
        }

        // Register client with Current parameter
        this.votingService.registerClient(clientId, new Current());
    }

    @Override
    public void notifyConsultationAssignment(String filename, int startLine, int endLine, Current current) {
        // Process assigned consultation range
        List<String> documentIds = generateMockDocumentIds(startLine, endLine);
        processDocumentIds(documentIds);
    }

    @Override
    public void update(String message, Current current) {
        // Handle updates from server
        LOGGER.info("Received update: " + message);
    }

    private List<String> generateMockDocumentIds(int startLine, int endLine) {
        List<String> documentIds = new ArrayList<>();
        for (int i = startLine; i <= endLine; i++) {
            documentIds.add(String.valueOf(1000000 + i));
        }
        return documentIds;
    }

    private void processDocumentIds(List<String> documentIds) {
        for (String documentId : documentIds) {
            executorService.submit(() -> {
                long startTime = System.currentTimeMillis();

                try {
                    // Add Current parameter to consultVotingTable
                    VotingTableInfo result = votingService.consultVotingTable(documentId, new Current());

                    long endTime = System.currentTimeMillis();
                    long processingTime = endTime - startTime;

                    LoggerUtil.logClientQuery(
                            clientId,
                            documentId,
                            result.votingLocation,
                            processingTime
                    );
                } catch (com.zeroc.Ice.Exception e) {
                    // Use specific Ice Exception instead of java.lang.Exception
                    LOGGER.severe("Error processing document: " + documentId);
                }
            });
        }

        executorService.shutdown();
    }

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            VotingConsultationClient client = new VotingConsultationClient(communicator);
            communicator.waitForShutdown();
        }
    }
}
