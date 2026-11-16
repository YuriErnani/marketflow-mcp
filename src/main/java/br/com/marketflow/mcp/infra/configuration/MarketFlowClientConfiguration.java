package br.com.marketflow.mcp.infra.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MarketFlowClientConfiguration {

    @Bean
    public RestClient marketFlowRestClient(RestClient.Builder builder, @Value("${api.marketflow.url}") String url) {
        return builder
                .baseUrl(url + "/api/v1")
                .build();
    }

}