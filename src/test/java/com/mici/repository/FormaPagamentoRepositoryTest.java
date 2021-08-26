package com.mici.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mici.entity.FormaPagamento;

@ActiveProfiles("tests")
@SpringBootTest
class FormaPagamentoRepositoryTest {
	
	@Autowired
	FormaPagamentoRepository repository;

	@Test
	void deveRetornarAsPrincipaisFormasDePagamentosSalvas() {
		List<FormaPagamento> all = repository.findAll();
		assertThat(all.size()).isGreaterThan(0);
	}

}
