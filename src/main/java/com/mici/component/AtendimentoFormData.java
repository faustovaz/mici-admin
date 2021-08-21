package com.mici.component;

import java.util.List;

import lombok.Data;

@Data
public class AtendimentoFormData {
	private String idCliente;
	private List<ServicoFormaData> servicos;
	private String cortesia;
	private String formaPgto;
	private String atendimentoObservacao;
}


