package com.clinicabelezatotal.config;

import com.clinicabelezatotal.model.*;
import com.clinicabelezatotal.model.Agendamento.StatusAgendamento;
import com.clinicabelezatotal.model.Usuario.PerfilUsuario;
import com.clinicabelezatotal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProcedimentoRepository procedimentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;
    private final HorarioAtendimentoRepository horarioRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        criarProcedimentosSeNaoExistirem();
        criarFuncionariosSeNaoExistirem();
        criarUsuariosSeNaoExistirem();
        criarClientesSeNaoExistirem();
        criarHorariosSeNaoExistirem();
        criarAgendamentosSeNaoExistirem();
    }

    private void criarProcedimentosSeNaoExistirem() {
        if (procedimentoRepository.count() == 0) {
            List<Procedimento> procedimentos = List.of(
                Procedimento.builder()
                    .nome("Limpeza de Pele")
                    .descricao("Limpeza profunda da pele com extração de impurezas")
                    .duracaoMinutos(60)
                    .valor(new BigDecimal("120.00"))
                    .categoria(Procedimento.CategoriaProcedimento.FACIAL)
                    .ativo(true)
                    .build(),
                Procedimento.builder()
                    .nome("Drenagem Linfática")
                    .descricao("Massagem para drenagem de líquidos e toxinas")
                    .duracaoMinutos(60)
                    .valor(new BigDecimal("150.00"))
                    .categoria(Procedimento.CategoriaProcedimento.CORPORAL)
                    .ativo(true)
                    .build(),
                Procedimento.builder()
                    .nome("Tratamento Capilar")
                    .descricao("Tratamento hidratante e nutritivo para cabelos")
                    .duracaoMinutos(90)
                    .valor(new BigDecimal("200.00"))
                    .categoria(Procedimento.CategoriaProcedimento.CAPILAR)
                    .ativo(true)
                    .build(),
                Procedimento.builder()
                    .nome("Peeling Químico")
                    .descricao("Renovação celular com ácidos naturais")
                    .duracaoMinutos(45)
                    .valor(new BigDecimal("180.00"))
                    .categoria(Procedimento.CategoriaProcedimento.FACIAL)
                    .ativo(true)
                    .build(),
                Procedimento.builder()
                    .nome("Massagem Modeladora")
                    .descricao("Massagem para modelagem corporal e redução de medidas")
                    .duracaoMinutos(60)
                    .valor(new BigDecimal("130.00"))
                    .categoria(Procedimento.CategoriaProcedimento.CORPORAL)
                    .ativo(true)
                    .build()
            );
            procedimentoRepository.saveAll(procedimentos);
            log.info("5 procedimentos criados com sucesso");
        }
    }

    private void criarFuncionariosSeNaoExistirem() {
        if (funcionarioRepository.count() == 0) {
            List<Procedimento> todosProcedimentos = procedimentoRepository.findAll();

            List<Funcionario> funcionarios = List.of(
                Funcionario.builder()
                    .nome("Ana Carolina Silva")
                    .telefone("(62) 99991-1111")
                    .cpf("111.111.111-11")
                    .especialidade("Esteticista Facial")
                    .procedimentosHabilitados(List.of(todosProcedimentos.get(0), todosProcedimentos.get(3)))
                    .ativo(true)
                    .build(),
                Funcionario.builder()
                    .nome("Beatriz Santos")
                    .telefone("(62) 99992-2222")
                    .cpf("222.222.222-22")
                    .especialidade("Esteticista Corporal")
                    .procedimentosHabilitados(List.of(todosProcedimentos.get(1), todosProcedimentos.get(4)))
                    .ativo(true)
                    .build(),
                Funcionario.builder()
                    .nome("Camila Oliveira")
                    .telefone("(62) 99993-3333")
                    .cpf("333.333.333-33")
                    .especialidade("Tricologista")
                    .procedimentosHabilitados(List.of(todosProcedimentos.get(2)))
                    .ativo(true)
                    .build(),
                Funcionario.builder()
                    .nome("Daniela Costa")
                    .telefone("(62) 99994-4444")
                    .cpf("444.444.444-44")
                    .especialidade("Esteticista Geral")
                    .procedimentosHabilitados(List.of(todosProcedimentos.get(0), todosProcedimentos.get(1), todosProcedimentos.get(3)))
                    .ativo(true)
                    .build(),
                Funcionario.builder()
                    .nome("Elena Martins")
                    .telefone("(62) 99995-5555")
                    .cpf("555.555.555-55")
                    .especialidade("Massoterapeuta")
                    .procedimentosHabilitados(List.of(todosProcedimentos.get(1), todosProcedimentos.get(4)))
                    .ativo(true)
                    .build()
            );
            funcionarioRepository.saveAll(funcionarios);
            log.info("5 funcionários criados com sucesso");
        }
    }

    private void criarUsuariosSeNaoExistirem() {
        if (usuarioRepository.count() == 0) {
            List<Funcionario> funcionarios = funcionarioRepository.findAll();

            List<Usuario> usuarios = List.of(
                Usuario.builder()
                    .nome("Administrador")
                    .email("admin@belezatotal.com.br")
                    .senha(passwordEncoder.encode("admin123"))
                    .perfil(PerfilUsuario.ADMIN)
                    .ativo(true)
                    .build(),
                Usuario.builder()
                    .nome("Silvana da Costa")
                    .email("silvana@belezatotal.com.br")
                    .senha(passwordEncoder.encode("silvana123"))
                    .perfil(PerfilUsuario.ESTETICISTA_ADMINISTRADORA)
                    .ativo(true)
                    .build(),
                Usuario.builder()
                    .nome("Fernanda Lima")
                    .email("fernanda@belezatotal.com.br")
                    .senha(passwordEncoder.encode("fernanda123"))
                    .perfil(PerfilUsuario.RECEPCIONISTA)
                    .ativo(true)
                    .build(),
                Usuario.builder()
                    .nome("Ana Carolina Silva")
                    .email("ana@belezatotal.com.br")
                    .senha(passwordEncoder.encode("ana123"))
                    .perfil(PerfilUsuario.FUNCIONARIO)
                    .funcionario(funcionarios.get(0))
                    .ativo(true)
                    .build(),
                Usuario.builder()
                    .nome("Beatriz Santos")
                    .email("beatriz@belezatotal.com.br")
                    .senha(passwordEncoder.encode("beatriz123"))
                    .perfil(PerfilUsuario.FUNCIONARIO)
                    .funcionario(funcionarios.get(1))
                    .ativo(true)
                    .build()
            );
            usuarioRepository.saveAll(usuarios);
            log.info("5 usuários criados com sucesso");
        }
    }

    private void criarClientesSeNaoExistirem() {
        if (clienteRepository.count() == 0) {
            List<Cliente> clientes = List.of(
                Cliente.builder()
                    .nome("Maria José da Silva")
                    .telefone("(62) 98881-1111")
                    .cpf("111.222.333-44")
                    .email("maria.jose@email.com")
                    .dataNascimento(LocalDate.of(1985, 3, 15))
                    .consentimentoDados(true)
                    .consentimentoWhatsApp(true)
                    .dataConsentimento(LocalDate.now().atStartOfDay())
                    .ativo(true)
                    .build(),
                Cliente.builder()
                    .nome("Juliana Pereira")
                    .telefone("(62) 98882-2222")
                    .cpf("222.333.444-55")
                    .email("juliana@email.com")
                    .dataNascimento(LocalDate.of(1990, 7, 22))
                    .consentimentoDados(true)
                    .consentimentoWhatsApp(true)
                    .dataConsentimento(LocalDate.now().atStartOfDay())
                    .ativo(true)
                    .build(),
                Cliente.builder()
                    .nome("Patrícia Ferreira")
                    .telefone("(62) 98883-3333")
                    .cpf("333.444.555-66")
                    .email("patricia@email.com")
                    .dataNascimento(LocalDate.of(1978, 11, 8))
                    .consentimentoDados(true)
                    .consentimentoWhatsApp(false)
                    .dataConsentimento(LocalDate.now().atStartOfDay())
                    .ativo(true)
                    .build(),
                Cliente.builder()
                    .nome("Camila Souza")
                    .telefone("(62) 98884-4444")
                    .cpf("444.555.666-77")
                    .email("camila@email.com")
                    .dataNascimento(LocalDate.of(1995, 5, 30))
                    .consentimentoDados(true)
                    .consentimentoWhatsApp(true)
                    .dataConsentimento(LocalDate.now().atStartOfDay())
                    .ativo(true)
                    .build(),
                Cliente.builder()
                    .nome("Renata Almeida")
                    .telefone("(62) 98885-5555")
                    .cpf("555.666.777-88")
                    .email("renata@email.com")
                    .dataNascimento(LocalDate.of(1988, 9, 12))
                    .consentimentoDados(true)
                    .consentimentoWhatsApp(true)
                    .dataConsentimento(LocalDate.now().atStartOfDay())
                    .ativo(true)
                    .build()
            );
            clienteRepository.saveAll(clientes);
            log.info("5 clientes criados com sucesso");
        }
    }

    private void criarHorariosSeNaoExistirem() {
        if (horarioRepository.count() == 0) {
            List<Funcionario> funcionarios = funcionarioRepository.findAll();

            List<HorarioAtendimento> horarios = List.of(
                HorarioAtendimento.builder()
                    .funcionario(funcionarios.get(0))
                    .diaSemana(DayOfWeek.MONDAY)
                    .horaInicio(LocalTime.of(8, 0))
                    .horaFim(LocalTime.of(18, 0))
                    .ativo(true)
                    .build(),
                HorarioAtendimento.builder()
                    .funcionario(funcionarios.get(1))
                    .diaSemana(DayOfWeek.MONDAY)
                    .horaInicio(LocalTime.of(9, 0))
                    .horaFim(LocalTime.of(17, 0))
                    .ativo(true)
                    .build(),
                HorarioAtendimento.builder()
                    .funcionario(funcionarios.get(2))
                    .diaSemana(DayOfWeek.MONDAY)
                    .horaInicio(LocalTime.of(8, 0))
                    .horaFim(LocalTime.of(16, 0))
                    .ativo(true)
                    .build(),
                HorarioAtendimento.builder()
                    .funcionario(funcionarios.get(0))
                    .diaSemana(DayOfWeek.TUESDAY)
                    .horaInicio(LocalTime.of(8, 0))
                    .horaFim(LocalTime.of(18, 0))
                    .ativo(true)
                    .build(),
                HorarioAtendimento.builder()
                    .funcionario(funcionarios.get(1))
                    .diaSemana(DayOfWeek.TUESDAY)
                    .horaInicio(LocalTime.of(9, 0))
                    .horaFim(LocalTime.of(17, 0))
                    .ativo(true)
                    .build()
            );
            horarioRepository.saveAll(horarios);
            log.info("5 horários de atendimento criados com sucesso");
        }
    }

    private void criarAgendamentosSeNaoExistirem() {
        if (agendamentoRepository.count() == 0) {
            List<Cliente> clientes = clienteRepository.findAll();
            List<Funcionario> funcionarios = funcionarioRepository.findAll();
            List<Procedimento> procedimentos = procedimentoRepository.findAll();

            LocalDate segunda = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
            LocalDate terca = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));

            List<Agendamento> agendamentos = List.of(
                Agendamento.builder()
                    .cliente(clientes.get(0))
                    .funcionario(funcionarios.get(0))
                    .procedimento(procedimentos.get(0))
                    .data(segunda)
                    .horaInicio(LocalTime.of(9, 0))
                    .horaFim(LocalTime.of(10, 0))
                    .status(StatusAgendamento.AGENDADO)
                    .lembreteEnviado(false)
                    .build(),
                Agendamento.builder()
                    .cliente(clientes.get(1))
                    .funcionario(funcionarios.get(1))
                    .procedimento(procedimentos.get(1))
                    .data(segunda)
                    .horaInicio(LocalTime.of(10, 0))
                    .horaFim(LocalTime.of(11, 0))
                    .status(StatusAgendamento.CONFIRMADO)
                    .lembreteEnviado(true)
                    .build(),
                Agendamento.builder()
                    .cliente(clientes.get(2))
                    .funcionario(funcionarios.get(2))
                    .procedimento(procedimentos.get(2))
                    .data(segunda)
                    .horaInicio(LocalTime.of(14, 0))
                    .horaFim(LocalTime.of(15, 30))
                    .status(StatusAgendamento.AGENDADO)
                    .lembreteEnviado(false)
                    .build(),
                Agendamento.builder()
                    .cliente(clientes.get(3))
                    .funcionario(funcionarios.get(0))
                    .procedimento(procedimentos.get(3))
                    .data(terca)
                    .horaInicio(LocalTime.of(9, 0))
                    .horaFim(LocalTime.of(9, 45))
                    .status(StatusAgendamento.AGENDADO)
                    .lembreteEnviado(false)
                    .build(),
                Agendamento.builder()
                    .cliente(clientes.get(4))
                    .funcionario(funcionarios.get(1))
                    .procedimento(procedimentos.get(4))
                    .data(terca)
                    .horaInicio(LocalTime.of(14, 0))
                    .horaFim(LocalTime.of(15, 0))
                    .status(StatusAgendamento.AGENDADO)
                    .lembreteEnviado(false)
                    .build()
            );
            agendamentoRepository.saveAll(agendamentos);
            log.info("5 agendamentos criados com sucesso");
        }
    }
}
