package com.hackathon;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;

@Service
public class DecisionService {

    private final DecisionRepository decisionRepository;
    // Repositorios del Integrante 2 (marcarán error hasta que él haga push)
    private final PlaythroughRepository playthroughRepository;
    private final StoryNodeRepository storyNodeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DecisionService(DecisionRepository decisionRepository,
                           PlaythroughRepository playthroughRepository,
                           StoryNodeRepository storyNodeRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.decisionRepository = decisionRepository;
        this.playthroughRepository = playthroughRepository;
        this.storyNodeRepository = storyNodeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DecisionResponse processDecision(DecisionRequest request, String playerEmail, boolean isMailFailureSimulation) {

        // 1. Obtener la partida (Validaciones)
        Playthrough playthrough = playthroughRepository.findById(request.playthroughId())
                .orElseThrow(() -> new RuntimeException("Partida no encontrada")); // 404

        if (!playthrough.getUser().getEmail().equals(playerEmail)) {
            throw new RuntimeException("No tienes permiso sobre esta partida"); // 403
        }
        if ("FINALIZADA".equals(playthrough.getStatus())) {
            throw new RuntimeException("La partida ya está finalizada"); // 409
        }

        // 2. Normalización del texto
        String normalizedText = Normalizer.normalize(request.text(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        // 3. Reglas de Clasificación (Orden Errata v1.3)
        String branchType;
        String handlerUnit;
        String outcomeCode;

        if (!normalizedText.matches(".*[a-z].*")) {
            branchType = "ENTRADA_CORRUPTA";
            handlerUnit = "Archivo de Errores";
            outcomeCode = "DISCARD_INPUT";
        } else if (normalizedText.matches(".*(rechaza|destruye|desobedece|renuncia).*")) {
            branchType = "REBELDIA";
            handlerUnit = "Control de Continuidad";
            outcomeCode = "FORK_TIMELINE";
        } else if (normalizedText.matches(".*(vigilan|simbolo|conspiracion).*")) {
            branchType = "SOSPECHA";
            handlerUnit = "Oficina de Seguridad";
            outcomeCode = "INJECT_WHITE_BEAR_SYMBOL";
        } else if (normalizedText.matches(".*(netflix|camara|espectador|videojuego).*")) {
            branchType = "RUPTURA_CUARTA_PARED";
            handlerUnit = "Departamento Netflix";
            outcomeCode = "BREAK_FOURTH_WALL";
        } else {
            branchType = "OBEDIENCIA";
            handlerUnit = "Mesa de Guión"; // Con tilde por errata v1.3
            outcomeCode = "ADVANCE_MAIN_PATH";
        }

        // 4. Calcular Stats por Impacto (Errata v1.3 aplica stat change incluso a corruptas)
        int lucidityChange = 0;
        int controlChange = 0;
        switch (request.impactLevel()) {
            case "LEVE" -> { lucidityChange = -5; controlChange = 5; }
            case "MODERADO" -> { lucidityChange = -15; controlChange = 10; }
            case "GRAVE" -> { lucidityChange = -30; controlChange = 20; }
            case "CRITICO" -> { lucidityChange = -45; controlChange = 40; } // Errata v1.3
            default -> throw new RuntimeException("Impacto inválido"); // 400
        }

        playthrough.setLucidity(Math.max(0, Math.min(100, playthrough.getLucidity() + lucidityChange)));
        playthrough.setControlLevel(Math.max(0, Math.min(100, playthrough.getControlLevel() + controlChange)));
        playthrough.setUpdatedAt(LocalDateTime.now());

        Decision decision = new Decision();
        decision.setPlaythrough(playthrough);
        decision.setNode(playthrough.getCurrentNode());
        decision.setInputText(request.text());
        decision.setBranchType(branchType);
        decision.setImpactLevel(request.impactLevel());
        decision.setHandlerUnit(handlerUnit);
        decision.setOutcomeCode(outcomeCode);

        // 5. Resolucion de Nodos y Finales (Solo si NO es corrupta)
        if (branchType.equals("ENTRADA_CORRUPTA")) {
            decision.setResolvedNodeCode(null);
            decision.setStatus("ERROR");
        } else {
            String targetNodeCode = (branchType.equals("RUPTURA_CUARTA_PARED") || "CRITICO".equals(request.impactLevel()))
                    ? playthrough.getCurrentNode().getGlitchBranchCode()
                    : playthrough.getCurrentNode().getPrimaryBranchCode();

            decision.setResolvedNodeCode(targetNodeCode);
            decision.setStatus("REGISTRADA");

            StoryNode targetNode = targetNodeCode != null ? storyNodeRepository.findByNodeCode(targetNodeCode).orElse(null) : null;

            // Orden de finales corregido (Errata v1.3)
            if (playthrough.getLucidity() <= 0) {
                playthrough.setStatus("FINALIZADA");
                playthrough.setEndingCode("ENDING_WHITE_BEAR");
            } else if (playthrough.getControlLevel() >= 100) {
                playthrough.setStatus("FINALIZADA");
                playthrough.setEndingCode("ENDING_PAC_SYMBOL");
            } else if (targetNode == null) {
                playthrough.setStatus("FINALIZADA");
                playthrough.setEndingCode("ENDING_NETFLIX_CUT");
            } else {
                playthrough.setStatus("ACTIVA");
                playthrough.setCurrentNode(targetNode);
            }
        }

        playthroughRepository.save(playthrough);
        decision = decisionRepository.save(decision);

        // 6. Publicar Evento solo si es válida
        if (!branchType.equals("ENTRADA_CORRUPTA")) {
            DecisionCommittedEvent event = new DecisionCommittedEvent(
                    decision.getId(),
                    playerEmail,
                    request.text(),
                    handlerUnit,
                    outcomeCode,
                    isMailFailureSimulation
            );
            eventPublisher.publishEvent(event);
        }

        return new DecisionResponse(
                decision.getId(),
                playthrough.getId(),
                decision.getResolvedNodeCode(),
                request.text(),
                branchType,
                request.impactLevel(),
                handlerUnit,
                outcomeCode,
                decision.getStatus(),
                playthrough.getStatus(),
                playthrough.getLucidity(),
                playthrough.getControlLevel(),
                playthrough.getEndingCode(),
                LocalDateTime.now()
        );
    }
}