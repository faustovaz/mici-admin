package com.mici.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mici.entity.Servico;
import com.mici.service.ServicoService;

@Controller
@RequestMapping("/servicos")
public class ServicosController {

	private ServicoService service;
	
	public ServicosController(ServicoService service) {
		this.service = service;
	}
	
	@GetMapping("form")
	public String formServicos(Model model) {
		model.addAttribute("servico", new Servico());
		model.addAttribute("titulo_form", "Cadastrar novo Serviço");
		model.addAttribute("texto_botao", "Cadastrar");
		return "servicos/form_servicos";
	}
	
	@GetMapping("listar")
	public String listarServicos(Model model) {
		model.addAttribute("servicos", this.service.findAll());
		return "servicos/listar_servicos";
	}

	@GetMapping("editar/{idServico}")
	public String editarServico(@PathVariable("idServico") Integer id, Model model) {
		model.addAttribute("servico", this.service.findById(id));
		model.addAttribute("titulo_form", "Alterar Serviço");
		model.addAttribute("texto_botao", "Alterar");		
		return "servicos/form_servicos";
	}
	
	@GetMapping("remover/{idServico}")
	public String remover(@PathVariable("idServico") Integer id, Model model) {
		this.service.remover(id);	
		model.addAttribute("servicos", this.service.findAll());
		model.addAttribute("mensagemSucesso", "Serviço removido!");
		return "servicos/listar_servicos";
	}
	
	@PostMapping("cadastrar")
	public String cadastrarServico(Servico servico, Model model) {
		this.service.salvar(servico);
		model.addAttribute("mensagemSucesso", "Serviço salvo com sucesso!");
		model.addAttribute("servicos", this.service.findAll());
		return "servicos/listar_servicos";
	}
	
	
}
