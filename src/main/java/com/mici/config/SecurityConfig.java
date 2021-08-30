package com.mici.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

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
			.authorizeRequests().antMatchers("/webjars/**", "/img/**", "/favicon.ico").permitAll()
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
		
}
