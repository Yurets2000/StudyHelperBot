package com.yube.db.dto;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class Trigger implements Serializable {

    private static final long serialVersionUID = 1634378540228028764L;

    private final int triggerId;
    @NonNull
    private final String triggerType;
    private final String triggerSubType;
    private final String botToken;
    @NonNull
    private final String triggerValue;
}
