package com.mici.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.mici.dialect.SQLiteLocalDateTimeConverter;

import lombok.Data;

@Data
@Entity
@Table(name = "agenda")
public class Agendamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty
	private String cliente;
	
	private String lembrete;
	
	@NotNull
	@Convert(converter = SQLiteLocalDateTimeConverter.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "data_agendamento")
	private LocalDateTime agendamento;
	
	public static Agendamento from(String cliente, String lembrete, String date, String time) {
		var dataAgendamento = LocalDateTime.parse(String.format("%sT%s", date, time));
		var a = new Agendamento();
		a.setCliente(cliente);
		a.setLembrete(lembrete);
		a.setAgendamento(dataAgendamento);
		return a;
	}
	
	public String getHora() {
		return this.agendamento.format(DateTimeFormatter.ISO_TIME);
	}
	
	public String getData() {
		return this.agendamento.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
	
	
}
