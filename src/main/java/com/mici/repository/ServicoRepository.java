package com.mici.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mici.entity.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Integer> {

	@Query("select s from Servico s where s.removido is false")	
	public List<Servico> findAllNaoRemovidos(Sort sort);
	
}
