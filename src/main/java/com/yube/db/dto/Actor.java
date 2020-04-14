package com.yube.db.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Actor implements Serializable {

    private static final long serialVersionUID = 3264345799553910737L;

    private final int actorId;
    private final String actorName;
    private final String actorType;
    private final int userId;
}
