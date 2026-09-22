package com.hackathon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/decisions")
public class DecisionController {

    private final DecisionService decisionService;
    private final DecisionRepository decisionRepository;

    public DecisionController(DecisionService decisionService, DecisionRepository decisionRepository) {
        this.decisionService = decisionService;
        this.decisionRepository = decisionRepository;
    }

    // 1. Endpoint para crear una decisión
    @PostMapping
    public ResponseEntity<DecisionResponse> makeDecision(
            @RequestBody DecisionRequest request,
            // Header temporal para simular al usuario hasta que el Integrante 1 acabe la Seguridad
            @RequestHeader(value = "X-User-Email", defaultValue = "jugador@test.com") String playerEmail) {

        DecisionResponse response = decisionService.processDecision(request, playerEmail);
        return ResponseEntity.ok(response);
    }

    // 2. Endpoint para ver el detalle de una decisión (Estrella 4)
    @GetMapping("/{id}")
    public ResponseEntity<Decision> getDecisionById(@PathVariable Long id) {
        return decisionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Endpoint para listar decisiones con filtros y paginación (Estrella 4)
    @GetMapping
    public ResponseEntity<Page<Decision>> listDecisions(
            @RequestParam Long playthroughId,
            Pageable pageable) {

        Page<Decision> decisions = decisionRepository.findByPlaythroughId(playthroughId, pageable);
        return ResponseEntity.ok(decisions);
    }
}