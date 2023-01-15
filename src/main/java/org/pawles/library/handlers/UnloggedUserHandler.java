package org.pawles.library.handlers;

import org.pawles.library.Library;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UnloggedUserHandler implements UserHandler {

    private final Connection connection;

    public UnloggedUserHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void start() {
        while (true) {
            Library.OUT.println("Welcome! Here is a list of publications you can read:\n");
            Library.OUT.printf("%10s | %50s\n", "id", "title");

            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM PublicationPreview");
                while (resultSet.next()) {
                    final int id = resultSet.getInt("id");
                    final String title = resultSet.getString("title");
                    Library.OUT.printf("%10d | %50s\n", id, title);
                }
                Library.OUT.println();
            } catch (SQLException e) {
                Library.ERR.println("SQL error: " + e.getMessage());
            }

            Library.OUT.println("Want more functionality? Consider making an account.");

            String input = Library.IN.nextLine();

            // quit if input = "quit"

            if ("quit".equals(input)) {
                break;
            }
        }
        Library.OUT.println("bye");
    }
}
