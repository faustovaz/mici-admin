package com.mici.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mici.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	@Query("select c from Cliente c where UPPER(c.nome) like %:name%")
	public List<Cliente> findByNameContainingIgnoreCase(@Param("name") String name);

	@Query(value = "select * from clientes where cast(strftime('%m', data_nascimento) as int) = :mes order by nome", nativeQuery = true)
	public List<Cliente> getAniversariantesDoMes(@Param("mes") Integer mes);
	
}
