package com.mici.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("tests")
class AgendamentoTest {

	@Test
	void deveCriarAgendamentosAPartirDeStrings() {
		var agendamento = Agendamento.from("Fausto", "Procedimento padrão", "2021-06-25", "14:00");
		assertThat(agendamento).isNotNull();
		assertThat(agendamento.getAgendamento()).isNotNull();
		assertThat(agendamento.getAgendamento()).isEqualTo(LocalDateTime.parse("2021-06-25T14:00"));
	}
	
	@Test
	void deveLancarExceptionAoCriarAgendamentoComDataEmFormatoNaoAceitavel() {
		assertThrows(DateTimeParseException.class, () -> {
			Agendamento.from("Fausto", "Procedimento padrão", "27/11/1983", "14:30");
		});
	}

}
