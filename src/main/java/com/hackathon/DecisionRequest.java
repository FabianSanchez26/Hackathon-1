package com.hackathon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DecisionRequest(
        @NotNull Long playthroughId,
        @NotBlank String text
) {}