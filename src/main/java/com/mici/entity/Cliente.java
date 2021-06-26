package com.mici.entity;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.mici.dialect.SQLiteLocalDateConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nome;
	
	@Convert(converter = SQLiteLocalDateConverter.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "data_nascimento")
	private LocalDate dataNascimento;
	
	private String telefone;
	
	private String observacao;
	
	public Date getDataNascimentoAsDate() {
		return java.sql.Date.valueOf(this.getDataNascimento());
	}
}
