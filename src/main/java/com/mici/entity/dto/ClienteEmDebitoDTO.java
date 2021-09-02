package com.mici.entity.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ClienteEmDebitoDTO {
    private String nome;
    private Integer IdCliente;
    private BigDecimal totalDebito;
}
