package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.dto.StoryNodeRequest;
import com.tuckersoft.branchengine.dto.StoryNodeResponse;
import com.tuckersoft.branchengine.model.StoryNode;
import com.tuckersoft.branchengine.repository.StoryNodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class StoryNodeService {

    private final StoryNodeRepository storyNodeRepository;

    public StoryNodeService(StoryNodeRepository storyNodeRepository) {
        this.storyNodeRepository = storyNodeRepository;
    }

    public StoryNodeResponse createNode(StoryNodeRequest request) {

        if (storyNodeRepository.existsByNodeCode(request.getNodeCode())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nodeCode ya existe"
            );
        }

        StoryNode node = new StoryNode();

        node.setNodeCode(request.getNodeCode());
        node.setTitle(request.getTitle());
        node.setSceneText(request.getSceneText());
        node.setBranchCapacity(request.getBranchCapacity());

        node.setCurrentBranches(0);

        node.setPrimaryBranchCode(request.getPrimaryBranchCode());
        node.setGlitchBranchCode(request.getGlitchBranchCode());

        node.setCreatedAt(Instant.now());

        StoryNode savedNode = storyNodeRepository.save(node);

        return toResponse(savedNode);
    }

    public List<StoryNodeResponse> getAllNodes() {
        return storyNodeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public StoryNodeResponse getNodeById(Long id) {

        StoryNode node = storyNodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nodo no encontrado"
                ));

        return toResponse(node);
    }

    private StoryNodeResponse toResponse(StoryNode node) {

        return new StoryNodeResponse(
                node.getId(),
                node.getNodeCode(),
                node.getTitle(),
                node.getSceneText(),
                node.getBranchCapacity(),
                node.getCurrentBranches(),
                node.getPrimaryBranchCode(),
                node.getGlitchBranchCode(),
                node.getCreatedAt()
        );
    }
}