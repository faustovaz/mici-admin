package com.mici.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mici.entity.Agendamento;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

	List<Agendamento> findByDataHoraAgendamentoBetween(LocalDateTime startDate, LocalDateTime endDate);
}
