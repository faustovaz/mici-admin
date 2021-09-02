package com.mici.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mici.entity.dto.PagamentoDTO;
import com.mici.entity.dto.RelatorioPagamentosDTO;
import com.mici.repository.PagamentosRepository;

@Service
public class PagamentosService {

    PagamentosRepository repository;
    
    public PagamentosService(PagamentosRepository repository) {
        this.repository = repository;
    }
    
    public List<PagamentoDTO> getAllPagamentosEntreOsDias(LocalDate datainicial, LocalDate dataFinal) {
        return repository.findPagamentosEntreOsDias(datainicial, dataFinal);
    }

    public List<PagamentoDTO> getAllPagamentosDeHoje() {
        return repository.findPagamentosEntreOsDias(LocalDate.now(), LocalDate.now());
    }

    public RelatorioPagamentosDTO gerarRelatorioPagamentosDeHoje() {
        return new RelatorioPagamentosDTO(getAllPagamentosDeHoje());
    }

    public RelatorioPagamentosDTO gerarRelatorioPagamentoDosDias(LocalDate dataInicial, LocalDate dataFinal) {
        return new RelatorioPagamentosDTO(getAllPagamentosEntreOsDias(dataInicial, dataFinal));
    }
    
}
