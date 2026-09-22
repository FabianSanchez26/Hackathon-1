package com.tuckersoft.branchengine.dto;

import java.util.List;

public class PlaythroughPathResponse {

    private Long playthroughId;
    private String playerTag;
    private String status;
    private String endingCode;
    private String startNodeCode;
    private String currentNodeCode;
    private List<PathStepResponse> steps;

    public PlaythroughPathResponse(
            Long playthroughId,
            String playerTag,
            String status,
            String endingCode,
            String startNodeCode,
            String currentNodeCode,
            List<PathStepResponse> steps) {

        this.playthroughId = playthroughId;
        this.playerTag = playerTag;
        this.status = status;
        this.endingCode = endingCode;
        this.startNodeCode = startNodeCode;
        this.currentNodeCode = currentNodeCode;
        this.steps = steps;
    }

    public Long getPlaythroughId() {
        return playthroughId;
    }

    public String getPlayerTag() {
        return playerTag;
    }

    public String getStatus() {
        return status;
    }

    public String getEndingCode() {
        return endingCode;
    }

    public String getStartNodeCode() {
        return startNodeCode;
    }

    public String getCurrentNodeCode() {
        return currentNodeCode;
    }

    public List<PathStepResponse> getSteps() {
        return steps;
    }
}