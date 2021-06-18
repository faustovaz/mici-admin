package com.mici.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mici.entity.Cliente;
import com.mici.repository.ClienteRepository;

@Service
public class ClienteService {

	private  ClienteRepository repository;

	public ClienteService(ClienteRepository repository) {
		this.repository = repository;
	}
	
	public Cliente save(Cliente cliente) {
		return this.repository.save(cliente);
	}
	
	public List<Cliente> findAll() {
		return this.repository.findAll(Sort.by(Direction.DESC, "id"));
	}

	public Cliente findById(Integer id) {
		Optional<Cliente> optional = this.repository.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return new Cliente();
	}
	
	public List<Cliente> findByName(String name) {
		return this.repository.findByNameContainingIgnoreCase(name);
	}
	
}
