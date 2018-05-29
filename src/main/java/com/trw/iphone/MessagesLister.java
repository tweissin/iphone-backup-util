package com.trw.iphone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MessagesLister {
    public static void main(String[] args) throws ClassNotFoundException {
        new MessagesLister().run2();
    }

    private void run() {

        String url = "jdbc:sqlite:/Users/tweissin/dev/iphone-backup-util/3d0d7e5fb2ce288813306e4d4636395e047a3d28";
        String query = "SELECT \n" +
                "  message.*,\n" +
                "  handle.id as sender_name\n" +
                "FROM chat_message_join \n" +
                "INNER JOIN message \n" +
                "  ON message.rowid = chat_message_join.message_id \n" +
                "INNER JOIN handle\n" +
                "  ON handle.rowid = message.handle_id\n" +
                "WHERE chat_message_join.chat_id = 1033";
        try (
                Connection conn=DriverManager.getConnection(url);
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(query)
        ) {
            while (rs.next()) {
                String message = rs.getString("text");
                String sender = rs.getString("sender_name");
                System.out.println(message + " " + sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void run2() {

        String url = "jdbc:sqlite:/Users/tweissin/dev/iphone-backup-util/3d0d7e5fb2ce288813306e4d4636395e047a3d28";
        String query = "SELECT \n" +
                "  message.*,\n" +
                "  handle.id as sender_name\n" +
                "FROM chat_message_join \n" +
                "INNER JOIN message \n" +
                "  ON message.rowid = chat_message_join.message_id \n" +
                "INNER JOIN handle\n" +
                "  ON handle.rowid = message.handle_id\n" +
                "WHERE chat_message_join.chat_id = ?";
        try (
                Connection conn=DriverManager.getConnection(url);
                Statement statement = conn.createStatement();
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet chatResultSet = statement.executeQuery("SELECT ROWID FROM chat");
        ) {
            int maxChatCount = 200;
            int maxMessageCount = 200;
            int chatCount = 0;
            while (chatResultSet.next()) {
                String chatId = chatResultSet.getString("ROWID");
                System.out.println("=-=-= Messages for chat ID " + chatId + " =-=-=");
                preparedStatement.setString(1, chatId);

                int messageCount = 0;
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        if (messageCount==1) {
                            chatCount++;
                        }
                        String message = rs.getString("text");
                        String sender = rs.getString("sender_name");
                        System.out.println("chatCount: " + chatCount + ": messageCount: " + messageCount + ": " + message + " " + sender);
                        if (messageCount++==maxMessageCount) {
                            break;
                        }
                    }
                }

                if (chatCount==maxChatCount) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
