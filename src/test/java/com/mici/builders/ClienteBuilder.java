package com.mici.builders;

import java.time.LocalDate;

import com.mici.entity.Cliente;

public class ClienteBuilder {
	private Cliente cliente;
	
	public ClienteBuilder() {
		this.cliente = new Cliente();
	}
	
	public ClienteBuilder nome(String nome) {
		this.cliente.setNome(nome);
		return this;
	}
	
	public ClienteBuilder dataNascimento(String dataNascimento) {
		this.cliente.setDataNascimento(LocalDate.parse(dataNascimento));
		return this;
	}
	
	public ClienteBuilder dataNascimento(LocalDate data) {
	    this.cliente.setDataNascimento(data);
	    return this;
	}
	
	public ClienteBuilder anyTelefone() {
		this.cliente.setTelefone("any");
		return this;
	}
	
	public ClienteBuilder anyObservacao() {
		this.cliente.setObservacao("any");
		return this;
	}
	
	public Cliente build() {
		return this.cliente;
	}
}
