package com.mici.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mici.service.UsuarioService;

@Controller
@RequestMapping("/password")
public class PasswordController {

	@Autowired
	UsuarioService service;
	
	@GetMapping("alterar")
	public String alterarPasswordForm() {
		return "password/alterar_password";
	}
	
	@PostMapping("alterar")
	public String alterarPassword(
			@RequestParam("senha") String password,
			@RequestParam("novaSenha") String newPassword,
			@RequestParam("novaSenhaConfirmacao") String newPasswordConfirmed,
			Authentication auth,
			Model model) {
		Boolean success = this.service.changePassword(auth.getName(), password, newPassword, newPasswordConfirmed);
		if(success) {
			model.addAttribute("mensagemSucesso", "Senha alterada com sucesso!");
		} else {
			model.addAttribute("mensagemErro", "Não foi possível alterar sua senha. Verifique os dados e tenta novamente.");
		}
		return "password/alterar_password";
	}
}
