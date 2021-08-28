package com.mici.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mici.service.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter  {

	private UsuarioService usuarioService;
	private PasswordEncoder passwordEncoder;
	
	public SecurityConfig(UsuarioService usuarioService, PasswordEncoder encoder) {
		this.usuarioService = usuarioService;
		this.passwordEncoder = encoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
			.and()
				.authorizeRequests().antMatchers("/webjars/**").permitAll()
			.and()
				.formLogin().loginPage("/login").permitAll()
			.and()
				.authorizeRequests().anyRequest().authenticated()
			.and()
				.logout().logoutUrl("/logout")
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.logoutSuccessUrl("/login");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(usuarioService).passwordEncoder(passwordEncoder);
	}
	
	@Bean
	public CorsConfigurationSource corsConfig() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:8080"));
		config.setAllowedMethods(List.of("GET", "POST"));
		config.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
}
