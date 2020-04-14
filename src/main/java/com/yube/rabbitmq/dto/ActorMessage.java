package com.yube.rabbitmq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActorMessage implements Serializable {

    private static final long serialVersionUID = 2304217959414830128L;

    private final int actorId;
    private final String message;
}
