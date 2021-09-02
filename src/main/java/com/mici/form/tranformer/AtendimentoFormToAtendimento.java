package com.mici.form.tranformer;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mici.entity.Atendimento;
import com.mici.entity.ItemAtendimento;
import com.mici.entity.PagamentoAtendimento;
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
			
			form.getServicos().forEach(servico -> {
			    var s = servicoService.findById(servico.getTipoServico());
			    BigDecimal valor = servico.getValorAplicado();
			    ItemAtendimento itemAtendimento = new ItemAtendimento();
			    itemAtendimento.setPrecoAplicado(valor);
			    itemAtendimento.setServico(s);
			    atendimento.adicionarItemAtendimento(itemAtendimento);
			});
			
			atendimento.setDiaDoAtendimento(form.getDataAtendimento());
			atendimento.setCronograma(form.isCronograma());
			atendimento.setSeraCobrado(form.isSeraCobrado());
			
			if (atendimento.isSeraCobrado() && form.getValorPago().compareTo(new BigDecimal(0)) > 0) {
			    var pagamento = new PagamentoAtendimento();
			    pagamento.setValorPagamento(form.getValorPago());
			    pagamento.setDiaDoPagamento(form.getDataPgto());
			    
			    if (form.getIdFormaPgto() > 0) {
			        pagamento.setFormaPagamento(formaPagamentoRepository.getById(form.getIdFormaPgto()));
			    }
			    atendimento.adicionarPagamentoAtendimento(pagamento);
			}
			
			atendimento.setObservacao(form.getAtendimentoObservacao());
			
			
			return atendimento;
		}
		catch(NumberFormatException numberFormaException) {
			throw new IllegalStateException("IDs informados não são tipo númerico.");
		}
	}
	
	
	
}
