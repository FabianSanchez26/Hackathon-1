package com.hackathon.dto;

import java.time.LocalDateTime;

public record DecisionResponse(
        Long id,
        Long playthroughId,
        Long destinationNodeId,
        String inputText,
        String handlerUnit,
        String outcomeCode,
        int lucidityChange,
        int controlChange,
        LocalDateTime createdAt
) {}