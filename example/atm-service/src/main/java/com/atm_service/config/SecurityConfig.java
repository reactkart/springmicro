package com.atm_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import feign.RequestInterceptor;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	public static final String BEARER_TYPE = "Bearer ";

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable()
				.authorizeRequests().antMatchers("/", "/login**").permitAll().antMatchers("/atm/getAvailableBalance")
				.hasRole("USER").antMatchers("/atm/welcome").hasAnyRole("ADMIN", "USER").and().oauth2ResourceServer()
				.jwt().jwtAuthenticationConverter(new KeycloakAuthoritiesExtractor());

	}

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			requestTemplate.header(AUTHORIZATION_HEADER_KEY,
					BEARER_TYPE + ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
							.getToken().getTokenValue());
		};
	}
}