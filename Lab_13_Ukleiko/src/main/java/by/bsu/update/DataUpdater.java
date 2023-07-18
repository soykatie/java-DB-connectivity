package by.bsu.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataUpdater {
    private Connection connection;

    public DataUpdater(Connection connection) {
        this.connection = connection;
    }

    public void addPerson(String fullName, String birthDate) throws SQLException {
        String sql = "INSERT INTO people(full_name, birth_date) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName);
            statement.setString(2, birthDate);
            int updatedRows = statement.executeUpdate();
            System.out.println(updatedRows + " добавлены строки в таблицу people");
        }
    }

    public void addEmail(String senderFullName, String receiverFullName, String topic, String text) throws SQLException {
        String sql = "INSERT INTO emails(sender_id, receiver_id, topic, text, send_date) " +
                "SELECT s.id AS sender_id, r.id AS receiver_id, ?, ?, CURRENT_TIMESTAMP " +
                "FROM people s JOIN people r ON s.full_name = ? AND r.full_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, topic);
            statement.setString(2, text);
            statement.setString(3, senderFullName);
            statement.setString(4, receiverFullName);
            int updatedRows = statement.executeUpdate();
            System.out.println(updatedRows + " добавлены строки в таблицу emails");
        }
    }
}