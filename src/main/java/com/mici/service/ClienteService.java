package com.mici.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mici.entity.Cliente;
import com.mici.repository.ClienteRepository;

@Service
public class ClienteService {

	private  ClienteRepository repository;

	public ClienteService(ClienteRepository repository) {
		this.repository = repository;
	}
	
	public Cliente salvar(Cliente cliente) {
		return this.repository.save(cliente);
	}
	
	public List<Cliente> findAll() {
		return this.repository.findAll();
	}

	public Object findById(Integer id) {
		Optional<Cliente> optional = this.repository.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return new Cliente();
	}
	
}
