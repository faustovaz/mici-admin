package com.mici.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String cadastrar(@RequestBody MultiValueMap<String, String> form, Model model) {
		String idCliente = form.getFirst("id-cliente");
		model.addAttribute("cliente", this.clienteService.findById(Integer.parseInt(idCliente)));
		model.addAttribute("servicos", this.servicoService.findAll());
		return "atendimentos/form_atendimentos";
	}
	
}
