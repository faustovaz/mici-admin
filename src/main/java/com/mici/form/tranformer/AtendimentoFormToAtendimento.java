package com.mici.form.tranformer;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mici.entity.Atendimento;
import com.mici.entity.ItemAtendimento;
import com.mici.form.AtendimentoForm;
import com.mici.repository.FormaPagamentoRepository;
import com.mici.service.ClienteService;
import com.mici.service.ServicoService;

@Component
public class AtendimentoFormToAtendimento {

	private ClienteService clienteService;
	private ServicoService servicoService;
	private FormaPagamentoRepository formaPagamentoRepository;
	
	public AtendimentoFormToAtendimento(ClienteService clienteService, ServicoService servicoService,
			FormaPagamentoRepository formaPagamentoRepository) {
		this.clienteService = clienteService;
		this.servicoService = servicoService;
		this.formaPagamentoRepository = formaPagamentoRepository;
	}

	public Atendimento transform(AtendimentoForm form) {
		try {
			Atendimento atendimento = new Atendimento();
			atendimento.setCliente(this.clienteService.findById(Integer.valueOf(form.getIdCliente())));
			atendimento.setCortesia(form.isCortesia());
			atendimento.setDiaDoAtendimento(form.getDataAtendimento());
			atendimento.setPagamentoRealizado(Boolean.valueOf(form.isPagamentoRealizado()));
			
			var idFormaPgto = Integer.valueOf(form.getFormaPgto());
			if (!atendimento.isCortesia() && atendimento.isPagamentoRealizado() && idFormaPgto > 0) {
				atendimento.setFormaPagamento(formaPagamentoRepository.getById(idFormaPgto));
			}
			
			form.getServicos().forEach(s -> {
				var servico = servicoService.findById(Integer.valueOf(s.getTipoServico()));
				var itemAtendimento = new ItemAtendimento();
				itemAtendimento.setServico(servico);
				itemAtendimento.setPrecoAplicado(BigDecimal.valueOf(Double.valueOf(s.getValorAplicado())));
				atendimento.adicionarItemAtendimento(itemAtendimento);
			});
			
			if(atendimento.isPagamentoRealizado()) {
				atendimento.setValorPago(atendimento.getTotalAtendimento());
			} else {
				atendimento.setValorPago(form.getValorPago());
			}
			
			atendimento.setObservacao(form.getAtendimentoObservacao());
			return atendimento;
		}
		catch(NumberFormatException numberFormaException) {
			throw new IllegalStateException("IDs informados não são tipo númerico.");
		}
	}
	
	
	
}
