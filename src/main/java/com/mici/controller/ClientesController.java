package com.mici.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mici.entity.Cliente;
import com.mici.service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

	private ClienteService service;
	
	public ClientesController(ClienteService service) {
		this.service = service;
	}
	
	@GetMapping("form")
	public String formServicos(Model model) {
		model.addAttribute("cliente", new Cliente());
		model.addAttribute("titulo_form", "Cadastrar novo Cliente");
		model.addAttribute("texto_botao", "Cadastrar");
		return "clientes/form_clientes";
	}
	
	@PostMapping("cadastrar")
	public String cadastrarServico(Cliente cliente, Model model) {
		this.service.save(cliente);
		model.addAttribute("mensagemSucesso", "Cliente salvo com sucesso!");
		model.addAttribute("clientes", this.service.findAll());
		return "clientes/listar_clientes";
	}
	
	@GetMapping("editar/{idCliente}")
	public String editarServico(@PathVariable("idCliente") Integer id, Model model) {
		model.addAttribute("cliente", this.service.findById(id));
		model.addAttribute("titulo_form", "Alterar");
		model.addAttribute("texto_botao", "Alterar");		
		return "clientes/form_clientes";
	}
	
	@GetMapping("listar")
	public String listarClientes(Model model) { 
		List<Cliente> all = this.service.findAll();
		
		if (all.size() > 10) {
			all = all.subList(0, 10);
		}
			
		model.addAttribute("clientes", all);
		model.addAttribute("termo_busca", "");
		return "clientes/listar_clientes";
	}
	
	@GetMapping("buscar")
	public String buscarCliente(@RequestParam("termo") String busca, Model model) {
		List<Cliente> clientes = this.service.findByName(busca);
		model.addAttribute("clientes", clientes);
		model.addAttribute("termo_busca", busca);
		return "clientes/listar_clientes";
	}
}
