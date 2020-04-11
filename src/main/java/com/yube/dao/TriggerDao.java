package com.yube.dao;

import com.yube.dto.Trigger;
import com.yube.exceptions.DatabaseException;

import java.util.Set;

public interface TriggerDao {
    Set<Trigger> getAllTriggers() throws DatabaseException;
    Set<Trigger> getCommonTriggers() throws DatabaseException;
    Set<Trigger> getBotTriggers(String token) throws DatabaseException;
}
