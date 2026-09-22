package com.hackathon;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DecisionService(DecisionRepository decisionRepository, ApplicationEventPublisher eventPublisher) {
        this.decisionRepository = decisionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DecisionResponse processDecision(DecisionRequest request, String playerEmail) {
        // 1. Normalización del texto
        String normalizedText = request.text().trim().toLowerCase();

        // 2. Variables por defecto
        String handlerUnit = "DEFAULT_UNIT";
        String outcomeCode = "NEUTRAL";
        int lucidityChange = 0;
        int controlChange = 0;
        boolean isMailFailureSimulation = false;

        // --- AQUÍ IRÁN LAS 5 REGLAS DE CLASIFICACIÓN ---
        if (normalizedText.contains("mail_failure")) {
            isMailFailureSimulation = true;
        }

        // 3. Crear entidad y guardar (Simulado hasta que el Integrante 2 termine)
        Decision decision = new Decision();
        decision.setInputText(normalizedText);
        decision.setHandlerUnit(handlerUnit);
        decision.setOutcomeCode(outcomeCode);
        decision = decisionRepository.save(decision);

        // 4. Publicar el evento para que el Integrante 1 envíe el correo
        DecisionCommittedEvent event = new DecisionCommittedEvent(
                decision.getId(),
                playerEmail,
                normalizedText,
                handlerUnit,
                outcomeCode,
                isMailFailureSimulation
        );
        eventPublisher.publishEvent(event);

        // 5. Retornar respuesta
        return new DecisionResponse(
                decision.getId(),
                request.playthroughId(),
                null, // Destino pendiente de integración
                normalizedText,
                handlerUnit,
                outcomeCode,
                lucidityChange,
                controlChange,
                LocalDateTime.now()
        );
    }
}