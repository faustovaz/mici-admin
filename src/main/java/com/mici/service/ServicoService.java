package com.mici.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mici.entity.Servico;
import com.mici.repository.ServicoRepository;

@Service
public class ServicoService {

	private ServicoRepository repository;
	
	public ServicoService(ServicoRepository repository) {
		this.repository = repository;
	}
	
	public Servico salvar(Servico servico) {
		return this.repository.save(servico);
	}
	
	public List<Servico> findAll() {
		return this.repository.findAllNaoRemovidos(Sort.by(Direction.ASC, "nome"));
	}
	
	public Servico findById(Integer id) {
		Optional<Servico> servico = this.repository.findById(id);
		return servico.isPresent() ? servico.get() : new Servico();
	}

	public void remover(Integer id, String usuario) {
		var servico = this.findById(id);
		if (Objects.nonNull(servico.getId())) {
			servico.setAsRemovido();
			servico.setCriadoPor(usuario);
			this.salvar(servico);
		}
	}
	
}
