package com.mici.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mici.entity.Agendamento;
import com.mici.repository.AgendamentoRepository;

@Service
public class AgendamentoService {

	private AgendamentoRepository repository;
	
	public AgendamentoService(AgendamentoRepository repository) {
		this.repository = repository;
	}
	
	public Agendamento agendar(Agendamento agendamento) {
		return this.repository.save(agendamento);
	}

	public List<Agendamento> getTodosDoDia(LocalDate date) {
		var startDate = LocalDateTime.of(date, LocalTime.MIN);
		var endDate = LocalDateTime.of(date, LocalTime.MAX);
		return this.repository.findByAgendamentoBetween(startDate, endDate, Sort.by(Sort.Direction.ASC, "agendamento"));
	}

	public List<Agendamento> findAll() {
		return this.repository.findAll();
	}

	public void deleteById(Integer id) {
		this.repository.deleteById(id);
	}
	
}
