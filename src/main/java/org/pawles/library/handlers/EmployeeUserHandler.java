package org.pawles.library.handlers;

import org.pawles.library.Library;

import java.sql.Connection;

public class EmployeeUserHandler implements UserHandler {

    private final Connection connection;

    public EmployeeUserHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void start() {
        Library.OUT.println("Welcome employee!");
    }
}
