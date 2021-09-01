package com.mici.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@EqualsAndHashCode(exclude = "atendimento")
@Entity
@Table(name = "itens_atendimento")
public class ItemAtendimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "id_servico")
	private Servico servico;
	
	@Column(name = "preco_aplicado")
	private BigDecimal precoAplicado;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "id_atendimento")
	private Atendimento atendimento;
}
