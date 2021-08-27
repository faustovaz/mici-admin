package com.mici.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mici.entity.Usuario;

@Service
public class UsuarioService implements UserDetailsService {

	private JdbcTemplate jdbcTemplate;
	
	public UsuarioService(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			Usuario usuario = jdbcTemplate.queryForObject(
					"select username, password from usuarios where username = ?", 
					new BeanPropertyRowMapper<Usuario>(Usuario.class),
					username);
			return usuario;
		}
		catch(EmptyResultDataAccessException e) {
			throw new UsernameNotFoundException("Usuário ou senha inválidos");
		}
	}
	
}