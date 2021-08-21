package com.mici.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mici.component.AtendimentoFormData;
import com.mici.repository.FormaPagamentoRepository;
import com.mici.service.ClienteService;
import com.mici.service.ServicoService;

@Controller
@RequestMapping("/atendimentos")
public class AtendimentoController {

	private ClienteService clienteService;
	private ServicoService servicoService;
	private FormaPagamentoRepository formaPagamentoRepository;
	
	public AtendimentoController(ClienteService clienteService, 
			ServicoService servicoService, 
			FormaPagamentoRepository repository) {
		this.clienteService = clienteService;
		this.servicoService = servicoService;
		this.formaPagamentoRepository = repository;
	}
	
	@GetMapping("novo/{idCliente}")
	public String form(@PathVariable("idCliente") Integer idCliente, Model model) {
		model.addAttribute("cliente", this.clienteService.findById(idCliente));
		model.addAttribute("servicos", this.servicoService.findAll());
		model.addAttribute("formasPagamento", this.formaPagamentoRepository.findAll());
		
		return "atendimentos/form_atendimentos";
	}
	
	@PostMapping("cadastrar")
	public String cadastrar(AtendimentoFormData form) {
		//Map<String, String[]> tts = r.getParameterMap();
		//String idCliente = form.getFirst("id-cliente");
		//model.addAttribute("cliente", this.clienteService.findById(Integer.parseInt(idCliente)));
		//model.addAttribute("servicos", this.servicoService.findAll());
		return "atendimentos/form_atendimentos";
		//{id-cliente=[1], servico[0][tipo-servico]=[1], servico[0][valor-aplicado]=[45], servico[1][tipo-servico]=[3], servico[1][valor-aplicado]=[10], cortesia=[sim], forma-pgto=[0], atendimento-observacao=[]}
	}
	
}
