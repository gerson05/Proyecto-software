package co.edu.icesi.votaciones.observer;

import java.util.ArrayList;
import java.util.List;

public class ObserverManager {
    private List<ClientObserver> observers = new ArrayList<>();

    public void registerObserver(ClientObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unregisterObserver(ClientObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (ClientObserver observer : observers) {
            observer.update(message);
        }
    }

    public int getObserverCount() {
        return observers.size();
    }

    public List<ClientObserver> getObservers() {
        return new ArrayList<>(observers);
    }
}