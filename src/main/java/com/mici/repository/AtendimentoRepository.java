package com.mici.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mici.entity.Atendimento;
import com.mici.entity.Cliente;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Integer>{

	List<Atendimento> findAllByCliente(Cliente cliente, Sort sort);

	List<Atendimento> findAllByDiaDoAtendimento(LocalDate now, Sort sort);

	List<Atendimento> findAllByPagamentoRealizadoFalseAndCortesiaFalse(Sort sort);

	List<Atendimento> findByDiaDoAtendimentoBetween(LocalDate inicio, LocalDate fim, Sort by);

}
