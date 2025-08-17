package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.service.AuditHistoryService;
import com.coherentsolutions.pot.insuranceservice.service.AuditHistoryService.RevisionMetadata;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuditController {

  private final AuditHistoryService service;

  @GetMapping("/users/{id}/history")
  public ResponseEntity<List<RevisionMetadata>> userHistory(@PathVariable UUID id) {
    return ResponseEntity.ok(service.userHistory(id));
  }

  @GetMapping("/companies/{id}/history")
  public ResponseEntity<List<RevisionMetadata>> companyHistory(@PathVariable UUID id) {
    return ResponseEntity.ok(service.companyHistory(id));
  }
}
