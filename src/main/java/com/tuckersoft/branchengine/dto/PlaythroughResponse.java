package com.tuckersoft.branchengine.dto;

import java.time.Instant;

public class PlaythroughResponse {

    private Long id;
    private String playerTag;
    private String ownerEmail;
    private String startNodeCode;
    private String currentNodeCode;
    private Integer lucidity;
    private Integer controlLevel;
    private String status;
    private String endingCode;
    private Instant createdAt;
    private Instant updatedAt;

    public PlaythroughResponse(
            Long id,
            String playerTag,
            String ownerEmail,
            String startNodeCode,
            String currentNodeCode,
            Integer lucidity,
            Integer controlLevel,
            String status,
            String endingCode,
            Instant createdAt,
            Instant updatedAt) {

        this.id = id;
        this.playerTag = playerTag;
        this.ownerEmail = ownerEmail;
        this.startNodeCode = startNodeCode;
        this.currentNodeCode = currentNodeCode;
        this.lucidity = lucidity;
        this.controlLevel = controlLevel;
        this.status = status;
        this.endingCode = endingCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getPlayerTag() {
        return playerTag;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getStartNodeCode() {
        return startNodeCode;
    }

    public String getCurrentNodeCode() {
        return currentNodeCode;
    }

    public Integer getLucidity() {
        return lucidity;
    }

    public Integer getControlLevel() {
        return controlLevel;
    }

    public String getStatus() {
        return status;
    }

    public String getEndingCode() {
        return endingCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}