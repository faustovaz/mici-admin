package com.mici.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.mici.dialect.SQLiteLocalDateConverter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@EqualsAndHashCode(exclude = "atendimento")
@Entity
@Table(name = "pagamentos_atendimento")
public class PagamentoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_atendimento")
    private Atendimento atendimento;
    
    @ManyToOne
    @JoinColumn(name = "id_forma_pagamento")
    private FormaPagamento formaPagamento;
    
    @Column(name = "valor_pagamento")
    private BigDecimal valorPagamento;
    
    @Convert(converter = SQLiteLocalDateConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dia_do_pagamento")
    private LocalDate diaDoPagamento;
    
    public Date getDiaDoPagamentoAsDate() {
        return java.sql.Date.valueOf(this.getDiaDoPagamento());
    }
    
}
