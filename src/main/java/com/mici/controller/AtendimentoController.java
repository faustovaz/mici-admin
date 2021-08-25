package com.mici.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mici.entity.Atendimento;
import com.mici.entity.Cliente;
import com.mici.entity.FormaPagamento;
import com.mici.form.AtendimentoForm;
import com.mici.form.tranformer.AtendimentoFormToAtendimento;
import com.mici.repository.FormaPagamentoRepository;
import com.mici.service.AtendimentoService;
import com.mici.service.ClienteService;
import com.mici.service.ServicoService;

@Controller
@RequestMapping("/atendimentos")
public class AtendimentoController {

	private ClienteService clienteService;
	private ServicoService servicoService;
	private FormaPagamentoRepository formaPagamentoRepository;
	private AtendimentoService service;
	private AtendimentoFormToAtendimento atendimentoTranformer;
	
	public AtendimentoController(ClienteService clienteService, 
			ServicoService servicoService, 
			FormaPagamentoRepository repository,
			AtendimentoService service,
			AtendimentoFormToAtendimento transformer) {
		this.clienteService = clienteService;
		this.servicoService = servicoService;
		this.formaPagamentoRepository = repository;
		this.service = service;
		this.atendimentoTranformer = transformer;
	}
	
	
	@GetMapping("novo/{idCliente}")
	public String form(@PathVariable("idCliente") Integer idCliente, Model model) {
		model.addAttribute("cliente", this.clienteService.findById(idCliente));
		model.addAttribute("servicos", this.servicoService.findAll());
		model.addAttribute("formasPagamento", this.formaPagamentoRepository.findAll());
		model.addAttribute("dataAtual", LocalDate.now());
		return "atendimentos/form_atendimentos";
	}
	
	
	@GetMapping("listar/{idCliente}") 
	public String listarPorCliente(@PathVariable("idCliente") Integer idCliente, Model model){
		Cliente cliente = this.clienteService.findById(idCliente);
		List<Atendimento> atendimentos = this.service.findAllByCliente(cliente);
		model.addAttribute("cliente", cliente);
		model.addAttribute("atendimentos", atendimentos);
		return "atendimentos/listar_atendimentos";
	}
	
	
	@PostMapping("cadastrar")
	public String cadastrar(AtendimentoForm form) {
		try {
			Atendimento atendimento = this.atendimentoTranformer.transform(form);
			this.service.salvar(atendimento);
			return "redirect:/atendimentos/listar/" + form.getIdCliente();
		}
		catch (Exception e){
			return "404";
		}
	}
	
	
	@GetMapping("editar/{idAtendimento}")
	public String editar(@PathVariable("idAtendimento") Integer idAtendimento, Model model){
		Optional<Atendimento> atendimentoOp = this.service.findById(idAtendimento);
		model.addAttribute("atendimento", atendimentoOp.get());
		model.addAttribute("formasPagamento", this.formaPagamentoRepository.findAll());
		return "atendimentos/editar_atendimentos";
	}
	
	
	@PostMapping("atualizar")
	public String atualizar(@RequestParam("cortesia") boolean cortesia,
			@RequestParam(required = false, name = "pagamentoRealizado") Boolean pagamentoRealizado,
			@RequestParam("formaPgto") Integer formaPagamento,
			@RequestParam(required = false, name = "valorPago") BigDecimal valorPago,
			@RequestParam("atendimentoObservacao") String observacao,
			@RequestParam("idAtendimento") Integer idAtendimento) {
		try {
			Optional<Atendimento> atendimentoOp = this.service.findById(idAtendimento);
			if(atendimentoOp.isEmpty())
				return "404";
			
			Atendimento atendimento = atendimentoOp.get();
			atendimento.setCortesia(cortesia);
			atendimento.setPagamentoRealizado(!Objects.isNull(pagamentoRealizado) && pagamentoRealizado ? true : false);
			
			if (formaPagamento > 0) {
				Optional<FormaPagamento> formaPgtoOp = this.formaPagamentoRepository.findById(formaPagamento);
				atendimento.setFormaPagamento(formaPgtoOp.isPresent() ? formaPgtoOp.get() : null);
			}
			
			if (!atendimento.isPagamentoRealizado()) {
				atendimento.setValorPago(valorPago);
			} else {
				atendimento.setValorPago(atendimento.getTotalAtendimento());
			}
			
			atendimento.setObservacao(observacao);
			this.service.salvar(atendimento);
			return "redirect:/atendimentos/listar/" + atendimento.getCliente().getId();
		}
		catch (Exception e) {
			return "404";
		}
	}
	
	
	@GetMapping("remover/{idAtendimento}")
	public String remover(@PathVariable("idAtendimento") Integer idAtendimento) {
		try {
			Optional<Atendimento> atendimentoOp = this.service.findById(idAtendimento);
			if(atendimentoOp.isEmpty())
				return "404";
			var atendimento = atendimentoOp.get();
			this.service.remove(atendimento);
			return "redirect:/atendimentos/listar/" + atendimento.getCliente().getId();
		}
		catch(Exception e) {
			return "404";
		}
	}
	
	
	@GetMapping("listar/hoje")
	public String listarAtendimentosDeHoje(Model model) {
		List<Atendimento> atendimentos = this.service.findAllAtendimentosDeHoje();
		model.addAttribute("atendimentos", atendimentos);		
		return "atendimentos/listar_atendimentos";
	}
	
	
	@GetMapping("listar/naopagos")
	public String listarAtendimentosNaoPagos(Model model) {
		List<Atendimento> atendimentos = this.service.findAllNaoPagos();
		model.addAttribute("atendimentos", atendimentos);		
		return "atendimentos/listar_atendimentos";
	}
}