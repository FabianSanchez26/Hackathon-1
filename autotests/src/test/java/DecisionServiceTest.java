package com.hackathon;
package com.hackathon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionServiceTest {

    @Mock private DecisionRepository decisionRepository;
    @Mock private PlaythroughRepository playthroughRepository;
    @Mock private StoryNodeRepository storyNodeRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DecisionService decisionService;

    private Playthrough mockPlaythrough;
    private User mockUser;
    private StoryNode mockNode;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setEmail("test@test.com");

        mockNode = new StoryNode();
        mockNode.setNodeCode("NODE-CEREAL");
        mockNode.setPrimaryBranchCode("NODE-BUS");

        mockPlaythrough = new Playthrough();
        mockPlaythrough.setId(1L);
        mockPlaythrough.setUser(mockUser);
        mockPlaythrough.setStatus("ACTIVA");
        mockPlaythrough.setLucidity(100);
        mockPlaythrough.setControlLevel(0);
        mockPlaythrough.setCurrentNode(mockNode);
    }

    @Test
    void test1_PrecedenciaRupturaCuartaPared() {
        // En v1.3 "destruye" (REBELDIA) va antes que "camara" (RUPTURA). Por tanto es REBELDIA.
        when(playthroughRepository.findById(1L)).thenReturn(Optional.of(mockPlaythrough));
        when(decisionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        DecisionRequest request = new DecisionRequest(1L, "Stefan destruye la camara", "LEVE");
        DecisionResponse res = decisionService.processDecision(request, "test@test.com", false);

        assertEquals("REBELDIA", res.branchType());
    }

    @Test
    void test2_EntradaCorrupta() {
        when(playthroughRepository.findById(1L)).thenReturn(Optional.of(mockPlaythrough));
        when(decisionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        DecisionRequest request = new DecisionRequest(1L, "123456 !!!", "LEVE");
        DecisionResponse res = decisionService.processDecision(request, "test@test.com", false);

        assertEquals("ENTRADA_CORRUPTA", res.branchType());
        assertEquals("ERROR", res.status());
        assertNull(res.resolvedNodeCode());
    }

    @Test
    void test3_ImpactoCriticoRespetaLimites() {
        mockPlaythrough.setLucidity(20); // Al bajar 45 debe quedar en 0
        mockPlaythrough.setControlLevel(80); // Al subir 40 debe quedar en 100
        when(playthroughRepository.findById(1L)).thenReturn(Optional.of(mockPlaythrough));
        when(decisionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        DecisionRequest request = new DecisionRequest(1L, "Opcion normal", "CRITICO");
        DecisionResponse res = decisionService.processDecision(request, "test@test.com", false);

        assertEquals(0, res.lucidity());
        assertEquals(100, res.controlLevel());
    }

    @Test
    void test4_FinalWhiteBearGanaSobrePacSymbol() {
        // Errata v1.3: lucidez 0 se evalúa primero que control 100
        mockPlaythrough.setLucidity(40);
        mockPlaythrough.setControlLevel(60);
        when(playthroughRepository.findById(1L)).thenReturn(Optional.of(mockPlaythrough));
        when(decisionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        DecisionRequest request = new DecisionRequest(1L, "Opcion", "CRITICO"); // -45, +40 -> Lucidez 0, Control 100
        DecisionResponse res = decisionService.processDecision(request, "test@test.com", false);

        assertEquals("FINALIZADA", res.playthroughStatus());
        assertEquals("ENDING_WHITE_BEAR", res.endingCode());
    }

    @Test
    void test5_PublishEventNoSeLlamaEnEntradaCorrupta() {
        when(playthroughRepository.findById(1L)).thenReturn(Optional.of(mockPlaythrough));
        when(decisionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        DecisionRequest reqNormal = new DecisionRequest(1L, "Saltar", "LEVE");
        decisionService.processDecision(reqNormal, "test@test.com", false);
        verify(eventPublisher, times(1)).publishEvent(any());

        DecisionRequest reqCorrupto = new DecisionRequest(1L, "123", "LEVE");
        decisionService.processDecision(reqCorrupto, "test@test.com", false);
        verify(eventPublisher, times(1)).publishEvent(any()); // Sigue siendo 1 porque no se publicó el segundo
    }
}