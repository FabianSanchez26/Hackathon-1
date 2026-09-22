package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.common.ApiException;
import com.tuckersoft.branchengine.dto.PlaythroughRequest;
import com.tuckersoft.branchengine.dto.PlaythroughResponse;
import com.tuckersoft.branchengine.model.Playthrough;
import com.tuckersoft.branchengine.model.StoryNode;
import com.tuckersoft.branchengine.repository.PlaythroughRepository;
import com.tuckersoft.branchengine.repository.StoryNodeRepository;
import com.tuckersoft.branchengine.security.CurrentUser;
import com.tuckersoft.branchengine.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PlaythroughService {

    private final PlaythroughRepository playthroughRepository;
    private final StoryNodeRepository storyNodeRepository;
    private final CurrentUser currentUser;

    public PlaythroughService(
            PlaythroughRepository playthroughRepository,
            StoryNodeRepository storyNodeRepository,
            CurrentUser currentUser) {

        this.playthroughRepository = playthroughRepository;
        this.storyNodeRepository = storyNodeRepository;
        this.currentUser = currentUser;
    }

    @Transactional
    public PlaythroughResponse createPlaythrough(PlaythroughRequest request) {

        User owner = currentUser.get();

        StoryNode startNode = storyNodeRepository
                .findByNodeCode(request.getStartNodeCode())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Nodo inicial no encontrado"
                ));

        if (playthroughRepository.existsByPlayerTag(request.getPlayerTag())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "El playerTag ya existe"
            );
        }

        if (startNode.getCurrentBranches() >= startNode.getBranchCapacity()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "El nodo inicial alcanzo su capacidad maxima"
            );
        }

        Instant now = Instant.now();

        Playthrough playthrough = new Playthrough();

        playthrough.setPlayerTag(request.getPlayerTag());
        playthrough.setUser(owner);
        playthrough.setStartNodeCode(startNode.getNodeCode());
        playthrough.setCurrentNode(startNode);

        playthrough.setLucidity(100);
        playthrough.setControlLevel(0);
        playthrough.setStatus("ACTIVA");
        playthrough.setEndingCode(null);

        playthrough.setCreatedAt(now);
        playthrough.setUpdatedAt(now);

        startNode.setCurrentBranches(
                startNode.getCurrentBranches() + 1
        );

        storyNodeRepository.save(startNode);

        Playthrough saved =
                playthroughRepository.save(playthrough);

        return toResponse(saved);
    }

    public List<PlaythroughResponse> getAllPlaythroughs() {

        User user = currentUser.get();

        List<Playthrough> playthroughs;

        if ("ROLE_ADMIN".equals(user.getRole())) {
            playthroughs =
                    playthroughRepository.findAllByOrderByCreatedAtDesc();
        } else {
            playthroughs =
                    playthroughRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return playthroughs
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PlaythroughResponse getPlaythroughById(Long id) {

        User user = currentUser.get();

        Playthrough playthrough = playthroughRepository
                .findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Partida no encontrada"
                ));

        boolean isOwner =
                playthrough.getUser().getId().equals(user.getId());

        boolean isAdmin =
                "ROLE_ADMIN".equals(user.getRole());

        if (!isOwner && !isAdmin) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a esta partida"
            );
        }

        return toResponse(playthrough);
    }

    private PlaythroughResponse toResponse(
            Playthrough playthrough) {

        return new PlaythroughResponse(
                playthrough.getId(),
                playthrough.getPlayerTag(),
                playthrough.getUser().getEmail(),
                playthrough.getStartNodeCode(),
                playthrough.getCurrentNode().getNodeCode(),
                playthrough.getLucidity(),
                playthrough.getControlLevel(),
                playthrough.getStatus(),
                playthrough.getEndingCode(),
                playthrough.getCreatedAt(),
                playthrough.getUpdatedAt()
        );
    }
}