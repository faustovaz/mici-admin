package com.mici.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
import com.mici.dialect.SQLiteLocalDateTimeConverter;

import lombok.Data;

@Data
@Entity
@Table(name = "atendimentos")
public class Atendimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "pagamento_realizado")
	private boolean pagamentoRealizado;
	
	@Convert(converter = SQLiteLocalDateConverter.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "dia_do_atendimento")
	private LocalDate diaDoAtendimento;
	
	private String observacao;
	
	@Column(name = "valor_atendimento")
	private BigDecimal valorAtendimento = new BigDecimal(0);
	
	@OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<ItemAtendimento> itensAtendimento;
	
	@ManyToOne
	@JoinColumn(name = "id_forma_pagamento")
	private FormaPagamento formaPagamento;
	
	@Column(name = "valor_pago")
	private BigDecimal valorPago;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	private boolean cortesia;

	@Convert(converter = SQLiteLocalDateTimeConverter.class)
	@Column(name = "ultima_atualizacao")
	private LocalDateTime ultimaAtualizacao;
	
	@Column(name = "criado_por")
	private String criadoPor;
	
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
	
	public Date getDiaDoAtendimentoAsDate() {
		return java.sql.Date.valueOf(getDiaDoAtendimento());
	}
	
	public BigDecimal getTotalAtendimento() {
		BigDecimal pretoAtendimento = this.itensAtendimento
			.stream()
			.map(item -> item.getPrecoAplicado())
			.reduce(new BigDecimal(0), (total, preco) -> total.add(preco));
		return pretoAtendimento;
	}
	
}
