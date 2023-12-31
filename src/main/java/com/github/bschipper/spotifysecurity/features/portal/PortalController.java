package com.github.bschipper.spotifysecurity.features.portal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portal")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class PortalController {
    private final PortalService portalService;

    @GetMapping
    public ResponseEntity<?> getApplicationMetrics() {
        return ResponseEntity.ok(portalService.getApplicationMetrics());
    }
}
