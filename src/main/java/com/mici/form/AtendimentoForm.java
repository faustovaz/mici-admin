package com.mici.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class AtendimentoForm {
	private String idCliente;
	private List<ServicoForm> servicos;
	private boolean cortesia;
	private String formaPgto;
	private String atendimentoObservacao;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataAtendimento;
	private boolean pagamentoRealizado;
	private BigDecimal valorPago;
}


