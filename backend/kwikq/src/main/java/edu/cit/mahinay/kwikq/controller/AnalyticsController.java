package edu.cit.mahinay.kwikq.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    @PostMapping("/retry")
    public ResponseEntity<Void> receiveRetryAnalytics(@RequestBody Map<String, Object> payload) {
        // For now, just log the incoming payload. This endpoint exists to accept mobile retry events.
        log.info("Received retry analytics: {}", payload);
        return ResponseEntity.noContent().build();
    }
}
