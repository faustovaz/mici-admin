package com.mici.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	public List<Cliente> getAniversariantesDaSemana(LocalDate dataInicial) {
		var dataFinal = dataInicial.plusDays(8);
		var totalAniversariantes = repository.getAniversariantesDoMes(dataInicial.getMonthValue());
		var aniversariantes = new ArrayList<Cliente>();
		
		if(dataInicial.getMonth() != dataFinal.getMonth()) {
			totalAniversariantes.addAll(repository.getAniversariantesDoMes(dataFinal.getMonthValue()));
		}
		
		dataInicial.datesUntil(dataFinal).forEach(data -> {
			aniversariantes.addAll(
				totalAniversariantes
				.stream()
				.filter(cliente -> cliente.getDataNascimento().getDayOfMonth() == data.getDayOfMonth())
				.collect(Collectors.toList())
			);
		});
		
		return aniversariantes;
	}

	public List<Cliente> getAniversariantesDoMesAtual() {
		var hoje = LocalDate.now();
		return repository.getAniversariantesDoMes(hoje.getMonthValue());
	}
	
}
