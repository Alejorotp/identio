package com.identio.mvp.interfaces.audit.http;

import com.identio.mvp.application.audit.dto.AuditEvent;
import com.identio.mvp.application.audit.dto.AuditLogResponse;
import com.identio.mvp.application.audit.ports.PublishAuditEventPort;
import com.identio.mvp.application.audit.usecases.GetAuditLogsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final GetAuditLogsUseCase getAuditLogsUseCase;
    private final PublishAuditEventPort publishAuditEventPort;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(getAuditLogsUseCase.getAuditLogs(PageRequest.of(page, size)));
    }

    @PostMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Publish an audit/access event",
            description = "Publish an event asynchronously. Details JSON depends on the event type.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuditEvent.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Access Event (NFC/Hardware)",
                                            value = "{\n  \"userId\": \"123e4567-e89b-12d3-a456-426614174000\",\n  \"eventType\": \"ACCESS\",\n  \"details\": {\n    \"sensor_id\": \"sensor-999\",\n    \"access_action\": \"ENTER\",\n    \"status\": \"GRANTED\"\n  }\n}"
                                    ),
                                    @ExampleObject(
                                            name = "Facial Event (Mobile)",
                                            value = "{\n  \"userId\": \"123e4567-e89b-12d3-a456-426614174000\",\n  \"eventType\": \"FACIAL\",\n  \"details\": {\n    \"confidence_score\": 0.98,\n    \"liveness_score\": 0.95,\n    \"device_os\": \"iOS\",\n    \"status\": \"REJECTED\",\n    \"reason\": \"mismatch\"\n  }\n}"
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<Void> publishEvent(@RequestBody AuditEvent event) {
        publishAuditEventPort.publish(event);
        return ResponseEntity.accepted().build();
    }
}
