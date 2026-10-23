package vallegrande.edu.pe.backend.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.r2dbc.mssql.MssqlConnectionFactoryProvider;
import org.springframework.boot.r2dbc.autoconfigure.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.util.function.Function;

@Configuration
public class R2dbcConfig {

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer r2dbcMssqlSslCustomizer() {
        X509ExtendedTrustManager trustAll = new X509ExtendedTrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {}

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        Function<SslContextBuilder, SslContextBuilder> customizer = b -> b.trustManager(trustAll);

        return builder -> {
            builder.option(MssqlConnectionFactoryProvider.SSL_CONTEXT_BUILDER_CUSTOMIZER, customizer);
            builder.option(MssqlConnectionFactoryProvider.TRUST_SERVER_CERTIFICATE, true);
        };
    }
}
