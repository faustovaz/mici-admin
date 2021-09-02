package com.mici.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import lombok.Data;

@Data
public class PagamentoDTO {

    private String nomeCliente;
    private LocalDate dataPagamento;
    private String formaPagamento;
    private BigDecimal valorPagamento;
    private Integer idAtendimento;
    
    public Date getDataPagamentoAsDate() {
        return java.sql.Date.valueOf(dataPagamento);
    }
    
}
