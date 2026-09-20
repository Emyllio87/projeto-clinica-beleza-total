package com.clinicabelezatotal.service;

import com.clinicabelezatotal.model.Agendamento;
import com.clinicabelezatotal.model.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WhatsAppService {

    private final WebClient webClient;
    private final boolean habilitado;

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.api.key}")
    private String apiKey;

    @Value("${whatsapp.phone-number-id}")
    private String instanceName;

    private final ConcurrentHashMap<String, LocalDateTime> mensagensEnviadas = new ConcurrentHashMap<>();

    public WhatsAppService(
            @Value("${whatsapp.habilitado:false}") boolean habilitado) {
        this.habilitado = habilitado;
        this.webClient = WebClient.builder().build();
    }

    public void enviarLembrete(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        if (!podeEnviar(cliente)) {
            log.info("Lembrete não enviado - cliente {} sem consentimento WhatsApp", cliente.getId());
            return;
        }

        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        String mensagem = String.format(
            "Olá %s! Lembrete: seu agendamento de %s é dia %s às %s. Clínica Beleza Total",
            cliente.getNome(),
            agendamento.getProcedimento().getNome(),
            agendamento.getData().format(fmtData),
            agendamento.getHoraInicio().format(fmtHora)
        );

        enviarMensagem(cliente.getTelefone(), mensagem);
    }

    public void enviarNotificacaoAlteracao(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        if (!podeEnviar(cliente)) {
            log.info("Notificação de alteração não enviada - cliente {} sem consentimento", cliente.getId());
            return;
        }

        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        String mensagem = String.format(
            "Olá %s! Seu agendamento de %s foi alterado para dia %s às %s. Clínica Beleza Total",
            cliente.getNome(),
            agendamento.getProcedimento().getNome(),
            agendamento.getData().format(fmtData),
            agendamento.getHoraInicio().format(fmtHora)
        );

        enviarMensagem(cliente.getTelefone(), mensagem);
    }

    public void enviarConfirmacao(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        if (!podeEnviar(cliente)) {
            log.info("Confirmação não enviada - cliente {} sem consentimento WhatsApp", cliente.getId());
            return;
        }

        String chave = agendamento.getId() + ":confirmacao";
        if (jaEnviado(chave)) {
            log.info("Confirmação já enviada para agendamento {}, ignorando duplicata", agendamento.getId());
            return;
        }

        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        String mensagem = String.format(
            "Olá %s! Seu agendamento de %s foi confirmado para dia %s às %s. Clínica Beleza Total",
            cliente.getNome(),
            agendamento.getProcedimento().getNome(),
            agendamento.getData().format(fmtData),
            agendamento.getHoraInicio().format(fmtHora)
        );

        enviarMensagem(cliente.getTelefone(), mensagem);
        registrarEnvio(chave);
    }

    public void enviarNotificacaoAlteracaoStatus(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        if (!podeEnviar(cliente)) {
            log.info("Notificação de mudança de status não enviada - cliente {} sem consentimento", cliente.getId());
            return;
        }

        if (agendamento.getStatus() == Agendamento.StatusAgendamento.REALIZADO) {
            enviarAgradecimento(agendamento);
            return;
        }

        log.info("Notificação de status não enviada para {} - apenas REALIZADO gera notificação", agendamento.getStatus());
    }

    public void enviarAgradecimento(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        if (!podeEnviar(cliente)) {
            return;
        }

        String chave = agendamento.getId() + ":agradecimento";
        if (jaEnviado(chave)) {
            log.info("Agradecimento já enviado para agendamento {}, ignorando duplicata", agendamento.getId());
            return;
        }

        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        String mensagem = String.format(
            "Olá, %s! Agradecemos por confiar na Clínica Beleza Total, esperamos nos ver em breve!",
            cliente.getNome()
        );

        enviarMensagem(cliente.getTelefone(), mensagem);
        registrarEnvio(chave);
    }

    public void enviarNotificacaoCancelamento(Agendamento agendamento) {
        Cliente cliente = agendamento.getCliente();
        if (!podeEnviar(cliente)) {
            log.info("Notificação de cancelamento não enviada - cliente {} sem consentimento", cliente.getId());
            return;
        }

        String chave = agendamento.getId() + ":cancelamento";
        if (jaEnviado(chave)) {
            log.info("Cancelamento já notificado para agendamento {}, ignorando duplicata", agendamento.getId());
            return;
        }

        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        String mensagem = String.format(
            "Olá %s! Seu agendamento de %s dia %s às %s foi cancelado. Clínica Beleza Total",
            cliente.getNome(),
            agendamento.getProcedimento().getNome(),
            agendamento.getData().format(fmtData),
            agendamento.getHoraInicio().format(fmtHora)
        );

        enviarMensagem(cliente.getTelefone(), mensagem);
        registrarEnvio(chave);
    }

    private boolean podeEnviar(Cliente cliente) {
        return cliente.getConsentimentoWhatsApp() != null && cliente.getConsentimentoWhatsApp();
    }

    private boolean jaEnviado(String chave) {
        LocalDateTime enviado = mensagensEnviadas.get(chave);
        if (enviado != null && enviado.isAfter(LocalDateTime.now().minusMinutes(5))) {
            return true;
        }
        return false;
    }

    private void registrarEnvio(String chave) {
        mensagensEnviadas.put(chave, LocalDateTime.now());
        mensagensEnviadas.entrySet().removeIf(e ->
            e.getValue().isBefore(LocalDateTime.now().minusMinutes(10)));
    }

    private void enviarMensagem(String telefone, String mensagem) {
        if (!habilitado) {
            log.info("[WHATSAPP MOCK] Para: {} | Mensagem: {}", telefone, mensagem);
            return;
        }

        String telefoneFormatado = telefone.replaceAll("[^0-9]", "");

        if (!telefoneFormatado.startsWith("55")) {
            telefoneFormatado = "55" + telefoneFormatado;
        }

        Map<String, Object> body = Map.of(
            "number", telefoneFormatado,
            "text", mensagem
        );

        try {
            String resposta = webClient.post()
                .uri(apiUrl + "/message/sendText/{instance}", instanceName)
                .header("apikey", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("WhatsApp enviado com sucesso para {}: {}", telefone, resposta);
        } catch (Exception e) {
            log.error("Erro ao enviar WhatsApp para {}: {}", telefone, e.getMessage(), e);
        }
    }
}
