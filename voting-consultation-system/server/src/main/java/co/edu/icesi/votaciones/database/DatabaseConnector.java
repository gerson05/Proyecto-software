package co.edu.icesi.votaciones.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());
    private static final String DB_URL = "jdbc:postgresql://hgrid2:5432/votaciones";
    private static final String USER = "postgres";
    private static final String PASS = "postgres";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public String findVotingTable(String documentId) {
        String votingTable = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT mesa_id FROM ciudadano WHERE documento = ?")) {

            pstmt.setString(1, documentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    votingTable = rs.getString("mesa_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error finding voting table", e);
        }
        return votingTable;
    }

    //quitar luego
    public static void main(String[] args) {
        // Ejemplo básico para probar el método findVotingTable
        DatabaseConnector connector = new DatabaseConnector();

        // Documento a buscar en la tabla
        String testDocumentId = "527760767";

        // Llamar al método y obtener el resultado
        String mesaDeVotacion = connector.findVotingTable(testDocumentId);

        // Mostrar el resultado en consola
        if (mesaDeVotacion != null) {
            System.out.println("La mesa de votación para el documento " + testDocumentId + " es: " + mesaDeVotacion);
        } else {
            System.out.println("No se encontró una mesa de votación para el documento " + testDocumentId);
        }
    }
}