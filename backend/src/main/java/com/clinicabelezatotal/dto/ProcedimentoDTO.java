package com.clinicabelezatotal.dto;

import com.clinicabelezatotal.model.Procedimento.CategoriaProcedimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcedimentoDTO {
    
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    @NotNull(message = "Duração é obrigatória")
    private Integer duracaoMinutos;
    
    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;
    
    @NotNull(message = "Categoria é obrigatória")
    private CategoriaProcedimento categoria;
}
