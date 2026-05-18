package com.identio.mvp.infrastructure.config;

import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            java.util.Arrays.stream(connector.findUpgradeProtocols())
                    .filter(protocol -> protocol instanceof Http2Protocol)
                    .map(protocol -> (Http2Protocol) protocol)
                    .forEach(http2Protocol -> {
                        // Increase the overhead data threshold to avoid ENHANCE_YOUR_CALM errors on
                        // gRPC/HTTP2
                        http2Protocol.setOverheadDataThreshold(0);
                    });
        });
    }
}
