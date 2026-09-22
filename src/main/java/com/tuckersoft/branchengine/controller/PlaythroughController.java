package com.tuckersoft.branchengine.controller;

import com.tuckersoft.branchengine.dto.PlaythroughRequest;
import com.tuckersoft.branchengine.dto.PlaythroughResponse;
import com.tuckersoft.branchengine.service.PlaythroughService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playthroughs")
public class PlaythroughController {

    private final PlaythroughService playthroughService;

    public PlaythroughController(PlaythroughService playthroughService) {
        this.playthroughService = playthroughService;
    }

    @PostMapping
    public ResponseEntity<PlaythroughResponse> createPlaythrough(
            @Valid @RequestBody PlaythroughRequest request) {

        PlaythroughResponse response =
                playthroughService.createPlaythrough(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlaythroughResponse>> getAllPlaythroughs() {

        return ResponseEntity.ok(
                playthroughService.getAllPlaythroughs()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaythroughResponse> getPlaythroughById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                playthroughService.getPlaythroughById(id)
        );
    }
}