package com.yube.db.dao;

import com.yube.db.dto.Actor;
import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class PostgresActorDaoImpl extends PostgresAbstractDaoImpl implements ActorDao {

    public PostgresActorDaoImpl() throws DatabaseException, ConfigurationException {
    }

    @Override
    public Actor getActorById(int actorId) throws DatabaseException {
        Actor actor;
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM actors WHERE actor_id = ?")){
            stmt.setInt(1, actorId);
            try(ResultSet rs = stmt.executeQuery()) {
                actor = processSingleResultSet(rs);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve actor with such id", e);
        }
        return actor;
    }

    @Override
    public Actor getActorByUserIdAndType(int userId, String actorType) throws DatabaseException {
        Actor actor;
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM actors WHERE user_id = ? AND actor_type = ?")){
            stmt.setInt(1, userId);
            stmt.setString(2, actorType);
            try(ResultSet rs = stmt.executeQuery()) {
                actor = processSingleResultSet(rs);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve actor with such user id and type", e);
        }
        return actor;
    }

    @Override
    public Set<Actor> getActorsByType(String type) throws DatabaseException {
        Set<Actor> actors;
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM actors WHERE actor_type = ?")){
            stmt.setString(1, type);
            try(ResultSet rs = stmt.executeQuery()) {
                actors = processMultipleResultSet(rs);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve triggers", e);
        }
        return actors;
    }

    private Actor processSingleResultSet(ResultSet rs) throws SQLException {
        Actor actor = null;
        while (rs.next()) {
             actor = new Actor(
                    rs.getInt("actor_id"),
                    rs.getString("actor_name"),
                    rs.getString("actor_type"),
                    rs.getInt("user_id")
            );
        }
        return actor;
    }

    private Set<Actor> processMultipleResultSet(ResultSet rs) throws SQLException {
        Set<Actor> actors = new HashSet<>();
        while (rs.next()) {
            actors.add(new Actor(
                    rs.getInt("actor_id"),
                    rs.getString("actor_name"),
                    rs.getString("actor_type"),
                    rs.getInt("user_id")
            ));
        }
        return actors;
    }
}
