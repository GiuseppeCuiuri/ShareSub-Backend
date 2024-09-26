package com.example.progettonegozio.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors() // Abilita il supporto CORS
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/check/**").permitAll()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/products/**").permitAll()
                .antMatchers("/purchasing/**").permitAll()
                .anyRequest().authenticated()
                .and();

    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:4200"); // Indirizzo frontend
        configuration.addAllowedHeader("*"); // Consente tutte le intestazioni
        configuration.addAllowedMethod("*"); // Consente tutti i metodi HTTP (GET, POST, PUT, DELETE, ecc.)

        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
