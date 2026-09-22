package com.tuckersoft.branchengine.repository;

import com.tuckersoft.branchengine.model.Playthrough;
import com.tuckersoft.branchengine.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaythroughRepository extends JpaRepository<Playthrough, Long> {

    boolean existsByPlayerTag(String playerTag);

    List<Playthrough> findByUserOrderByCreatedAtDesc(User user);

    List<Playthrough> findAllByOrderByCreatedAtDesc();
}