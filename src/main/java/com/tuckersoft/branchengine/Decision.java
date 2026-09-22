package com.hackathon.model; // Ajusta a tu paquete base

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "decisions")
public class Decision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la partida (Playthrough) - El integrante 2 debe crear esta entidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playthrough_id", nullable = false)
    private Playthrough playthrough;

    // Relación con el nodo destino (StoryNode) - El integrante 2 debe crear esta entidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_node_id")
    private StoryNode destinationNode;

    @Column(nullable = false, length = 500)
    private String inputText;

    @Column(nullable = false)
    private String handlerUnit;

    @Column(nullable = false)
    private String outcomeCode;

    private int lucidityChange;
    private int controlChange;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructores, Getters y Setters
    public Decision() {}

    // Genera el resto de getters y setters con IntelliJ (Alt + Insert)
}