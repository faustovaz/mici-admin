package com.mici.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.mici.entity.Dashboard;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DashboardService {

	private ClienteService clienteService;
	private AtendimentoService atendimentoService;
	
	public Dashboard getDashboard() {
		return new Dashboard(
				atendimentoService.countTotalAtendimentosDeHoje(), 
				atendimentoService.countTotalAtendimentosDaSemana(), 
				atendimentoService.countTotalAtendimentos(), 
				clienteService.getAniversariantesDaSemana(LocalDate.now()), 
				clienteService.getAniversariantesDoMesAtual());
	}
	
}
