package com.yube.db.dao;

import com.yube.db.dto.Trigger;
import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class PostgresTriggerDaoImpl extends PostgresAbstractDaoImpl implements TriggerDao {

    public PostgresTriggerDaoImpl() throws DatabaseException, ConfigurationException {
    }

    @Override
    public Set<Trigger> getAllTriggers() throws DatabaseException {
        Set<Trigger> triggers;
        try(Connection conn = databaseConnectionFactory.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM triggers")){
                triggers = processMultipleResultSet(rs);
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve triggers", e);
        }
        return triggers;
    }

    @Override
    public Set<Trigger> getCommonTriggers() throws DatabaseException {
        Set<Trigger> triggers;
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM triggers WHERE trigger_type = ?")){
            stmt.setString(1, "common");
            try(ResultSet rs = stmt.executeQuery()) {
                triggers = processMultipleResultSet(rs);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve triggers", e);
        }
        return triggers;
    }

    @Override
    public Set<Trigger> getBotTriggers(String token) throws DatabaseException {
        Set<Trigger> triggers;
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM triggers WHERE bot_token = ?")){
            stmt.setString(1, token);
            try(ResultSet rs = stmt.executeQuery()) {
                triggers = processMultipleResultSet(rs);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve triggers", e);
        }
        return triggers;
    }

    private Set<Trigger> processMultipleResultSet(ResultSet rs) throws SQLException {
        Set<Trigger> triggers = new HashSet<>();
        while (rs.next()) {
            Trigger trigger = new Trigger(
                    rs.getInt("trigger_id"),
                    rs.getString("trigger_type"),
                    rs.getString("trigger_subtype"),
                    rs.getString("bot_token"),
                    rs.getString("trigger_value")
            );
            triggers.add(trigger);
        }
        return triggers;
    }
}
