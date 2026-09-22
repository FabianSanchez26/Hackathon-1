package com.tuckersoft.branchengine.notification;

import java.time.Instant;

// Immutable snapshot: the worker must not read later playthrough statistics.
public record RealityReport(
        Long decisionId,
        String recipientEmail,
        String displayName,
        String playerTag,
        String branchType,
        String impactLevel,
        String handlerUnit,
        String outcomeCode,
        String sourceNodeCode,
        String resolvedNodeCode,
        String playthroughStatus,
        int lucidity,
        int controlLevel,
        String endingCode,
        Instant createdAt,
        String rawInput) {
}
