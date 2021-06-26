package com.mici.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.github.javafaker.Faker;
import com.mici.entity.Agendamento;

@ActiveProfiles("tests")
@SpringBootTest
class AgendamentoServiceTest {

	@Autowired
	AgendamentoService agendamentoService;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	Faker faker;
	
	@BeforeEach
	public void cleanDatabase() throws DataAccessException{
		faker = new Faker();
		jdbcTemplate.execute("DELETE FROM agenda");
	}
	
	@Test
	void deveSalvarUmAgendamentoNoBancoERecuperalo() {
		var dia25 = LocalDate.parse("2021-06-25");
		var dia29 = LocalDate.parse("2021-06-29");
		var diaNaoAgendado = LocalDate.parse("2021-06-01");
		
		var agendamentos = List.of(
			Agendamento.from(faker.name().firstName(), faker.lorem().word(), "2021-06-25", "09:30"),
			Agendamento.from(faker.name().firstName(), faker.lorem().word(), "2021-06-25", "11:30"),
			Agendamento.from(faker.name().firstName(), faker.lorem().word(), "2021-06-26", "15:30"),
			Agendamento.from(faker.name().firstName(), faker.lorem().word(), "2021-06-27", "15:30"),
			Agendamento.from(faker.name().firstName(), faker.lorem().word(), "2021-06-28", "15:30"),
			Agendamento.from(faker.name().firstName(), faker.lorem().word(), "2021-06-29", "15:30"),
			Agendamento.from(faker.name().firstName(), faker.lorem().word(), "2021-06-30", "15:30"));
		
		agendamentos.forEach((a) -> agendamentoService.agendar(a));
		assertThat(agendamentoService.getTodosDoDia(diaNaoAgendado)).isEmpty();
		assertThat(agendamentoService.getTodosDoDia(dia25)).isNotEmpty();
		assertThat(agendamentoService.getTodosDoDia(dia29)).hasAtLeastOneElementOfType(Agendamento.class);
	}
	
}
