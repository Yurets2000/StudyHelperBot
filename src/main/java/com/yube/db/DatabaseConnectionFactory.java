package com.yube.db;

import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;
import com.yube.utils.PropertiesHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnectionFactory {

    private Properties props;
    private static DatabaseConnectionFactory databaseConnectionFactory;

    private DatabaseConnectionFactory() throws DatabaseException, ConfigurationException {
        props = PropertiesHandler.getProperties("db.properties");
        try{
            Class.forName(props.getProperty("DB_DRIVER_CLASS"));
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Couldn't load database driver class", e);
        }
    }

    public static DatabaseConnectionFactory getInstance() throws DatabaseException, ConfigurationException {
        if (databaseConnectionFactory == null) {
            databaseConnectionFactory = new DatabaseConnectionFactory();
        }
        return databaseConnectionFactory;
    }

    public Connection getConnection() throws DatabaseException {
        Connection con;
        try {
            con = DriverManager.getConnection(
                    props.getProperty("DB_URL"),
                    props.getProperty("DB_USERNAME"),
                    props.getProperty("DB_PASSWORD"));
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't establish connection with database", e);
        }
        return con;
    }
}
