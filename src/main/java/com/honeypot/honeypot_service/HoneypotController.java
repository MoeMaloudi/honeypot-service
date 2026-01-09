package com.honeypot.honeypot_service;

import com.github.dockerjava.api.model.Container;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/honeypots")
public class HoneypotController {

    private final HoneypotService honeypotService;

    public HoneypotController(HoneypotService honeypotService) {
        this.honeypotService = honeypotService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createHoneypot() {
        Map<String, String> result = honeypotService.createHoneypot();
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<Container>> listHoneypots() {
        List<Container> honeypots = honeypotService.listHoneypots();
        return ResponseEntity.ok(honeypots);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> stopHoneypot(@PathVariable String id) {
        honeypotService.stopHoneypot(id);
        return ResponseEntity.ok("Honeypot stopped and removed");
    }
}