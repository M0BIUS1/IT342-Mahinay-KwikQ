package edu.cit.mahinay.kwikq.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {
    private Long id;
    private String adminName;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime createdAt;

    public AuditLogResponse(Long id, String adminName, String action, String entityType, Long entityId, String details, LocalDateTime createdAt) {
        this.id = id;
        this.adminName = adminName;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getAdminName() { return adminName; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
