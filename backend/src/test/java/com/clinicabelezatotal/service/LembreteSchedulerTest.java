package com.clinicabelezatotal.service;

import com.clinicabelezatotal.model.Agendamento;
import com.clinicabelezatotal.repository.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LembreteSchedulerTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private WhatsAppService whatsappService;

    @InjectMocks
    private LembreteScheduler lembreteScheduler;

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setLembreteEnviado(false);
    }

    @Test
    void enviarLembretes_envioComSucesso_deveMarcarComoEnviadoESalvar() {
        when(agendamentoRepository.findParaEnviarLembrete(any(LocalDate.class)))
            .thenReturn(List.of(agendamento));

        lembreteScheduler.enviarLembretesPendentes();

        assertTrue(agendamento.getLembreteEnviado());
        verify(agendamentoRepository, times(1)).save(agendamento);
        verify(whatsappService, times(1)).enviarLembrete(agendamento);
    }

    @Test
    void enviarLembretes_falhaNoEnvio_naoDeveMarcarNemSalvar() {
        when(agendamentoRepository.findParaEnviarLembrete(any(LocalDate.class)))
            .thenReturn(List.of(agendamento));
        doThrow(new RuntimeException("Falha ao enviar WhatsApp"))
            .when(whatsappService).enviarLembrete(agendamento);

        lembreteScheduler.enviarLembretesPendentes();

        assertFalse(agendamento.getLembreteEnviado());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void enviarLembretes_semPendentes_naoDeveChamarWhatsapp() {
        when(agendamentoRepository.findParaEnviarLembrete(any(LocalDate.class)))
            .thenReturn(List.of());

        lembreteScheduler.enviarLembretesPendentes();

        verify(whatsappService, never()).enviarLembrete(any(Agendamento.class));
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }
}
