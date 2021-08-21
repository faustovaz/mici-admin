package com.mici.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.mici.dialect.SQLiteLocalDateConverter;

import lombok.Data;

@Data
@Entity
@Table(name = "atendimentos")
public class Atendimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "pagamento_realizado")
	private Boolean pagamentoRealizado;
	
	@Convert(converter = SQLiteLocalDateConverter.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "dia_do_atendimento")
	private LocalDate diaDoAtendimento;
	
	private String observacao;
	
	@Column(name = "valor_atendimento")
	private BigDecimal valorAtendimento = new BigDecimal(0);
	
	@OneToMany(mappedBy = "atendimento", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private List<ItemAtendimento> itensAtendimento;
	
	@ManyToOne
	@JoinColumn(name = "id_forma_pagamento")
	private FormaPagamento formaPagamento;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	private Boolean cortesia;
	
	public void adicionarItemAtendimento(ItemAtendimento itemAtendimento){
		if (Objects.isNull(itensAtendimento)) {
			this.itensAtendimento = new ArrayList<>();
		}
		itemAtendimento.setAtendimento(this);
		valorAtendimento = valorAtendimento.add(itemAtendimento.getPrecoAplicado());
		this.itensAtendimento.add(itemAtendimento);
	}
	
	public void adicionarItensAtendimento(List<ItemAtendimento> itens){
		itens.forEach(item -> this.adicionarItemAtendimento(item));
	}
	
}
