package com.tuckersoft.branchengine.controller;

import com.tuckersoft.branchengine.dto.StoryNodeRequest;
import com.tuckersoft.branchengine.dto.StoryNodeResponse;
import com.tuckersoft.branchengine.service.StoryNodeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
public class StoryNodeController {

    private final StoryNodeService storyNodeService;

    public StoryNodeController(StoryNodeService storyNodeService) {
        this.storyNodeService = storyNodeService;
    }

    @PostMapping
    public ResponseEntity<StoryNodeResponse> createNode(
            @Valid @RequestBody StoryNodeRequest request) {

        StoryNodeResponse response = storyNodeService.createNode(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<StoryNodeResponse>> getAllNodes() {

        return ResponseEntity.ok(
                storyNodeService.getAllNodes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryNodeResponse> getNodeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                storyNodeService.getNodeById(id)
        );
    }
}   