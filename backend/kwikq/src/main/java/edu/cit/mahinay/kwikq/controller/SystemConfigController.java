package edu.cit.mahinay.kwikq.controller;

import edu.cit.mahinay.kwikq.dto.MessageResponse;
import edu.cit.mahinay.kwikq.features.systemconfig.entity.SystemConfig;
import edu.cit.mahinay.kwikq.features.systemconfig.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService configService;

    @GetMapping("/{key}")
    public ResponseEntity<?> getConfig(@PathVariable String key) {
        try {
            String value = configService.getConfig(key, "NOT_FOUND");
            return ResponseEntity.ok(new MessageResponse("Value: " + value));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllConfigs() {
        try {
            List<SystemConfig> configs = configService.getAllConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setConfig(
            @PathVariable String key,
            @RequestParam String value,
            @RequestParam(required = false) String description) {
        try {
            SystemConfig config = configService.setConfig(key, value, description != null ? description : "");
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
