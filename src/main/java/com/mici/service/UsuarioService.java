package com.mici.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mici.entity.Usuario;

@Service
public class UsuarioService implements UserDetailsService {

	private JdbcTemplate jdbcTemplate;
	private PasswordEncoder encoder;
	
	public UsuarioService(JdbcTemplate template, PasswordEncoder encoder) {
		this.jdbcTemplate = template;
		this.encoder = encoder;
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
	
	public Boolean changePassword(String username, String oldPassword, String newPassword, String newPasswordConfirmed) {
		try {
			UserDetails usuario = this.loadUserByUsername(username);
			if(newPassword.equals(newPasswordConfirmed) && encoder.matches(oldPassword, usuario.getPassword())) {
				PreparedStatementCallback<Boolean> psc = (ps) -> {
					ps.setString(1, encoder.encode(newPasswordConfirmed));
					ps.setString(2, username);
					return ps.execute();
				};
				jdbcTemplate.execute("update usuarios set password = ? where username = ?", psc);
				return true;
			}
			return false;
		} catch(Exception e) {
			return false;
		}
	}
	
}