package com.mici.entity.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mici.entity.Atendimento;
import com.mici.entity.Cliente;

public class RelatorioClientesEmDebitoDTO {
    
    List<ClienteEmDebitoDTO> clientesEmDebito;
    
    public RelatorioClientesEmDebitoDTO(List<Atendimento> atendimentosNaoPagos) {
        this.clientesEmDebito = new ArrayList<>();
        this.processar(atendimentosNaoPagos);
    }
    
    public List<ClienteEmDebitoDTO> getClientesEmDebito() {
        return this.clientesEmDebito;
    }
    
    public BigDecimal getTotalDebitos() {
        return this.clientesEmDebito
                .stream()
                .map(cliente -> cliente.getTotalDebito())
                .reduce(new BigDecimal(0), (total, valor) -> total.add(valor));
    }
    
    private void processar(List<Atendimento> atendimentosNaoPagos) {
        var clientes = this.getUniqueClientes(atendimentosNaoPagos);
        clientes.forEach(cliente -> {
            var valorJaPago = this.getTotalJaPagoPeloCliente(atendimentosNaoPagos, cliente);
            var valorTotalAtendimentos = this.getValorTotalAtendimentosDoCliente(atendimentosNaoPagos, cliente);
            var totalDevedor = valorTotalAtendimentos.subtract(valorJaPago);
            this.clientesEmDebito.add(new ClienteEmDebitoDTO(cliente.getNome(), cliente.getId(), totalDevedor));
        });
    }
    
    private Set<Cliente> getUniqueClientes(List<Atendimento> atendimentosNaoPagos) {
        return atendimentosNaoPagos
                .stream()
                .map(atendimento -> atendimento.getCliente())
                .collect(Collectors.toSet());
    }
    
    private BigDecimal getTotalJaPagoPeloCliente(List<Atendimento> atendimentosNaoPagos, Cliente cliente) {
        return atendimentosNaoPagos
            .stream()
            .filter(atendimento -> atendimento.getCliente().equals(cliente))
            .map(atendimentoNaoPago -> atendimentoNaoPago.getValorPago())
            .reduce(new BigDecimal(0), (total, pgto) -> total.add(pgto));
    }
    
    private BigDecimal getValorTotalAtendimentosDoCliente(List<Atendimento> atendimentosNaoPagos, Cliente cliente) {
       return atendimentosNaoPagos
                .stream()
                .filter(atendimento -> atendimento.getCliente().equals(cliente))
                .map(atendimentoNaoPago -> atendimentoNaoPago.getValorAtendimento())
                .reduce(new BigDecimal(0), (total, pgto) -> total.add(pgto));
    }
    
}
