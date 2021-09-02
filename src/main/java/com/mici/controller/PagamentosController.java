package com.mici.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mici.entity.dto.RelatorioPagamentosDTO;
import com.mici.service.PagamentosService;

@Controller
@RequestMapping("pagamentos")
public class PagamentosController {

    @Autowired
    private PagamentosService service;
    
    @GetMapping("hoje")
    public String listarPagamentosDeHoje(Model model) {
        RelatorioPagamentosDTO relatorio = service.gerarRelatorioPagamentosDeHoje();
        model.addAttribute("messageNaoEncontrado", "Nenhum pagamento registrado hoje!");
        model.addAttribute("relatorio", relatorio);
        return "pagamentos/listar_pagamentos.html";
    }
    
    
    @GetMapping("consultar")
    public String formConsultarPagamentos(Model model) {
        RelatorioPagamentosDTO relatorio = service.gerarRelatorioPagamentosDeHoje();
        model.addAttribute("dataInicial", LocalDate.now());
        model.addAttribute("dataFinal", LocalDate.now());
        model.addAttribute("messageNaoEncontrado", "Nenhum pagamento registrado para entre os dias informados!");
        model.addAttribute("relatorio", relatorio);
        return "pagamentos/consultar_pagamentos.html";
    }
    
    @PostMapping("consultar")
    public String consultarPagamentos(
            @RequestParam(name = "dataInicial") String strDataInicial,
            @RequestParam(name = "dataFinal") String strDataFinal,
            Model model) {
        var dataInicial = LocalDate.parse(strDataInicial);
        var dataFinal = LocalDate.parse(strDataFinal);
        var relatorio = service.gerarRelatorioPagamentoDosDias(dataInicial, dataFinal);
        model.addAttribute("dataInicial", dataInicial);
        model.addAttribute("dataFinal", dataFinal);
        model.addAttribute("messageNaoEncontrado", "Nenhum pagamento registrado para entre os dias informados!");
        model.addAttribute("relatorio", relatorio);
        return "pagamentos/consultar_pagamentos.html";
    }
    
    
}
