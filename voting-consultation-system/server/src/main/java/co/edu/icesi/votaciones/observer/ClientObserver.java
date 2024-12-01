package co.edu.icesi.votaciones.observer;

public interface ClientObserver {
    void update(String message);
    String getClientId();
}
