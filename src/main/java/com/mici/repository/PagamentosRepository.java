package com.mici.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mici.entity.dto.PagamentoDTO;

@Repository
public class PagamentosRepository {

    private JdbcTemplate jdbcTemplate;
    
    public PagamentosRepository(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }
    
    public List<PagamentoDTO> findPagamentosEntreOsDias(LocalDate inicio, LocalDate fim) {
        var sql = """
            select 
                clientes.nome as "nomeCliente" , 
                pagamentos_atendimento.dia_do_pagamento as "dataPagamento",
                formas_pagamento.nome as "formaPagamento",
                pagamentos_atendimento.valor_pagamento as "valorPagamento",
                pagamentos_atendimento.id_atendimento as "idAtendimento"
            from    
                pagamentos_atendimento join atendimentos on (pagamentos_atendimento.id_atendimento = atendimentos.id)
                join clientes on (atendimentos.id_cliente = clientes.id) 
                join formas_pagamento on (pagamentos_atendimento.id_forma_pagamento = formas_pagamento.id)
            where 
                pagamentos_atendimento.dia_do_pagamento BETWEEN ? and ? 
            order by 
                pagamentos_atendimento.dia_do_pagamento, pagamentos_atendimento.id desc""";
        
        RowMapper<PagamentoDTO> mapper = (row, num) -> {
            PagamentoDTO dto = new PagamentoDTO();
            dto.setNomeCliente(row.getString("nomeCliente"));
            dto.setDataPagamento(LocalDate.parse(row.getString("dataPagamento")));
            dto.setFormaPagamento(row.getString("formaPagamento"));
            dto.setValorPagamento(new BigDecimal(row.getString("valorPagamento")));
            dto.setIdAtendimento(Integer.valueOf(row.getString("idAtendimento")));
            return dto;
        };
        
        return jdbcTemplate.query(sql, mapper, inicio, fim);
    }
    
}
