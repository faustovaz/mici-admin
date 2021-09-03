package com.mici.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.github.javafaker.Faker;
import com.mici.builders.AtendimentoBuilder;
import com.mici.builders.ClienteBuilder;
import com.mici.entity.Cliente;
import com.mici.entity.dto.RelatorioClientesEmDebitoDTO;
import com.mici.repository.ClienteRepository;
import com.mici.repository.FormaPagamentoRepository;

@ActiveProfiles("tests")
@SpringBootTest
class ClienteServiceTest {

    @Autowired
    ClienteService service;
    
    @Autowired
    ClienteRepository repo;
    
    @Autowired
    ServicoService servicoService;
    
    @Autowired
    FormaPagamentoRepository pgtoRepo;
    
    @Autowired
    AtendimentoService atendimentoService;
    
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
    }
    
    @Test
    void deveListarTodosAniversariantesDoMesDeNovembro() {
        List.of(
            new ClienteBuilder().nome(faker.name().name()).dataNascimento("2000-11-02").build(),
            new ClienteBuilder().nome(faker.name().name()).dataNascimento("1983-11-27").build(),
            new ClienteBuilder().nome(faker.name().name()).dataNascimento("1999-11-15").build(),
            new ClienteBuilder().nome(faker.name().name()).dataNascimento("2002-07-15").build(),
            new ClienteBuilder().nome(faker.name().name()).dataNascimento("1963-09-30").build(),
            new ClienteBuilder().nome(faker.name().name()).dataNascimento("2006-10-10").build()
        ).forEach(cliente -> service.save(cliente));
        
        var aniversariantes = repo.getAniversariantesDoMes(11);
        assertThat(aniversariantes.size()).isEqualTo(3);
    }
    
    @Test
    void deveListarTodosAniversariantesDaSemana() {
        var inicio = LocalDate.now();
        List.of(
                new ClienteBuilder().nome(faker.name().name()).dataNascimento(inicio.plusDays(5)).build(),
                new ClienteBuilder().nome(faker.name().name()).dataNascimento(inicio).build(),
                new ClienteBuilder().nome(faker.name().name()).dataNascimento(inicio.plusDays(2)).build(),
                new ClienteBuilder().nome(faker.name().name()).dataNascimento(inicio.plusDays(7)).build(),
                new ClienteBuilder().nome(faker.name().name()).dataNascimento(inicio.plusDays(8)).build(),
                new ClienteBuilder().nome(faker.name().name()).dataNascimento(inicio.minusDays(1)).build(),
                new ClienteBuilder().nome(faker.name().name()).dataNascimento(inicio.minusDays(4)).build()
            ).forEach(cliente -> service.save(cliente));
        
        List<Cliente> aniversariantes = service.getAniversariantesDaSemana(inicio);
        assertThat(aniversariantes.size()).isEqualTo(4);
    }
    
    @Test
    void deveRetornarASomaDosDebitosDeUmCliente() {
        db.execute("insert into clientes(nome, data_nascimento) values('John', '1983-11-02')");
        
        var atendimentoNaoPago = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(service.findById(1))
                .comItemAtendimento()
                    .comServico(servicoService.findById(1))
                    .comPreco(BigDecimal.valueOf(150))
                    .build()
                .build();
        
        var atendimentoPago = new AtendimentoBuilder()
                .seraCobrado()
                .deHoje()
                .doCliente(service.findById(1))
                .comItemAtendimento()
                    .comServico(servicoService.findById(1))
                    .comPreco(new BigDecimal(70))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comFormaPagamento(pgtoRepo.findById(1).get())
                    .comValor(BigDecimal.valueOf(70))
                    .build()
                .build();
        
        var atendimentoPagoParcialmente = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(service.findById(1))
                .comItemAtendimento()
                    .comServico(servicoService.findById(1))
                    .comPreco(new BigDecimal(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoHoje()
                    .comFormaPagamento(pgtoRepo.findById(1).get())
                    .comValor(BigDecimal.valueOf(40))
                    .build()
                .build();
                
        List.of(
            atendimentoNaoPago, 
            atendimentoPago, 
            atendimentoPagoParcialmente).forEach(atendimento -> atendimentoService.salvar(atendimento));
        
        RelatorioClientesEmDebitoDTO clientesEmDebito = service.getAllClientesEmDebito();
        assertThat(clientesEmDebito.getTotalDebitos()).isEqualByComparingTo(BigDecimal.valueOf(160));
        assertThat(clientesEmDebito.getClientesEmDebito().size()).isEqualTo(1);
        assertThat(clientesEmDebito.getClientesEmDebito().get(0).getNome()).isEqualTo("John");
        assertThat(clientesEmDebito.getClientesEmDebito().get(0).getTotalDebito()).isEqualByComparingTo(BigDecimal.valueOf(160));
    }
    
    
    @Test
    void deveRetornarASomaDosDebitosDosClientes() {
        db.execute("insert into clientes(nome, data_nascimento) values('John', '1983-11-02')");
        db.execute("insert into clientes(nome, data_nascimento) values('Rambo', '1983-11-02')");
        db.execute("insert into clientes(nome, data_nascimento) values('Conan', '1983-11-02')");

        var atendimentoNaoPagoDoJohn = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(service.findById(1))
                .comItemAtendimento()
                    .comServico(servicoService.findById(1))
                    .comPreco(BigDecimal.valueOf(50))
                    .build()
                .build();
        
        var atendimentoPagoDoRambo = new AtendimentoBuilder()
                .seraCobrado()
                .deHoje()
                .doCliente(service.findById(2))
                .comItemAtendimento()
                    .comServico(servicoService.findById(1))
                    .comPreco(new BigDecimal(70))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comFormaPagamento(pgtoRepo.findById(1).get())
                    .comValor(BigDecimal.valueOf(70))
                    .build()
                .build();
        
        var atendimentoPagoParcialmenteDoConan = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(service.findById(3))
                .comItemAtendimento()
                    .comServico(servicoService.findById(1))
                    .comPreco(new BigDecimal(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoHoje()
                    .comFormaPagamento(pgtoRepo.findById(1).get())
                    .comValor(BigDecimal.valueOf(40))
                    .build()
                .build();
        
        var atendimentoPagoParcialmenteDoJohn = new AtendimentoBuilder()
                .seraCobrado()
                .deOntem()
                .doCliente(service.findById(1))
                .comItemAtendimento()
                    .comServico(servicoService.findById(1))
                    .comPreco(BigDecimal.valueOf(50))
                    .build()
                .comPagamentoAtendimento()
                    .pagoOntem()
                    .comFormaPagamento(pgtoRepo.findById(1).get())
                    .comValor(BigDecimal.valueOf(30))
                    .build()                    
                .build();
        
                
        List.of(
            atendimentoNaoPagoDoJohn, 
            atendimentoPagoDoRambo, 
            atendimentoPagoParcialmenteDoConan,
            atendimentoPagoParcialmenteDoJohn).forEach(atendimento -> atendimentoService.salvar(atendimento));
        
        RelatorioClientesEmDebitoDTO clientesEmDebito = service.getAllClientesEmDebito();
        assertThat(clientesEmDebito.getTotalDebitos()).isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(clientesEmDebito.getClientesEmDebito().size()).isEqualTo(2); //2 clientes devedores

        var debitosDoJohn = clientesEmDebito
            .getClientesEmDebito()
            .stream()
            .filter(c -> c.getNome().equals("John"))
            .collect(Collectors.toList());
        
        var debitosDoConan = clientesEmDebito
                .getClientesEmDebito()
                .stream()
                .filter(c -> c.getNome().equals("Conan"))
                .collect(Collectors.toList());        
        
        assertThat(debitosDoJohn.get(0).getTotalDebito()).isEqualByComparingTo(BigDecimal.valueOf(70));
        assertThat(debitosDoConan.get(0).getTotalDebito()).isEqualByComparingTo(BigDecimal.valueOf(10));
    }    

}
