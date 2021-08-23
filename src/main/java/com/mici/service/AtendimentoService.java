package com.mici.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mici.entity.Atendimento;
import com.mici.entity.Cliente;
import com.mici.repository.AtendimentoRepository;

@Service
public class AtendimentoService {

	private AtendimentoRepository atendimentoRepository;

	public AtendimentoService(AtendimentoRepository repository) {
		this.atendimentoRepository = repository;
	}
	
	public Atendimento salvar(Atendimento atendimento) {
		atendimento.setUltimaAtualizacao(LocalDateTime.now());
		return this.atendimentoRepository.save(atendimento);
	}

	public Optional<Atendimento> findById(Integer id) {
		return this.atendimentoRepository.findById(id);
	}

	public List<Atendimento> findAllByCliente(Cliente cliente) {
		return this.atendimentoRepository.findAllByCliente(cliente, Sort.by(Direction.DESC, "id"));
	}
	
	@Transactional
	public void remove(Atendimento atendimento) {
		this.atendimentoRepository.deleteById(atendimento.getId());
	}

}
