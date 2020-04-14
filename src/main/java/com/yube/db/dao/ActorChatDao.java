package com.yube.db.dao;

import com.yube.db.dto.ActorChat;
import com.yube.exceptions.DatabaseException;

import java.util.Set;

public interface ActorChatDao {
    ActorChat getActorChat(int actorId, long chatId) throws DatabaseException;
    void addActorChat(ActorChat actorChat) throws DatabaseException;
    Set<ActorChat> getActorChatsByType(String type) throws DatabaseException;
}
