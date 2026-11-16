package br.com.marketflow.mcp.infra.configuration;

import br.com.marketflow.mcp.infra.security.MarketFlowTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class MarketFlowClientConfiguration {

    @Bean
    @Primary
    public RestClient marketFlowRestClient(RestClient.Builder builder, @Value("${api.marketflow.url}") String url) {
        return builder
                .baseUrl(url + "/api/v1")
                .build();
    }

    @Bean
    public RestClient marketFlowAdminRestClient(RestClient.Builder builder, @Value("${api.marketflow.url}") String url, MarketFlowTokenService tokenService) {
        return builder
                .baseUrl(url + "/api/v1")
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(tokenService.obterToken());
                    return execution.execute(request, body);
                })
                .build();
    }

}