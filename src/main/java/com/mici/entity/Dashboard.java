package com.mici.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dashboard {
	private Long totalAtendimentosDia;
	private Long totalAtendimentosSemana;
	private Long totalAtendimentos;
	private List<Cliente> aniversariantesDaSemana;
	private List<Cliente> aniversariantesDoMes;
}
