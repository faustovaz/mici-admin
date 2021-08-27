package com.mici.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	@GetMapping("js/all")
	public List<Agendamento> getAll() {
		return this.service.findAll();
	}
	
	@ResponseBody
	@GetMapping("js/{date}")
	public ResponseEntity<List<Agendamento>> getTodosDoDia(@PathVariable("date") String strDate) {
		try {
			LocalDate date = LocalDate.parse(strDate);
			return new ResponseEntity<>(this.service.getTodosDoDia(date), HttpStatus.OK);
		} 
		catch(DateTimeParseException d) {
			return ResponseEntity.badRequest().header("error", "Data Inválida").build();
		}
		
	}
	
	@ResponseBody
	@PostMapping
	public ResponseEntity<Agendamento> agendar(@Valid @RequestBody Agendamento agenda, BindingResult result, Authentication auth) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		agenda.setCriadoPor(auth.getName());
		return new ResponseEntity<>(this.service.agendar(agenda), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Integer id){
		try {
			this.service.deleteById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch(IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
