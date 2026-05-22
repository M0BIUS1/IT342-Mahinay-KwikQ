package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.AuditLogResponse;
import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditLogResponse> result = auditLogService.getAllLogs(pageable)
                    .map(log -> new AuditLogResponse(log.getId(), log.getAdmin().getName(), log.getAction(),
                            log.getEntityType(), log.getEntityId(), log.getDetails(), log.getCreatedAt()));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/logs/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditLogResponse> result = auditLogService.getLogsByAction(action, pageable)
                    .map(log -> new AuditLogResponse(log.getId(), log.getAdmin().getName(), log.getAction(),
                            log.getEntityType(), log.getEntityId(), log.getDetails(), log.getCreatedAt()));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/logs/entity/{entityType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLogsByEntityType(
            @PathVariable String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditLogResponse> result = auditLogService.getLogsByEntityType(entityType, pageable)
                    .map(log -> new AuditLogResponse(log.getId(), log.getAdmin().getName(), log.getAction(),
                            log.getEntityType(), log.getEntityId(), log.getDetails(), log.getCreatedAt()));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
