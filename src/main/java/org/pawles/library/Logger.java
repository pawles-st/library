package org.pawles.library;

import org.mindrot.jbcrypt.BCrypt;
import org.pawles.library.utils.Pair;
import org.pawles.library.utils.Usertype;

import java.sql.*;

public class Logger {
    public Pair<Connection, Usertype> logIn() throws SQLException {

        Library.OUT.print("login: ");
        Library.OUT.flush();
        final String login = Library.IN.nextLine();

        Library.OUT.print("password: ");
        Library.OUT.flush();
        final String password = Library.IN.nextLine(); // TODO no echo

        Connection connection = DriverManager.getConnection(Library.DATABASE_LINK, "logger", System.getenv("LOGGERPASS"));
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT login, password, type FROM Account;");

        boolean logged = false;
        String retrievedLogin;
        String retrievedPassword;
        String retrievedSalt;
        String usertype = null;
        while (resultSet.next()) {
            retrievedSalt = "";
            retrievedLogin = resultSet.getString("login");
            retrievedPassword = resultSet.getString("password");
            int k = 0;
            int i = 0;
            try {
                while (k < 4) {
                    System.out.println("s");
                    if (retrievedPassword.charAt(i) == '$') {
                        ++k;
                        if (k == 4) {
                            break;
                        }
                    }
                    retrievedSalt += retrievedPassword.charAt(i);
                    ++i;
                }
            } catch (StringIndexOutOfBoundsException e) {
                continue;
            }
            System.out.println(retrievedSalt);
            if (retrievedLogin.equals(login) && retrievedPassword.equals(retrievedSalt + BCrypt.hashpw(password, retrievedSalt))) {
                usertype = resultSet.getString("type");
                System.out.println("logged in as " + login);
                logged = true;
                break;
            }
        }

        if (logged) {
            try {
                if ("Admin".equals(usertype)) {
                    return new Pair<>(DriverManager.getConnection(Library.DATABASE_LINK, "admin", System.getenv("ADMINPASS")), Usertype.ADMIN);
                } else if ("Employee".equals(usertype)) {
                    return new Pair<>(DriverManager.getConnection(Library.DATABASE_LINK, "employee", System.getenv("EMPLOYEEPASS")), Usertype.EMPLOYEE);
                } else if ("Client".equals(usertype)) {
                    return new Pair<>(DriverManager.getConnection(Library.DATABASE_LINK, "client", System.getenv("CLIENTPASS")), Usertype.CLIENT);
                } else {
                    throw new IllegalStateException("Unhandled user type found.");
                }
            } catch (SQLException e) {
                throw new SQLException(e);
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e);
            }
        } else {
            Library.OUT.println("No such user, try again.");
        }
        return null;
    }
}
