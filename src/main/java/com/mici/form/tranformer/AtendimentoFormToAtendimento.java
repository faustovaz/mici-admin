package com.mici.form.tranformer;

import org.springframework.stereotype.Component;

import com.mici.entity.Atendimento;
import com.mici.form.AtendimentoForm;
import com.mici.repository.FormaPagamentoRepository;
import com.mici.service.ClienteService;
import com.mici.service.ServicoService;

@Component
public class AtendimentoFormToAtendimento {

	private ClienteService clienteService;
	//private ServicoService servicoService;
	//private FormaPagamentoRepository formaPagamentoRepository;
	
	public AtendimentoFormToAtendimento(ClienteService clienteService, ServicoService servicoService,
			FormaPagamentoRepository formaPagamentoRepository) {
		this.clienteService = clienteService;
		//this.servicoService = servicoService;
		//this.formaPagamentoRepository = formaPagamentoRepository;
	}

	public Atendimento transform(AtendimentoForm form) {
		try {
			Atendimento atendimento = new Atendimento();
			atendimento.setCliente(this.clienteService.findById(Integer.valueOf(form.getIdCliente())));
			atendimento.setSeraCobrado(form.isSeraCobrado());
			atendimento.setDiaDoAtendimento(form.getDataAtendimento());
			atendimento.setObservacao(form.getAtendimentoObservacao());
			return atendimento;
		}
		catch(NumberFormatException numberFormaException) {
			throw new IllegalStateException("IDs informados não são tipo númerico.");
		}
	}
	
	
	
}
