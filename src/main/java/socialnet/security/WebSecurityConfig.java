package socialnet.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import socialnet.security.jwt.AuthTokenFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final AuthTokenFilter authTokenFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.applyPermitDefaultValues();
        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:8085");
        allowedOrigins.add("http://81.177.6.228:8085");
        allowedOrigins.add("http://localhost:8080");
        allowedOrigins.add("http://81.177.6.228:8080");
        allowedOrigins.add("http://81.177.6.228:8086");
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        List<String> allowedMethods = new ArrayList<>();
        allowedMethods.add("OPTIONS");
        allowedMethods.add("DELETE");
        allowedMethods.add("POST");
        allowedMethods.add("GET");
        allowedMethods.add("PATCH");
        allowedMethods.add("PUT");
        corsConfiguration.setAllowedMethods(allowedMethods);
        List<String> exposedHeaders = new ArrayList<>();
        exposedHeaders.add("Content-Type");
        exposedHeaders.add("X-Requested-With");
        exposedHeaders.add("accept");
        exposedHeaders.add("Origin");
        exposedHeaders.add("Access-Control-Request-Method");
        exposedHeaders.add("Access-Control-Request-Headers");
        exposedHeaders.add("Access-Control-Allow-Origin");
        exposedHeaders.add("Access-Control-Allow-Credentials");
        corsConfiguration.setExposedHeaders(exposedHeaders);
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] matchers = new String[] {
            "/api/v1/auth/login", "/api/v1/auth/captcha", "/api/v1/account/register",
            "/api/v1/account/password/recovery", "/api/v1/account/email/recovery", "/api/v1/ws/**",
            "*/api/v1/change-password", "/api/v1/account/password/set", "/api/v1/account/password/reset",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/", "/actuator/*", "/api/v1/statistics/**",
            "/api/v1/account/email", "/api/v1/tg/**"
        };

        http.csrf().disable().httpBasic().disable().cors().configurationSource(corsConfigurationSource())
            .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeRequests().antMatchers(matchers).permitAll().anyRequest().authenticated()
            .and().addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
