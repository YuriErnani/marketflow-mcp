package br.com.marketflow.mcp.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("remoto")
public class SecurityConfiguration {

    private static final int TAMANHO_MINIMO_CHAVE = 32;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Value("${api.seguranca.chave}") String chave) throws Exception {
        if (chave.isBlank() || chave.length() < TAMANHO_MINIMO_CHAVE)
            throw new IllegalStateException("Modo remoto exige MCP_API_KEY com pelo menos " + TAMANHO_MINIMO_CHAVE + " caracteres.");

        return http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(autorizacao -> autorizacao.anyRequest().authenticated())

                .exceptionHandling(erros -> erros.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                .addFilterBefore(new ChaveApiFilter(chave), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

}