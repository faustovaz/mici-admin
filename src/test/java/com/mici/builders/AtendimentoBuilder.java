package com.mici.builders;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mici.entity.Atendimento;
import com.mici.entity.Cliente;
import com.mici.entity.FormaPagamento;
import com.mici.entity.ItemAtendimento;
import com.mici.entity.Servico;

public class AtendimentoBuilder  {

	private Atendimento atendimento;
	private ItemAtendimentoBuilder itemAtendimentoBuilder;
	
	public AtendimentoBuilder() {
		this.atendimento = new Atendimento();
	}
	
	public AtendimentoBuilder pago() {
		this.atendimento.setPagamentoRealizado(true);
		return this;
	}
	
	public AtendimentoBuilder naoPago() {
		this.atendimento.setPagamentoRealizado(false);
		return this;
	}
	
	public AtendimentoBuilder hoje() {
		this.atendimento.setDiaDoAtendimento(LocalDate.now());
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
	
	public AtendimentoBuilder comFormaDePagamento(FormaPagamento formaPgto) {
		this.atendimento.setFormaPagamento(formaPgto);
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
}


