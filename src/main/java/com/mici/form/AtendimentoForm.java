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
    
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAtendimento;
	
	private boolean seraCobrado;
	
	private boolean cronograma;
	
	private Integer idFormaPgto;
	
	private BigDecimal valorPago;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataPgto;
	
	private String atendimentoObservacao;
}


