package org.pawles.library;

import org.mindrot.jbcrypt.BCrypt;
import org.pawles.library.handlers.*;
import org.pawles.library.utils.Pair;
import org.pawles.library.utils.Usertype;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Library {

    public static final Scanner IN = new Scanner(System.in);
    public static final PrintWriter ERR = new PrintWriter(System.err, true);
    public static final PrintWriter OUT = new PrintWriter(System.out, true);
    public static final String DATABASE_LINK = "jdbc:mariadb://localhost:3306/publicationsdb";

    private static Pair<Connection, Usertype> logIn() {
        Logger logger = new Logger();
        Pair<Connection, Usertype> pair = null;
        try {
            while (pair == null) {
                pair = logger.logIn();
            }
        } catch (SQLException e) {
            ERR.println("Error while connecting to the server: " + e.getMessage());
        } catch (IllegalStateException e) {
            ERR.println("Logged in as an unhandled user");
        }
        return pair;
    }

    private static void register() {
        OUT.println("Register");
    }

    private static void start(Connection connection, Usertype usertype) {
        UserHandler userHandler = null;
        try {
            switch (usertype) {
                case UNLOGGED -> userHandler = new UnloggedUserHandler(connection);
                case CLIENT -> userHandler = new ClientUserHandler(connection);
                case EMPLOYEE -> userHandler = new EmployeeUserHandler(connection);
                case ADMIN -> userHandler = new AdminUserHandler(connection);
                default -> throw new IllegalStateException("Unexpected value: " + usertype);
            }
        } catch (SQLException e) {
            OUT.println("there was an error in preparing the handler: " + e.getMessage());
        }
        userHandler.start();
    }

    private static void chooseAccount() {
        String input = IN.nextLine();
        try {
            final int action = Integer.parseInt(input);
            switch (action) {
                case 1 -> {
                    Pair<Connection, Usertype> pair = logIn();
                    start(pair.first, pair.second);
                }
                case 2 -> register();
                case 3 -> start(DriverManager.getConnection(DATABASE_LINK, "unlogged", System.getenv("UNLOGGEDPASS")), Usertype.UNLOGGED);
                default -> throw new InputMismatchException("Received " + action + ", expected number from range 1-3");
            }
        } catch (NumberFormatException | InputMismatchException e) {
            ERR.println("Input error: " + e.getMessage());
        } catch (SQLException e) {
            ERR.println("Error while connecting to the database: " + e.getMessage());
        }
    }

    public static void main(final String[] args) {

        final String salt = BCrypt.gensalt(5);
        OUT.println(salt);
        OUT.println(salt + BCrypt.hashpw("abc", salt));

        OUT.println("Welcome to the electronic library application! Please choose how you wish to continue:");
        OUT.println("1) Log in");
        OUT.println("2) Register");
        OUT.println("3) Continue without an account\n");
        OUT.print("user input: ");
        OUT.flush();

        chooseAccount();
    }
}
