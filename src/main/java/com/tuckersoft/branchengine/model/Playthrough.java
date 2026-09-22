package com.tuckersoft.branchengine.model;

import com.tuckersoft.branchengine.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "playthroughs")
public class Playthrough {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String playerTag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 40)
    private String startNodeCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_node_id", nullable = false)
    private StoryNode currentNode;

    @Column(nullable = false)
    private Integer lucidity;

    @Column(nullable = false)
    private Integer controlLevel;

    @Column(nullable = false)
    private String status;

    private String endingCode;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Playthrough() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerTag() {
        return playerTag;
    }

    public void setPlayerTag(String playerTag) {
        this.playerTag = playerTag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStartNodeCode() {
        return startNodeCode;
    }

    public void setStartNodeCode(String startNodeCode) {
        this.startNodeCode = startNodeCode;
    }

    public StoryNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(StoryNode currentNode) {
        this.currentNode = currentNode;
    }

    public Integer getLucidity() {
        return lucidity;
    }

    public void setLucidity(Integer lucidity) {
        this.lucidity = lucidity;
    }

    public Integer getControlLevel() {
        return controlLevel;
    }

    public void setControlLevel(Integer controlLevel) {
        this.controlLevel = controlLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndingCode() {
        return endingCode;
    }

    public void setEndingCode(String endingCode) {
        this.endingCode = endingCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}