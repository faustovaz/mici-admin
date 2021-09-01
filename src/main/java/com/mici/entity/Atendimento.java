package com.mici.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.mici.dialect.SQLiteLocalDateConverter;
import com.mici.dialect.SQLiteLocalDateTimeConverter;

import lombok.Data;

@Data
@Entity
@Table(name = "atendimentos")
public class Atendimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
		
	@Convert(converter = SQLiteLocalDateConverter.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "dia_do_atendimento")
	private LocalDate diaDoAtendimento;
	
	private String observacao;
	
	@Column(name = "valor_atendimento")
	private BigDecimal valorAtendimento = new BigDecimal(0);
	
	@OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<ItemAtendimento> itensAtendimento;
	
	@OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<PagamentoAtendimento> pagamentos;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@Column(name = "sera_cobrado")
	private boolean seraCobrado;
	
	@Column(name = "is_cronograma")
	private boolean cronograma;
	
	@Column(name = "pagamento_realizado")
	private boolean pagamentoRealizado;

	@Convert(converter = SQLiteLocalDateTimeConverter.class)
	@Column(name = "ultima_atualizacao")
	private LocalDateTime ultimaAtualizacao;
	
	@Column(name = "criado_por")
	private String criadoPor;
	
	public void adicionarItemAtendimento(ItemAtendimento itemAtendimento){
		if (Objects.isNull(itensAtendimento)) {
			this.itensAtendimento = new HashSet<>();
		}
		itemAtendimento.setAtendimento(this);
		valorAtendimento = valorAtendimento.add(itemAtendimento.getPrecoAplicado());
		this.itensAtendimento.add(itemAtendimento);
	}
	
	public void adicionarItensAtendimento(List<ItemAtendimento> itens){
		itens.forEach(item -> this.adicionarItemAtendimento(item));
	}
	
	public Date getDiaDoAtendimentoAsDate() {
		return java.sql.Date.valueOf(getDiaDoAtendimento());
	}
	
	public BigDecimal getTotalAtendimento() {
		BigDecimal pretoAtendimento = this.itensAtendimento
			.stream()
			.map(item -> item.getPrecoAplicado())
			.reduce(new BigDecimal(0), (total, preco) -> total.add(preco));
		return pretoAtendimento;
	}

    public void adicionarPagamentoAtendimento(PagamentoAtendimento pagamento) {
        if (Objects.isNull(pagamentos)) {
            this.pagamentos = new HashSet<>();
        }
        pagamento.setAtendimento(this);
        this.pagamentos.add(pagamento);
        this.updatePagamentoRealizado();
    }
    
    private void updatePagamentoRealizado() {
        this.setPagamentoRealizado(this.isPago());
    }
    
    public void adicionarPagamentosAtendimento(List<PagamentoAtendimento> pagamentos) {
        pagamentos.forEach(pagamento -> this.adicionarPagamentoAtendimento(pagamento));
    }
    
    public boolean isPago() {
        return this.valorAtendimento.equals(this.getValorPago());
    }
    
    public BigDecimal getValorPago() {
        return this.pagamentos
                .stream()
                .map(pagamento -> pagamento.getValorPagamento())
                .reduce(new BigDecimal(0), (total, value) -> total.add(value));
    }

}
