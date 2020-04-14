package com.yube.db.dao;

import com.yube.db.dto.Actor;
import com.yube.exceptions.DatabaseException;

import java.util.Set;

public interface ActorDao {
    Actor getActorById(int actorId) throws DatabaseException;
    Actor getActorByUserIdAndType(int userId, String actorType) throws DatabaseException;
    Set<Actor> getActorsByType(String type) throws DatabaseException;
}
