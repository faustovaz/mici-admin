package com.mici.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mici.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

}
