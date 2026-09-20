package com.clinicabelezatotal.repository;

import com.clinicabelezatotal.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByClienteIdAndDataBetween(Long clienteId, LocalDate inicio, LocalDate fim);
    List<Agendamento> findByFuncionarioIdAndDataAndStatusNot(Long funcionarioId, LocalDate data, Agendamento.StatusAgendamento status);
    List<Agendamento> findByFuncionarioIdAndData(Long funcionarioId, LocalDate data);
    List<Agendamento> findByClienteId(Long clienteId);
    
    @Query("SELECT a FROM Agendamento a WHERE a.funcionario.id = :funcionarioId AND a.data = :data AND a.status NOT IN ('CANCELADO', 'NAO_COMPARECEU') AND ((a.horaInicio < :horaFim) AND (a.horaFim > :horaInicio))")
    List<Agendamento> findConflitosHorario(Long funcionarioId, LocalDate data, LocalTime horaInicio, LocalTime horaFim);
    
    @Query("SELECT a FROM Agendamento a WHERE a.data = :data AND a.status NOT IN ('CANCELADO')")
    List<Agendamento> findByData(LocalDate data);
    
    @Query("SELECT a FROM Agendamento a WHERE a.data BETWEEN :inicio AND :fim AND a.status NOT IN ('CANCELADO')")
    List<Agendamento> findByPeriodo(LocalDate inicio, LocalDate fim);
    
    @Query("SELECT a FROM Agendamento a WHERE a.lembreteEnviado = false AND a.data = :data AND a.status IN ('AGENDADO', 'CONFIRMADO')")
    List<Agendamento> findParaEnviarLembrete(LocalDate data);
}
