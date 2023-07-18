package by.bsu.run;

import by.bsu.connect.DBConnector;
import by.bsu.execute.QueryExecutor;
import by.bsu.update.DataUpdater;

import java.sql.Connection;
import java.sql.SQLException;

/**
 Вариант 4. Письма. В БД хранится информация о письмах и отправляющих их людях.
 Для людей необходимо хранить:
 • ФИО;
 • дату рождения.
 Для писем необходимо хранить:
 • отправителя;
 • получателя;
 • тему письма;
 • текст письма;
 • дату отправки.

 • Найти пользователя, длина писем которого наименьшая.
 • Вывести информацию о пользователях, а также количестве полученных и отправленных ими письмах.
 • Вывести информацию о пользователях, которые получили хотя бы одно сообщение с заданной темой.
 • Вывести информацию о пользователях, которые не получали сообщения с заданной темой.
 • Направить письмо заданного человека с заданной темой всем адресатам.
 */

public class Main {
    public static void main(String[] args) {
        try (Connection connection = new DBConnector().getConnection()) {
            QueryExecutor queryExecutor = new QueryExecutor(connection);
            DataUpdater dataUpdater = new DataUpdater(connection);

            dataUpdater.addPerson("Амрит Дубей", "2000-08-16");
            dataUpdater.addPerson("Рейтан Вайш", "1999-12-12");
            dataUpdater.addPerson("Амала Басу", "2001-11-15");
            dataUpdater.addEmail("Амрит Дубей", "Рейтан Вайш", "Собрание Калигхора", "Рейтан, не опаздывай. Сегодня в 21.00 в резиденции Дубеев. Жду");
            dataUpdater.addEmail("Рейтан Вайш", "Амрит Дубей", "Re: Собрание Калигхора", "Амрит, ок! Увидимся сегодня:)");
            dataUpdater.addEmail("Амала Басу", "Амрит Дубей", "Подготовка к свадьбе", "Когда свадьба, Махарадж?))");
            dataUpdater.addEmail("Амрит Дубей", "Амала Басу", "Re: Подготовка к свадьбе", "Когда разрулим ситуацию с Вималом, Махарани!");

            queryExecutor.findUserWithMinimalEmailLength();
            queryExecutor.getUsersWithEmailCounts();
            queryExecutor.getUsersWithTopicEmails("Собрание Калигхора");
            queryExecutor.getUsersWithoutTopicEmails("Подготовка к свадьбе");
            queryExecutor.sendEmailToRecipients("Дедас Дубей", "Приглашение", "Всем привет! Приезжайте в Калькутту! У нас тут весело!!!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}