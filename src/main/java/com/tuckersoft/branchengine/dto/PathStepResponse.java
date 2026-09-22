package com.tuckersoft.branchengine.dto;

import java.time.Instant;

public class PathStepResponse {

    private Integer order;
    private Long decisionId;
    private String fromNodeCode;
    private String toNodeCode;
    private String branchType;
    private String impactLevel;
    private Instant createdAt;

    public PathStepResponse(
            Integer order,
            Long decisionId,
            String fromNodeCode,
            String toNodeCode,
            String branchType,
            String impactLevel,
            Instant createdAt) {

        this.order = order;
        this.decisionId = decisionId;
        this.fromNodeCode = fromNodeCode;
        this.toNodeCode = toNodeCode;
        this.branchType = branchType;
        this.impactLevel = impactLevel;
        this.createdAt = createdAt;
    }

    public Integer getOrder() {
        return order;
    }

    public Long getDecisionId() {
        return decisionId;
    }

    public String getFromNodeCode() {
        return fromNodeCode;
    }

    public String getToNodeCode() {
        return toNodeCode;
    }

    public String getBranchType() {
        return branchType;
    }

    public String getImpactLevel() {
        return impactLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}