package com.yube.db.dao;

import com.yube.db.DatabaseConnectionFactory;
import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;

public class PostgresAbstractDaoImpl {

    protected DatabaseConnectionFactory databaseConnectionFactory;

    public PostgresAbstractDaoImpl() throws DatabaseException, ConfigurationException {
        this.databaseConnectionFactory = DatabaseConnectionFactory.getInstance();
    }
}
