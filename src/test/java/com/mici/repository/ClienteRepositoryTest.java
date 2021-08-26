package com.mici.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.mici.builders.ClienteBuilder;
import com.mici.service.ClienteService;

@ActiveProfiles("tests")
@SpringBootTest
class ClienteRepositoryTest {

	@Autowired
	JdbcTemplate template;
	
	@Autowired
	ClienteRepository repository;
	
	@Autowired
	ClienteService service;
	
	@BeforeEach
	public void cleanUp() {
		template.execute("DELETE FROM clientes");
	}
	
	@Test
	void deveRetornarOsAniversariantesDoMes() {
		List.of(
			new ClienteBuilder()
				.nome("Cliente1")
				.dataNascimento("1983-11-27")
				.anyObservacao()
				.anyTelefone()
				.build(),
			new ClienteBuilder()
				.nome("Cliente2")
				.dataNascimento("1987-11-02")
				.anyObservacao()
				.anyTelefone()
				.build(),
			new ClienteBuilder()
				.nome("Cliente3")
				.dataNascimento("1999-07-30")
				.anyObservacao()
				.anyTelefone()
				.build(),
			new ClienteBuilder()
				.nome("Cliente1")
				.dataNascimento("2002-12-14")
				.anyObservacao()
				.anyTelefone()
				.build()
		).forEach(cliente -> service.save(cliente));
		
		var aniversariantes = repository.getAniversariantesDoMes(11);	
		assertThat(aniversariantes.size()).isEqualTo(2);
	}
	
	@Test
	void deveRetornarSomenteAniversariantesDaSemana() {
		List.of(
				new ClienteBuilder().nome("Cliente1").dataNascimento("1983-08-26").build(),
				new ClienteBuilder().nome("Cliente2").dataNascimento("1988-08-30").build(),
				new ClienteBuilder().nome("Cliente3").dataNascimento("2001-08-23").build(),
				new ClienteBuilder().nome("Cliente4").dataNascimento("2000-09-09").build(),
				new ClienteBuilder().nome("Cliente5").dataNascimento("1979-09-11").build(),
				new ClienteBuilder().nome("Cliente6").dataNascimento("1999-09-01").build(),
				new ClienteBuilder().nome("Cliente7").dataNascimento("1999-09-02").build(),
				new ClienteBuilder().nome("Cliente8").dataNascimento("1979-02-02").build(),
				new ClienteBuilder().nome("Cliente9").dataNascimento("1983-11-27").build(),
				new ClienteBuilder().nome("Cliente10").dataNascimento("1998-01-01").build(),
				new ClienteBuilder().nome("Cliente11").dataNascimento("2002-09-03").build(),
				new ClienteBuilder().nome("Cliente12").dataNascimento("2004-12-14").build()
			).forEach(cliente -> service.save(cliente));
		//Pega do dia 26/08 até 02/09, ou seja 7 dias
		var aniversariantes = service.getAniversariantesDaSemana(LocalDate.parse("2021-08-26"));
		assertThat(aniversariantes.size()).isEqualTo(4);
	}
}
