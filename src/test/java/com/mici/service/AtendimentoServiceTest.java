package com.mici.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.mici.builders.AtendimentoBuilder;
import com.mici.entity.Atendimento;
import com.mici.repository.AtendimentoRepository;
import com.mici.repository.FormaPagamentoRepository;

@ActiveProfiles("tests")
@SpringBootTest
class AtendimentoServiceTest {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	AtendimentoService atendimentoService;
	
	@Autowired
	AtendimentoRepository atendimentoRepository;
	
	@Autowired
	ServicoService servicoService;
	
	@Autowired
	FormaPagamentoRepository formaPgtoRepo;
	
	@Autowired
	ClienteService clienteService;
	
	void cleanTables() {
	    jdbcTemplate.execute("delete from clientes");
		jdbcTemplate.execute("DELETE FROM servicos");
		jdbcTemplate.execute("DELETE FROM atendimentos");
		jdbcTemplate.execute("DELETE FROM itens_atendimento");		
		jdbcTemplate.execute("DELETE FROM pagamentos_atendimento");
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
									.doCliente(clienteService.findById(1))
									.deHoje()
									.comObservacao("Novo Atendimento")
									.comItemAtendimento()
										.comServico(servicoService.findById(1))
										.comPreco(new BigDecimal(25))
										.build()
									.comItemAtendimento()
										.comServico(servicoService.findById(2))
										.comPreco(new BigDecimal(20))
										.build()
									.comPagamentoAtendimento()
									    .comValor(new BigDecimal(20))
									    .comFormaPagamento(formaPgtoRepo.findById(1).get())
		                                .pagoHoje()
		                                .build()
									.build();
			
			
		atendimentoService.salvar(atendimento);
		var atendimentos = jdbcTemplate.queryForList("select * from atendimentos");
		assertThat(atendimentos.size()).isEqualTo(1);
        
		var itens = jdbcTemplate.queryForList("select * from itens_atendimento");
        assertThat(itens.size()).isEqualTo(2);
        
        var pagamentos = jdbcTemplate.queryForList("select * from pagamentos_atendimento");
        assertThat(pagamentos.size()).isEqualTo(1);
        
        var atendimentoOptional = atendimentoService.findById(1).get();
        assertThat(atendimentoOptional.getValorAtendimento()).isEqualByComparingTo(new BigDecimal(45));
        assertThat(atendimentoOptional.getValorPago()).isEqualByComparingTo(new BigDecimal(20));
        assertThat(atendimentoOptional.isPagamentoRealizado()).isFalse();
        assertThat(atendimentoOptional.getObservacao()).isEqualTo("Novo Atendimento");
        assertThat(atendimentoOptional.getItensAtendimento().size()).isEqualTo(2);      
	}
	
	
	@Test
	void deveRetornarSomenteAtendimentosDoDiaCorrente() {
		Atendimento atendimento1 = new AtendimentoBuilder()
				.doCliente(clienteService.findById(1))
				.deHoje()
				.comObservacao("Novo Atendimento 1")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento atendimento2 = new AtendimentoBuilder()
				.doCliente(clienteService.findById(2))
				.deHoje()
				.comObservacao("Novo Atendimento 2")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento atendimento3 = new AtendimentoBuilder()
				.doCliente(clienteService.findById(1))
				.deOntem()
				.comObservacao("Novo Atendimento 3")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		List.of(atendimento1, atendimento2, atendimento3).forEach(atendimento -> atendimentoService.salvar(atendimento));
		
		List<Atendimento> deHoje = atendimentoService.findAllAtendimentosDeHoje();
		assertThat(deHoje.size()).isEqualTo(2);
		assertThat(deHoje.get(0).getObservacao()).isEqualTo("Novo Atendimento 2");
	}
	
	
	@Test
	void deveRetornarSomenteAtendimentosNaoPagos() {
		Atendimento pago1 = new AtendimentoBuilder()
				.seraCobrado()
				.doCliente(clienteService.findById(1))
				.deHoje()
				.comObservacao("Pago")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.comPagamentoAtendimento()
				    .comValor(new BigDecimal(25))
				    .pagoHoje()
				    .comFormaPagamento(formaPgtoRepo.findById(2).get())
				    .build()
				.build();
		
		Atendimento naoPago1 = new AtendimentoBuilder()
				.seraCobrado()
				.doCliente(clienteService.findById(2))
				.deHoje()
				.comObservacao("Nao pago")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento naoSeraCobrado = new AtendimentoBuilder()
				.naoSeraCobrado()
				.doCliente(clienteService.findById(2))
				.deHoje()
				.comObservacao("Nao sera cobrado")
				.comItemAtendimento()
					.comServico(servicoService.findById(1))
					.comPreco(new BigDecimal(25))
					.build()
				.build();
		
		Atendimento naoPago2 = new AtendimentoBuilder()
		        .seraCobrado()
		        .doCliente(clienteService.findById(2))
		        .deOntem()
		        .comObservacao("Nao pago")
		        .comItemAtendimento()
		            .comServico(servicoService.findById(1))
		            .comPreco(new BigDecimal(100))
		            .build()
	            .comPagamentoAtendimento()
	                .comFormaPagamento(formaPgtoRepo.findById(1).get())
	                .comValor(new BigDecimal(70))
	                .pagoOntem()
	                .build()
                .build();
		
		atendimentoService.salvar(pago1);
		atendimentoService.salvar(naoPago1);
		atendimentoService.salvar(naoSeraCobrado);
		atendimentoService.salvar(naoPago2);
		
		List<Atendimento> naoPagos = atendimentoService.findAllNaoPagos();
		assertThat(naoPagos.size()).isEqualTo(2);
		assertThat(naoPagos.get(0).getObservacao()).isEqualTo("Nao pago");
	}
	
	
	@Test
	void deveRetornarTotalCorretoDeAtendimentosDoDia() {
		var atendimento1 = new AtendimentoBuilder()
			.seraCobrado()
			.deOntem()
			.doCliente(clienteService.findById(1))
			.comObservacao("Atendimento 1")
			.build();
		var atendimento2 = new AtendimentoBuilder()
			.naoSeraCobrado()
			.deHoje()
			.doCliente(clienteService.findById(2))
			.comObservacao("Atendimento 2")
			.build();
		var atendimento3 = new AtendimentoBuilder()
			.seraCobrado()
			.doDia("2021-08-20")
			.doCliente(clienteService.findById(1))
			.comObservacao("Atendimento 3")
			.build();
		
		List.of(atendimento1, atendimento2, atendimento3).forEach(a -> atendimentoService.salvar(a));
		Long total = atendimentoService.countTotalAtendimentosDeHoje();
		assertThat(total).isEqualTo(1);
	}
	
	
	@Test
	void deveRetornarTotalCorretoDeAtendimentoDaSemana() {
		List.of(
				new AtendimentoBuilder()
					.naoSeraCobrado()
					.doDia("2021-08-26")
					.doCliente(clienteService.findById(2))
					.comObservacao("Atendimento 1")
					.build(),
				new AtendimentoBuilder()
					.naoSeraCobrado()
					.doDia("2021-08-22")
					.doCliente(clienteService.findById(2))
					.comObservacao("Atendimento 2")
					.build(),
				new AtendimentoBuilder()
					.naoSeraCobrado()
					.doDia("2021-08-23")
					.doCliente(clienteService.findById(2))
					.comObservacao("Atendimento 3")
					.build(),
				new AtendimentoBuilder()
					.naoSeraCobrado()
					.doDia("2021-08-21")
					.doCliente(clienteService.findById(2))
					.comObservacao("Atendimento 1")
					.build()		
		).forEach(atendimento -> atendimentoService.salvar(atendimento));
		
		var dataFinal = LocalDate.parse("2021-08-26");
		var dataInicial = dataFinal.minusDays(dataFinal.getDayOfWeek().getValue());
		var totalAtendimentos = atendimentoRepository.countByDiaDoAtendimentoBetween(dataInicial, dataFinal);
		assertThat(totalAtendimentos).isEqualTo(3);
	}
	
	@Test
	void deveAveriguarQuePagamentoEstaPago() {
	    var atendimento = new AtendimentoBuilder()
    	        .seraCobrado()
    	        .doDia("2021-07-30")
    	        .doCliente(clienteService.findById(1))
    	        .comItemAtendimento()
    	            .comServico(servicoService.findById(1))
    	            .comPreco(new BigDecimal(20))
    	            .build()
    	        .comItemAtendimento()
    	            .comServico(servicoService.findById(2))
    	            .comPreco(new BigDecimal(20))
    	            .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comFormaPagamento(formaPgtoRepo.findById(1).get())
                    .comValor(new BigDecimal(20))
                    .build()
                .comPagamentoAtendimento()
                    .pagoHoje()
                    .comFormaPagamento(formaPgtoRepo.findById(2).get())
                    .comValor(new BigDecimal(20))
                .build()
            .build();
	    
	    atendimentoService.salvar(atendimento);
	    var atendimentoSalvo = atendimentoService.findById(1).get();
	    assertThat(atendimentoSalvo.isPagamentoRealizado()).isTrue();
	    assertThat(atendimentoSalvo.getValorPago()).isEqualByComparingTo(new BigDecimal(40));
	    assertThat(atendimentoSalvo.getValorAtendimento()).isEqualByComparingTo(new BigDecimal(40));
	}
	

}
