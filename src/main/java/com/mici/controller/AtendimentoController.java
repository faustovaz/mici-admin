package com.mici.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mici.entity.Atendimento;
import com.mici.entity.Cliente;
import com.mici.entity.PagamentoAtendimento;
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
	public String cadastrar(AtendimentoForm form, Authentication auth) {
		try {
			Atendimento atendimento = this.atendimentoTranformer.transform(form);
			atendimento.setCriadoPor(auth.getName());
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
		
		if(atendimentoOp.isEmpty())
		    return "404";
		
		model.addAttribute("atendimento", atendimentoOp.get());
	    model.addAttribute("dataAtual", LocalDate.now());
		model.addAttribute("formasPagamento", this.formaPagamentoRepository.findAll());
		return "atendimentos/editar_atendimentos";
	}
	
	
	@PostMapping("atualizar")
	public String atualizar(
	        @RequestParam(required = false, name ="valorPago") BigDecimal valorPago,
	        @RequestParam(required = false, name = "idFormaPgto") Integer idFormaPgto,
	        @RequestParam(required = false, name = "dataPgto") String dataPagamento,
			@RequestParam("atendimentoObservacao") String observacao,
			@RequestParam("idAtendimento") Integer idAtendimento,
			Authentication auth) {
		try {
			Optional<Atendimento> atendimentoOp = this.service.findById(idAtendimento);
			
			if(atendimentoOp.isEmpty())
				return "404";
			
			Atendimento atendimento = atendimentoOp.get();
			if(atendimento.isSeraCobrado() && !atendimento.isPagamentoRealizado() && valorPago.compareTo(new BigDecimal(0)) > 0) {
			    var novoPagamento = new PagamentoAtendimento();
			    novoPagamento.setValorPagamento(valorPago);
			    novoPagamento.setDiaDoPagamento(LocalDate.parse(dataPagamento));
			    if (idFormaPgto > 0) {
			        novoPagamento.setFormaPagamento(formaPagamentoRepository.getById(idFormaPgto));
			    }
			    atendimento.adicionarPagamentoAtendimento(novoPagamento);
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
	
	
	@GetMapping("consultar")
	public String formConsultar(Model model) {
		model.addAttribute("dataInicial", LocalDate.now());
		model.addAttribute("dataFinal", LocalDate.now());
		return "atendimentos/consultar_atendimentos";
	}
	
	
	@PostMapping("consultar")
	public String consultar(
			@RequestParam(name = "dataInicial") String strDataInicial,
			@RequestParam(name = "dataFinal") String strDataFinal,
			Model model) {
		var dataInicial = LocalDate.parse(strDataInicial);
		var dataFinal = LocalDate.parse(strDataFinal);
		var atendimentos = this.service.findByDiaDoAtendimentoBetween(dataInicial, dataFinal);
		model.addAttribute("dataInicial", dataInicial);
		model.addAttribute("dataFinal", dataFinal);
		model.addAttribute("atendimentos", atendimentos);
		return "atendimentos/consultar_atendimentos";
	}
	
	@ExceptionHandler({BindException.class})
	public String handler(HttpServletRequest r, BindException e, Model model) {
	    String uri = r.getRequestURI();
	    if ("/atendimentos/cadastrar".equals(uri)) {
	        var idCliente = r.getParameter("idCliente");
	        model.addAttribute("cliente", this.clienteService.findById(Integer.valueOf(idCliente)));
	        model.addAttribute("servicos", this.servicoService.findAll());
	        model.addAttribute("formasPagamento", this.formaPagamentoRepository.findAll());
	        model.addAttribute("dataAtual", LocalDate.now());
	        model.addAttribute("mensagemErro", 
	                    "Por favor, preencha todos os corretamente. Atenção ao selecionar os serviços prestados.");
	        return "atendimentos/form_atendimentos";
	    }
	    
	    if("/atendimentos/atualizar".equals(uri)) {
	        var idAtendimento = r.getParameter("idAtendimento");
	        Optional<Atendimento> atendimentoOp = this.service.findById(Integer.valueOf(idAtendimento));
	        model.addAttribute("atendimento", atendimentoOp.get());
	        model.addAttribute("dataAtual", LocalDate.now());
	        model.addAttribute("formasPagamento", this.formaPagamentoRepository.findAll());
            model.addAttribute("mensagemErro", "Por favor, preencha todos os corretamente.");	        
	        return "atendimentos/editar_atendimentos";
	    }
	    
	    return "500";
	}
}