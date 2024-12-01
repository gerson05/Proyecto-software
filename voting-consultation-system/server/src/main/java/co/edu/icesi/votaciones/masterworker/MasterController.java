package co.edu.icesi.votaciones.masterworker;

import co.edu.icesi.votaciones.observer.ClientObserver;
import co.edu.icesi.votaciones.observer.ObserverManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MasterController {
    private ObserverManager observerManager;
    private ExecutorService executorService;

    public MasterController(ObserverManager observerManager) {
        this.observerManager = observerManager;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void distributeWorkload(List<String> documentIds) {
        List<ClientObserver> clients = observerManager.getObservers();
        int totalClients = clients.size();

        if (totalClients == 0) {
            // Process locally if no clients registered
            processLocally(documentIds);
            return;
        }

        int documentsPerClient = documentIds.size() / totalClients;
        int extraDocuments = documentIds.size() % totalClients;

        int startIndex = 0;
        for (int i = 0; i < totalClients; i++) {
            int clientLoad = documentsPerClient + (i < extraDocuments ? 1 : 0);
            List<String> clientDocuments = documentIds.subList(startIndex, startIndex + clientLoad);

            WorkerTask task = new WorkerTask(clients.get(i), clientDocuments);
            executorService.submit(task);

            startIndex += clientLoad;
        }
    }

    private void processLocally(List<String> documentIds) {
        // Implement local processing logic
        // Similar to WorkerTask but executed in the server
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
