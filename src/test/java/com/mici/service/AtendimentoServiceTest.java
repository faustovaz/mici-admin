package com.mici.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.mici.builders.AtendimentoBuilder;
import com.mici.entity.Atendimento;
import com.mici.repository.FormaPagamentoRepository;

@ActiveProfiles("tests")
@SpringBootTest
class AtendimentoServiceTest {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	AtendimentoService atendimentoService;
	
	@Autowired
	ServicoService servicoService;
	
	@Autowired
	FormaPagamentoRepository formaPgtoRepo;
	
	@Autowired
	ClienteService clienteService;
	
	void cleanTables() {
		jdbcTemplate.execute("DELETE FROM servicos");
		jdbcTemplate.execute("DELETE FROM atendimentos");
		jdbcTemplate.execute("DELETE FROM itens_atendimento");		
	}
	
	void populateTables() {
		jdbcTemplate.execute("insert into servicos(nome) values('servico_1')");
		jdbcTemplate.execute("insert into servicos(nome) values('servico_2')");
		jdbcTemplate.execute("insert into servicos(nome) values('servico_3')");
		
		jdbcTemplate.execute("insert into clientes(nome, data_nascimento) values('quitino', '1983-11-02')");
		jdbcTemplate.execute("insert into clientes(nome, data_nascimento) values('michel',  '1983-11-02')");
	}
	
	@BeforeEach
	void setupEachTest() {
		cleanTables();
		populateTables();
	}
	
	@Test
	void deveGravarUmAtendimentoComDoisItensDeAtendimento() {
		List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from atendimentos");
		assertThat(result).isEmpty();
		
		Atendimento atendimento = new AtendimentoBuilder()
									.pago()
									.doCliente(clienteService.findById(1))
									.hoje()
									.comFormaDePagamento(formaPgtoRepo.findById(2).get())
									.comObservacao("Novo Atendimento")
									.comItemAtendimento()
										.comServico(servicoService.findById(1))
										.comPreco(new BigDecimal(25))
										.build()
									.comItemAtendimento()
										.comServico(servicoService.findById(2))
										.comPreco(new BigDecimal(20))
										.build()
									.build();
			
			
		atendimentoService.salvar(atendimento);
		List<Map<String, Object>> atendimentos = jdbcTemplate.queryForList("select * from atendimentos");
		assertThat(atendimentos.size()).isEqualTo(1);
		
		List<Map<String, Object>> itens = jdbcTemplate.queryForList("select * from itens_atendimento");
		assertThat(itens.size()).isEqualTo(2);
		
		Optional<Atendimento> atendimentoOptional = atendimentoService.findById(1);
		assertThat(atendimentoOptional.get().getValorAtendimento()).isEqualByComparingTo(new BigDecimal(45));
		assertThat(atendimentoOptional.get().getObservacao()).isEqualTo("Novo Atendimento");
		assertThat(atendimentoOptional.get().getItensAtendimento().size()).isEqualTo(2);
	}
	
	
	@Test
	void deveRetornarSomenteAtendimentosDoDiaCorrente() {
		Atendimento atendimento1 = new AtendimentoBuilder()
				.pago()
				.doCliente(clienteService.findById(1))
				.hoje()
				.comFormaDePagamento(formaPgtoRepo.findById(2).get())
				.comObservacao("Novo Atendimento 1")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento atendimento2 = new AtendimentoBuilder()
				.pago()
				.doCliente(clienteService.findById(2))
				.hoje()
				.comFormaDePagamento(formaPgtoRepo.findById(2).get())
				.comObservacao("Novo Atendimento 2")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento atendimento3 = new AtendimentoBuilder()
				.naoPago()
				.doCliente(clienteService.findById(1))
				.deOntem()
				.comFormaDePagamento(formaPgtoRepo.findById(2).get())
				.comObservacao("Novo Atendimento 3")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		List.of(atendimento1, atendimento2, atendimento3)
			.forEach(atendimento -> atendimentoService.salvar(atendimento));
		
		List<Atendimento> deHoje = atendimentoService.findAllAtendimentosDeHoje();
		assertThat(deHoje.size()).isEqualTo(2);
		assertThat(deHoje.get(0).getObservacao()).isEqualTo("Novo Atendimento 2");
	}
	
	
	@Test
	void deveRetornarSomenteAtendimentosNaoPagos() {
		Atendimento atendimento1 = new AtendimentoBuilder()
				.pago()
				.naoCortesia()
				.doCliente(clienteService.findById(1))
				.hoje()
				.comFormaDePagamento(formaPgtoRepo.findById(2).get())
				.comObservacao("Novo Atendimento 1")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento atendimento2 = new AtendimentoBuilder()
				.naoPago()
				.naoCortesia()
				.doCliente(clienteService.findById(2))
				.hoje()
				.comFormaDePagamento(formaPgtoRepo.findById(2).get())
				.comObservacao("Novo Atendimento 2")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento atendimento3 = new AtendimentoBuilder()
				.naoPago()
				.cortesia()
				.doCliente(clienteService.findById(2))
				.hoje()
				.comFormaDePagamento(formaPgtoRepo.findById(2).get())
				.comObservacao("Novo Atendimento 3")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		atendimentoService.salvar(atendimento1);
		atendimentoService.salvar(atendimento2);
		atendimentoService.salvar(atendimento3);
		
		List<Atendimento> naoPagos = atendimentoService.findAllNaoPagos();
		assertThat(naoPagos.size()).isEqualTo(1);
		assertThat(naoPagos.get(0).getObservacao()).isEqualTo("Novo Atendimento 2");
		
	}

}
