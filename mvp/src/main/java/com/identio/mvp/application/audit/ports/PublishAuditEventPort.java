package com.identio.mvp.application.audit.ports;

import com.identio.mvp.application.audit.dto.AuditEvent;

public interface PublishAuditEventPort {
    void publish(AuditEvent event);
}
