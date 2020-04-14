package com.yube.db.dao;

import com.yube.db.dto.ActorChat;
import com.yube.exceptions.ConfigurationException;
import com.yube.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class PostgresActorChatDaoImpl extends PostgresAbstractDaoImpl implements ActorChatDao {

    public PostgresActorChatDaoImpl() throws DatabaseException, ConfigurationException {
    }

    @Override
    public ActorChat getActorChat(int actorId, long chatId) throws DatabaseException {
        ActorChat actorChat;
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM actor_chat WHERE actor_id = ? AND chat_id = ?")){
            stmt.setInt(1, actorId);
            stmt.setLong(2, chatId);
            try(ResultSet rs = stmt.executeQuery()) {
                actorChat = processSingleResultSet(rs);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve actorChat with such user and chat id", e);
        }
        return actorChat;
    }

    @Override
    public void addActorChat(ActorChat actorChat) throws DatabaseException {
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO actor_chat(actor_id, chat_id) VALUES (?, ?)")){
            stmt.setInt(1, actorChat.getActorId());
            stmt.setLong(2, actorChat.getChatId());
            stmt.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't insert such actorChat", e);
        }
    }

    @Override
    public Set<ActorChat> getActorChatsByType(String type) throws DatabaseException {
        Set<ActorChat> actorChats;
        try(Connection conn = databaseConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM actor_chat WHERE actor_id in (SELECT actor_id FROM actors WHERE actor_type = ?)")){
            stmt.setString(1, type);
            try(ResultSet rs = stmt.executeQuery()) {
                actorChats = processMultipleResultSet(rs);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Couldn't retrieve triggers", e);
        }
        return actorChats;
    }

    private ActorChat processSingleResultSet(ResultSet rs) throws SQLException {
        ActorChat actorChat = null;
        while (rs.next()) {
            actorChat = new ActorChat(
                    rs.getInt("actor_id"),
                    rs.getLong("chat_id")
            );
        }
        return actorChat;
    }

    private Set<ActorChat> processMultipleResultSet(ResultSet rs) throws SQLException {
        Set<ActorChat> actorChats = new HashSet<>();
        while (rs.next()) {
            actorChats.add(new ActorChat(
                    rs.getInt("actor_id"),
                    rs.getLong("chat_id")
            ));
        }
        return actorChats;
    }
}
