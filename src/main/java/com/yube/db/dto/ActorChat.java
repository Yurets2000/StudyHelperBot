package com.yube.db.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActorChat implements Serializable {

    private static final long serialVersionUID = -2245628051162576161L;

    private final int actorId;
    private final long chatId;
}
