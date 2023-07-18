package by.bsu.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryExecutor {
    private Connection connection;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
    }

    public void findUserWithMinimalEmailLength() throws SQLException {
        String sql = "SELECT p.full_name, MIN(CHAR_LENGTH(e.text)) AS min_length " +
                "FROM people p JOIN emails e ON p.id = e.sender_id " +
                "GROUP BY p.id " +
                "ORDER BY min_length " +
                "LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String fullName = resultSet.getString("full_name");
                int minLength = resultSet.getInt("min_length");
                System.out.printf("Пользователь, длина писем которого наименьшая: %s (длина = %d)%n", fullName, minLength);
            } else {
                System.out.println("Пользователи не найдены.");
            }
        }
    }

    public void getUsersWithEmailCounts() throws SQLException {
        String sql = "SELECT p.full_name, " +
                "(SELECT COUNT(*) FROM emails WHERE sender_id = p.id) AS sent_count, " +
                "(SELECT COUNT(*) FROM emails WHERE receiver_id = p.id) AS received_count " +
                "FROM people p";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Информация о пользователях, а также о кол-ве полученных/отправленных писем:");
            while (resultSet.next()) {
                String fullName = resultSet.getString("full_name");
                int sentCount = resultSet.getInt("sent_count");
                int receivedCount = resultSet.getInt("received_count");
                System.out.printf("%s: кол-во отправленных писем = %d, кол-во полученных писем = %d%n", fullName, sentCount, receivedCount);
            }
        }
    }

    public void getUsersWithTopicEmails(String topic) throws SQLException {
        String sql = "SELECT DISTINCT p.full_name " +
                "FROM people p JOIN emails e ON p.id = e.receiver_id " +
                "WHERE e.topic = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, topic);
            ResultSet resultSet = statement.executeQuery();
            System.out.printf("Информация о пользователях, которые получили хотя бы 1 сообщение с темой '%s':%n", topic);
            while (resultSet.next()) {
                String fullName = resultSet.getString("full_name");
                System.out.println(fullName);
            }
        }
    }

    public void getUsersWithoutTopicEmails(String topic) throws SQLException {
        String sql = "SELECT DISTINCT p.full_name " +
                "FROM people p LEFT JOIN emails e ON p.id = e.receiver_id AND e.topic = ? " +
                "WHERE e.id IS NULL";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, topic);
            ResultSet resultSet = statement.executeQuery();
            System.out.printf("Информация о пользователях, которые не получали сообщения с темой '%s':%n", topic);
            while (resultSet.next()) {
                String fullName = resultSet.getString("full_name");
                System.out.println(fullName);
            }
        }
    }

    public void sendEmailToRecipients(String senderFullName, String topic, String text) throws SQLException {
        String sql = "INSERT INTO emails(sender_id, receiver_id, topic, text, send_date) " +
                "SELECT s.id AS sender_id, r.id AS receiver_id, ?, ?, CURRENT_TIMESTAMP " +
                "FROM people s JOIN people r ON s.full_name <> r.full_name";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, topic);
            statement.setString(2, text);
            int updatedRows = statement.executeUpdate();
            System.out.printf("Письмо %s к %d адресатам%n", senderFullName, updatedRows);
        }
    }
}
