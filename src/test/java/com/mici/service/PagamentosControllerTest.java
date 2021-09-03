package com.mici.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.github.javafaker.Faker;
import com.mici.builders.AtendimentoBuilder;
import com.mici.repository.FormaPagamentoRepository;

@ActiveProfiles("tests")
@SpringBootTest
class PagamentosControllerTest {

    @Autowired
    FormaPagamentoRepository formaPgtos;
    
    @Autowired
    AtendimentoService atendimentos;
    
    @Autowired
    PagamentosService pagamentos;
    
    @Autowired
    ClienteService clientes;
    
    @Autowired
    ServicoService servicos;
    
    @Autowired
    JdbcTemplate db;
    
    Faker faker;
    
    @BeforeEach
    void cleanUp() {
        faker = new Faker();
        db.execute("DELETE from clientes");
        db.execute("DELETE FROM servicos");
        db.execute("DELETE FROM atendimentos");
        db.execute("DELETE FROM itens_atendimento");      
        db.execute("DELETE FROM pagamentos_atendimento");
        
        db.execute("insert into servicos(nome) values('servico_1')");
        db.execute("insert into servicos(nome) values('servico_2')");
        db.execute("insert into servicos(nome) values('servico_3')");  
        
        db.execute("insert into clientes(nome, data_nascimento) values('John', '1983-11-02')");
        db.execute("insert into clientes(nome, data_nascimento) values('Rambo', '1983-11-02')");
        db.execute("insert into clientes(nome, data_nascimento) values('Conan', '1983-11-02')");
    }
    
    @Test
    void deveRetornarListaVaziaDePagamentoDoDia() {
        var atendimentoNaoPagoDoJohn = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(1))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comValor(BigDecimal.valueOf(25))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                    
                .build();
        
        var atendimentoPagoDoConan = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(3))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(25))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comValor(BigDecimal.valueOf(25))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()
                .build();
        
        var atendimentoPagoDoRambo = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(2))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(150))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comValor(BigDecimal.valueOf(150))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                    
                .build();        
        
        List.of(
            atendimentoNaoPagoDoJohn, 
            atendimentoPagoDoConan, 
            atendimentoPagoDoRambo).forEach(atendimento -> atendimentos.salvar(atendimento));
        
        var pagamentosDeHoje = pagamentos.gerarRelatorioPagamentosDeHoje();
        assertThat(pagamentosDeHoje.getPagamentos()).isEmpty();
        assertThat(pagamentosDeHoje.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(0));
    }
    
    @Test
    void deveRetornarTodosOsPagamentosDeHoje() {
        var atendimentoNaoPagoDoJohn = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(1))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comValor(BigDecimal.valueOf(25))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                    
                .build();
        
        var pagamentoDeOntem = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(1))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comValor(BigDecimal.valueOf(50))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                    
                .build();        
        
        var atendimentoPagoDoConan = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(3))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(25))
                    .build()
                .comPagamentoAtendimento()
                    .pagoHoje()
                    .comValor(BigDecimal.valueOf(25))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()
                .build();
        
        var atendimentoPagoDoRambo = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(2))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(150))
                    .build()
                .comPagamentoAtendimento()
                    .pagoHoje()
                    .comValor(BigDecimal.valueOf(150))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                    
                .build();        
        
        List.of(
            atendimentoNaoPagoDoJohn, 
            pagamentoDeOntem,
            atendimentoPagoDoConan, 
            atendimentoPagoDoRambo).forEach(atendimento -> atendimentos.salvar(atendimento));
        
        var relatorioPagamentosDTO = pagamentos.gerarRelatorioPagamentosDeHoje();
        assertThat(relatorioPagamentosDTO.getPagamentos().size()).isEqualTo(2);
        assertThat(relatorioPagamentosDTO.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(175));
    }
    
    @Test
    void deveRetornarTodosOsPagamentosEntreDatas() {
        var hoje = LocalDate.now();
        var ontem = hoje.minusDays(1);
        
        var atendimentoNaoPagoDoJohn = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(1))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comValor(BigDecimal.valueOf(25))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                    
                .build();
        
        var pagamentoDeOntem = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(1))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comValor(BigDecimal.valueOf(25))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                                        
                .build();        
        
        var atendimentoPagoDoConan = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(3))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(25))
                    .build()
                .comPagamentoAtendimento()
                    .pagoHoje()
                    .comValor(BigDecimal.valueOf(25))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()
                .build();
        
        var atendimentoPagoDoRambo = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(clientes.findById(2))
                .comItemAtendimento()
                    .comServico(servicos.findById(1))
                    .comPreco(BigDecimal.valueOf(150))
                    .build()
                .comPagamentoAtendimento()
                    .pagoEm(hoje.minusDays(5))
                    .comValor(BigDecimal.valueOf(150))
                    .comFormaPagamento(formaPgtos.findById(1).get())
                    .build()                    
                .build();        
        
        List.of(
            atendimentoNaoPagoDoJohn, 
            pagamentoDeOntem,
            atendimentoPagoDoConan, 
            atendimentoPagoDoRambo).forEach(atendimento -> atendimentos.salvar(atendimento));
        
        var relatorioPagamentosDTO = pagamentos.gerarRelatorioPagamentoDosDias(ontem, hoje);
        assertThat(relatorioPagamentosDTO.getPagamentos().size()).isEqualTo(3);
        assertThat(relatorioPagamentosDTO.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(75));
    }

}
