package com.hackathon.event;

public record DecisionCommittedEvent(
        Long decisionId,
        String playerEmail, // Para que el Integrante 1 sepa a quién escribirle
        String inputText,
        String handlerUnit,
        String outcomeCode,
        boolean isMailFailureSimulation // true si la decisión activó MAIL_FAILURE
) {}