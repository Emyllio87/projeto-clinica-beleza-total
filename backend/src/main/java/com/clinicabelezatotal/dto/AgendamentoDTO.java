package com.clinicabelezatotal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AgendamentoDTO {
    
    private Long id;
    
    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;
    
    @NotNull(message = "Funcionário é obrigatório")
    private Long funcionarioId;
    
    @NotNull(message = "Procedimento é obrigatório")
    private Long procedimentoId;
    
    @NotNull(message = "Data é obrigatória")
    private LocalDate data;
    
    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;
    
    private String observacoes;
}
