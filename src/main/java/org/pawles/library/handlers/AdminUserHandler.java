package org.pawles.library.handlers;

import org.mindrot.jbcrypt.BCrypt;
import org.pawles.library.Library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;

public class AdminUserHandler implements UserHandler {
    Connection connection;

    private void createUser() throws SQLException {
        PreparedStatement createUserQuery = connection.prepareStatement("CREATE USER '?'@'localhost' IDENTIFIED BY ?");
        Library.OUT.println("username: ");
        final String username = Library.IN.nextLine();
        createUserQuery.setString(1, username);
        Library.OUT.println("password: ");
        final String password = Library.IN.nextLine(); // TODO no echo
        createUserQuery.setString(2, password);
        createUserQuery.execute();
    }

    private void addAccount() throws SQLException {
        PreparedStatement addAccountQuery = connection.prepareStatement("INSERT INTO Account (first_name, last_name, login, password, birth_date, type) VALUE(?, ?, ?, ?, ?, ?);");
        Library.OUT.println("first name: ");
        final String firstName = Library.IN.nextLine();
        addAccountQuery.setString(1, firstName);
        Library.OUT.println("last name: ");
        final String lastName = Library.IN.nextLine();
        addAccountQuery.setString(2, lastName);
        Library.OUT.println("login: ");
        final String login = Library.IN.nextLine();
        addAccountQuery.setString(3, login);
        Library.OUT.println("password: ");
        final String password = Library.IN.nextLine();
        String salt = BCrypt.gensalt(5);
        final String hashedPassword = salt + BCrypt.hashpw(password, salt);
        addAccountQuery.setString(4, hashedPassword);
        Library.OUT.println("birth date: ");
        final String birthDate = Library.IN.nextLine();
        addAccountQuery.setString(5, birthDate);
        Library.OUT.println("user type (admin, employee. client): ");
        final String type = Library.IN.nextLine();
        if ("client".equals(type) || "employee".equals(type) || "admin".equals(type)) {
            addAccountQuery.setString(6, type);
        } else {
            Library.ERR.println("Incorrect user type given. Query failed, please try again. [PRESS ENTER TO CONTINUE]");
            Library.IN.nextLine();
        }
        addAccountQuery.execute();
    }

    private void grant() throws SQLException {
        PreparedStatement grantQuery = connection.prepareStatement("GRANT ? ON publicationsdb.? TO '?'@'localhost'");
        Library.OUT.println("user to grant privileges to: ");
        final String user = Library.IN.nextLine();
        grantQuery.setString(3, user);
        Library.OUT.println("table to grant privileges on: ");
        final String table = Library.IN.nextLine();
        grantQuery.setString(2, table);
        Library.OUT.println("list of privileges to grant: ");
        final String privileges = Library.IN.nextLine();
        grantQuery.setString(1, privileges);
        grantQuery.execute();
    }

    public AdminUserHandler(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public void start() {
        while (true) {
            Library.OUT.println("Welcome admin! Which query would you like to use:");
            Library.OUT.println("1) CREATE USER");
            Library.OUT.println("2) Add an account");
            Library.OUT.println("3) GRANT privileges");

            String input = Library.IN.nextLine();

            // quit if input = "quit"

            if ("quit".equals(input)) {
                break;
            }

            // perform the selected query

            try {
                int query = Integer.parseInt(input);
                switch (query) {
                    case 1 -> createUser();
                    case 2 -> addAccount();
                    case 3 -> grant();
                    default -> throw new InputMismatchException("Expected a number from range 1-1");
                }
            } catch (NumberFormatException | InputMismatchException e) {
                Library.ERR.println("Input error: " + e.getMessage());
            } catch (SQLException e) {
                Library.ERR.println("SQL error: " + e.getMessage());
            }
        }
        Library.OUT.println("bye");
    }
}
