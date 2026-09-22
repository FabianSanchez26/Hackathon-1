package com.hackathon.repository;

import com.hackathon.model.Decision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Long> {
    // Método listo para la paginación que te pide la estrella 4
    Page<Decision> findByPlaythroughId(Long playthroughId, Pageable pageable);
}