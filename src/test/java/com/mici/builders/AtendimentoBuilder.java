package com.mici.builders;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mici.entity.Atendimento;
import com.mici.entity.Cliente;
import com.mici.entity.FormaPagamento;
import com.mici.entity.ItemAtendimento;
import com.mici.entity.PagamentoAtendimento;
import com.mici.entity.Servico;

public class AtendimentoBuilder  {

	private Atendimento atendimento;
	private ItemAtendimentoBuilder itemAtendimentoBuilder;
    private PagamentoAtendimentoBuilder pagamentoAtendimentoBuilder;
	
	public AtendimentoBuilder() {
		this.atendimento = new Atendimento();
	}
	
	public AtendimentoBuilder deHoje() {
		this.atendimento.setDiaDoAtendimento(LocalDate.now());
		return this;
	}
	
	public AtendimentoBuilder doDia(String dia) {
		var data = LocalDate.parse(dia);
		this.atendimento.setDiaDoAtendimento(data);
		return this;
	}
	
	public AtendimentoBuilder seraCobrado() {
		this.atendimento.setSeraCobrado(true);
		return this;
	}
	
	public AtendimentoBuilder naoSeraCobrado() {
		this.atendimento.setSeraCobrado(false);
		return this;
	}
	
	public AtendimentoBuilder deOntem() {
		LocalDate hoje = LocalDate.now();
		LocalDate ontem = hoje.minusDays(1);
		this.atendimento.setDiaDoAtendimento(ontem);
		return this;
	}
	
	public AtendimentoBuilder comObservacao(String observacao) {
		this.atendimento.setObservacao(observacao);
		return this;
	}
	
	public AtendimentoBuilder doCliente(Cliente cliente) {
		this.atendimento.setCliente(cliente);
		return this;
	}
	
	public ItemAtendimentoBuilder comItemAtendimento() {
		this.itemAtendimentoBuilder =  new ItemAtendimentoBuilder(this);
		return this.itemAtendimentoBuilder;
	}
	
	public AtendimentoBuilder addItemAtendimento(ItemAtendimento item) {
		this.atendimento.adicionarItemAtendimento(item);
		return this;
	}
	
	public PagamentoAtendimentoBuilder comPagamentoAtendimento() {
	    this.pagamentoAtendimentoBuilder = new PagamentoAtendimentoBuilder(this);
	    return this.pagamentoAtendimentoBuilder;
	}
	
	public AtendimentoBuilder addPagamentoAtendimento(PagamentoAtendimento pagamento) {
	    this.atendimento.adicionarPagamentoAtendimento(pagamento);
	    return this;
	}

	public Atendimento build() {
		return this.atendimento;
	}
	
	public class ItemAtendimentoBuilder {
		private AtendimentoBuilder atendimentoBuilder;
		private ItemAtendimento itemAtendimento;
		
		public ItemAtendimentoBuilder(AtendimentoBuilder builder) {
			this.atendimentoBuilder = builder;
			this.itemAtendimento = new ItemAtendimento();
		}
		
		public ItemAtendimentoBuilder comServico(Servico servico) {
			this.itemAtendimento.setServico(servico);
			return this;
		}
		
		public ItemAtendimentoBuilder comPreco(BigDecimal preco) {
			this.itemAtendimento.setPrecoAplicado(preco);
			return this;
		}
		
		public AtendimentoBuilder build() {
			this.atendimentoBuilder.addItemAtendimento(itemAtendimento);
			return this.atendimentoBuilder;
		}
		
	}
	
	public class PagamentoAtendimentoBuilder {
	    private PagamentoAtendimento pagamentoAtendimento;
	    private AtendimentoBuilder atendimentoBuilder;
	    
	    public PagamentoAtendimentoBuilder(AtendimentoBuilder builder) {
	        this.atendimentoBuilder = builder;
	        this.pagamentoAtendimento = new PagamentoAtendimento();
        }
	    
	    public PagamentoAtendimentoBuilder comValor(BigDecimal valor) {
	        this.pagamentoAtendimento.setValorPagamento(valor);
	        return this;
	    }
	    
       public PagamentoAtendimentoBuilder pagoHoje() {
            this.pagamentoAtendimento.setDiaDoPagamento(LocalDate.now());
            return this;
       }
       
       public PagamentoAtendimentoBuilder pagoOntem() {
           this.pagamentoAtendimento.setDiaDoPagamento(LocalDate.now().minusDays(1));
           return this;
       }
       
       public PagamentoAtendimentoBuilder comFormaPagamento(FormaPagamento formaPagamento) {
           this.pagamentoAtendimento.setFormaPagamento(formaPagamento);
           return this;
       }
       
       public AtendimentoBuilder build() {
           this.atendimentoBuilder.addPagamentoAtendimento(pagamentoAtendimento);   
           return this.atendimentoBuilder;
       }
	    
	}
}


