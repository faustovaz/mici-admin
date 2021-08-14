package com.mici.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mici.entity.Atendimento;
import com.mici.repository.AtendimentoRepository;

@Service
public class AtendimentoService {

	private AtendimentoRepository atendimentoRepository;

	public AtendimentoService(AtendimentoRepository repository) {
		this.atendimentoRepository = repository;
	}
	
	public Atendimento cadastrar(Atendimento atendimento) {
		return this.atendimentoRepository.save(atendimento);
	}

	public Optional<Atendimento> findById(Integer id) {
		return this.atendimentoRepository.findById(id);
	}

}
