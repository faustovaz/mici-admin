package com.mici.entity.dto;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioPagamentosDTO {

    private List<PagamentoDTO> pagamentos;
    private BigDecimal valorTotal;
    
    public RelatorioPagamentosDTO(List<PagamentoDTO> pagamentos) {
        this.pagamentos = pagamentos;
        this.valorTotal = new BigDecimal(0);
        this.calculaTotalPagamentos();
    }
    
    private void calculaTotalPagamentos() {
        this.valorTotal = this.pagamentos
                            .stream()
                            .map(pagamento -> pagamento.getValorPagamento())
                            .reduce(new BigDecimal(0), (valorTotal, valorPagamento) -> valorTotal.add(valorPagamento));
    }
    
    public BigDecimal getValorTotal() {
        return this.valorTotal;
    }
    
    public List<PagamentoDTO> getPagamentos() {
        return this.pagamentos;
    }
    
}
