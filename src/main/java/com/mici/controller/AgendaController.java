package com.mici.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mici.entity.Agendamento;
import com.mici.service.AgendamentoService;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

	private AgendamentoService service;
	
	public AgendaController(AgendamentoService service) {
		this.service = service;
	}
	
	@GetMapping("/")
	public String index() {
		return "agenda";
	}
	
	@ResponseBody
	@PostMapping(value = "/agendar", produces = "applicatio/json")
	public Agendamento agendar(@RequestBody Agendamento agendamento) {
		return this.service.agendar(agendamento);
	}
}
