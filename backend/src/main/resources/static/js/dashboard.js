// Global data
let clientes = [];
let funcionarios = [];
let procedimentos = [];
let agendamentos = [];
let usuarios = [];
let horarios = [];

// Check authentication
if (!auth.isAuthenticated()) {
    window.location.href = 'login.html';
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
    loadDashboard();
    setupFormHandlers();
});

function loadUserInfo() {
    const user = auth.getUser();
    if (user) {
        document.getElementById('userProfile').textContent = `${user.nome} - ${user.perfil}`;
        
        // Hide menu items based on role
        if (!auth.hasRole(['ADMIN', 'ESTETICISTA_ADMINISTRADORA'])) {
            document.getElementById('menuUsuarios').style.display = 'none';
        }
        if (!auth.hasRole(['ADMIN', 'ESTETICISTA_ADMINISTRADORA', 'RECEPCIONISTA'])) {
            document.getElementById('menuClientes').style.display = 'none';
            document.getElementById('menuFuncionarios').style.display = 'none';
        }
    }
}

function showSection(section) {
    document.querySelectorAll('.section').forEach(s => s.style.display = 'none');
    document.getElementById(`section${section.charAt(0).toUpperCase() + section.slice(1)}`).style.display = 'block';
    
    document.querySelectorAll('.sidebar-menu a').forEach(a => a.classList.remove('active'));
    event.target.classList.add('active');
    
    // Load data for section
    switch(section) {
        case 'dashboard': loadDashboard(); break;
        case 'clientes': loadClientes(); break;
        case 'funcionarios': loadFuncionarios(); break;
        case 'procedimentos': loadProcedimentos(); break;
        case 'agendamentos': loadAgendamentos(); break;
        case 'usuarios': loadUsuarios(); break;
        case 'horarios': loadHorarios(); break;
    }
}

// Dashboard
async function loadDashboard() {
    try {
        const [c, f, p, a] = await Promise.all([
            api.get('/clientes'),
            api.get('/funcionarios'),
            api.get('/procedimentos'),
            api.get('/agendamentos/periodo?inicio=2020-01-01&fim=2030-12-31')
        ]);
        
        clientes = c || [];
        funcionarios = f || [];
        procedimentos = p || [];
        agendamentos = a || [];
        
        document.getElementById('statClientes').textContent = clientes.length;
        document.getElementById('statFuncionarios').textContent = funcionarios.length;
        document.getElementById('statProcedimentos').textContent = procedimentos.length;
        document.getElementById('statAgendamentos').textContent = agendamentos.length;
    } catch (error) {
        console.error('Erro ao carregar dashboard:', error);
    }
}

// Clientes
async function loadClientes() {
    try {
        clientes = await api.get('/clientes') || [];
        const tbody = document.getElementById('clientesTable');
        tbody.innerHTML = clientes.map(c => `
            <tr>
                <td>${c.id}</td>
                <td>${c.nome}</td>
                <td>${c.telefone}</td>
                <td>${c.email || '-'}</td>
                <td>
                    <button class="btn btn-warning" onclick="editCliente(${c.id})">Editar</button>
                    <button class="btn btn-danger" onclick="deleteCliente(${c.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar clientes:', error);
    }
}

function openClienteModal(cliente = null) {
    document.getElementById('clienteModalTitle').textContent = cliente ? 'Editar Cliente' : 'Novo Cliente';
    document.getElementById('clienteId').value = cliente ? cliente.id : '';
    document.getElementById('clienteNome').value = cliente ? cliente.nome : '';
    document.getElementById('clienteTelefone').value = cliente ? cliente.telefone : '';
    document.getElementById('clienteCpf').value = cliente ? cliente.cpf || '' : '';
    document.getElementById('clienteEmail').value = cliente ? cliente.email || '' : '';
    document.getElementById('clienteDataNascimento').value = cliente ? cliente.dataNascimento || '' : '';
    document.getElementById('clienteConsentimento').checked = cliente ? cliente.consentimentoDados : false;
    ui.showModal('clienteModal');
}

function editCliente(id) {
    const cliente = clientes.find(c => c.id === id);
    if (cliente) openClienteModal(cliente);
}

async function deleteCliente(id) {
    if (confirm('Tem certeza que deseja excluir este cliente?')) {
        try {
            await api.delete(`/clientes/${id}`);
            ui.showAlert('Cliente excluído com sucesso!');
            loadClientes();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    }
}

// Funcionários
async function loadFuncionarios() {
    try {
        funcionarios = await api.get('/funcionarios') || [];
        const tbody = document.getElementById('funcionariosTable');
        tbody.innerHTML = funcionarios.map(f => `
            <tr>
                <td>${f.id}</td>
                <td>${f.nome}</td>
                <td>${f.telefone}</td>
                <td>${f.especialidade || '-'}</td>
                <td>
                    <button class="btn btn-warning" onclick="editFuncionario(${f.id})">Editar</button>
                    <button class="btn btn-danger" onclick="deleteFuncionario(${f.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar funcionários:', error);
    }
}

async function openFuncionarioModal(funcionario = null) {
    document.getElementById('funcionarioModalTitle').textContent = funcionario ? 'Editar Funcionário' : 'Novo Funcionário';
    document.getElementById('funcionarioId').value = funcionario ? funcionario.id : '';
    document.getElementById('funcionarioNome').value = funcionario ? funcionario.nome : '';
    document.getElementById('funcionarioTelefone').value = funcionario ? funcionario.telefone : '';
    document.getElementById('funcionarioCpf').value = funcionario ? funcionario.cpf : '';
    document.getElementById('funcionarioEspecialidade').value = funcionario ? funcionario.especialidade || '' : '';
    
    if (procedimentos.length === 0) {
        procedimentos = await api.get('/procedimentos') || [];
    }
    const select = document.getElementById('funcionarioProcedimentos');
    select.innerHTML = procedimentos.map(p => 
        `<option value="${p.id}" ${funcionario?.procedimentoIds?.includes(p.id) ? 'selected' : ''}>${p.nome}</option>`
    ).join('');
    
    ui.showModal('funcionarioModal');
}

function editFuncionario(id) {
    const funcionario = funcionarios.find(f => f.id === id);
    if (funcionario) openFuncionarioModal(funcionario);
}

async function deleteFuncionario(id) {
    if (confirm('Tem certeza que deseja excluir este funcionário?')) {
        try {
            await api.delete(`/funcionarios/${id}`);
            ui.showAlert('Funcionário excluído com sucesso!');
            loadFuncionarios();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    }
}

// Procedimentos
async function loadProcedimentos() {
    try {
        procedimentos = await api.get('/procedimentos') || [];
        const tbody = document.getElementById('procedimentosTable');
        tbody.innerHTML = procedimentos.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.nome}</td>
                <td><span class="badge badge-info">${p.categoria}</span></td>
                <td>${p.duracaoMinutos} min</td>
                <td>${format.currency(p.valor)}</td>
                <td>
                    <button class="btn btn-warning" onclick="editProcedimento(${p.id})">Editar</button>
                    <button class="btn btn-danger" onclick="deleteProcedimento(${p.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar procedimentos:', error);
    }
}

function openProcedimentoModal(procedimento = null) {
    document.getElementById('procedimentoModalTitle').textContent = procedimento ? 'Editar Procedimento' : 'Novo Procedimento';
    document.getElementById('procedimentoId').value = procedimento ? procedimento.id : '';
    document.getElementById('procedimentoNome').value = procedimento ? procedimento.nome : '';
    document.getElementById('procedimentoDescricao').value = procedimento ? procedimento.descricao : '';
    document.getElementById('procedimentoDuracao').value = procedimento ? procedimento.duracaoMinutos : '';
    document.getElementById('procedimentoValor').value = procedimento ? procedimento.valor : '';
    document.getElementById('procedimentoCategoria').value = procedimento ? procedimento.categoria : 'FACIAL';
    ui.showModal('procedimentoModal');
}

function editProcedimento(id) {
    const procedimento = procedimentos.find(p => p.id === id);
    if (procedimento) openProcedimentoModal(procedimento);
}

async function deleteProcedimento(id) {
    if (confirm('Tem certeza que deseja excluir este procedimento?')) {
        try {
            await api.delete(`/procedimentos/${id}`);
            ui.showAlert('Procedimento excluído com sucesso!');
            loadProcedimentos();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    }
}

// Agendamentos
async function loadAgendamentos() {
    try {
        agendamentos = await api.get('/agendamentos/periodo?inicio=2020-01-01&fim=2030-12-31') || [];
        
        if (clientes.length === 0) clientes = await api.get('/clientes') || [];
        if (funcionarios.length === 0) funcionarios = await api.get('/funcionarios') || [];
        if (procedimentos.length === 0) procedimentos = await api.get('/procedimentos') || [];
        
        const tbody = document.getElementById('agendamentosTable');
        tbody.innerHTML = agendamentos.map(a => {
            const cliente = clientes.find(c => c.id === a.clienteId);
            const funcionario = funcionarios.find(f => f.id === a.funcionarioId);
            const procedimento = procedimentos.find(p => p.id === a.procedimentoId);
            
            return `
                <tr>
                    <td>${a.id}</td>
                    <td>${cliente?.nome || '-'}</td>
                    <td>${funcionario?.nome || '-'}</td>
                    <td>${procedimento?.nome || '-'}</td>
                    <td>${format.date(a.data)}</td>
                    <td>${a.horaInicio}</td>
                    <td>${format.status(a.status || 'AGENDADO')}</td>
                    <td>
                        <button class="btn btn-warning" onclick="editAgendamento(${a.id})">Editar</button>
                        <button class="btn btn-danger" onclick="cancelarAgendamento(${a.id})">Cancelar</button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        console.error('Erro ao carregar agendamentos:', error);
    }
}

async function openAgendamentoModal(agendamento = null) {
    document.getElementById('agendamentoModalTitle').textContent = agendamento ? 'Editar Agendamento' : 'Novo Agendamento';
    document.getElementById('agendamentoId').value = agendamento ? agendamento.id : '';
    document.getElementById('agendamentoData').value = agendamento ? agendamento.data : '';
    document.getElementById('agendamentoHora').value = agendamento ? agendamento.horaInicio : '';
    document.getElementById('agendamentoObservacoes').value = agendamento ? agendamento.observacoes || '' : '';
    
    if (clientes.length === 0) clientes = await api.get('/clientes') || [];
    if (procedimentos.length === 0) procedimentos = await api.get('/procedimentos') || [];
    
    ui.populateSelect('agendamentoCliente', clientes, 'id', 'nome');
    ui.populateSelect('agendamentoProcedimento', procedimentos, 'id', 'nome');
    
    if (agendamento) {
        document.getElementById('agendamentoCliente').value = agendamento.clienteId;
        document.getElementById('agendamentoProcedimento').value = agendamento.procedimentoId;
        await carregarFuncionariosPorProcedimento();
        document.getElementById('agendamentoFuncionario').value = agendamento.funcionarioId;
    }
    
    ui.showModal('agendamentoModal');
}

async function carregarFuncionariosPorProcedimento() {
    const procedimentoId = document.getElementById('agendamentoProcedimento').value;
    if (procedimentoId) {
        try {
            const funcs = await api.get(`/funcionarios/procedimento/${procedimentoId}`);
            ui.populateSelect('agendamentoFuncionario', funcs || [], 'id', 'nome');
        } catch (error) {
            console.error('Erro ao carregar funcionários:', error);
        }
    }
}

function editAgendamento(id) {
    const agendamento = agendamentos.find(a => a.id === id);
    if (agendamento) openAgendamentoModal(agendamento);
}

async function cancelarAgendamento(id) {
    if (confirm('Tem certeza que deseja cancelar este agendamento?')) {
        try {
            await api.patch(`/agendamentos/${id}/cancelar`);
            ui.showAlert('Agendamento cancelado com sucesso!');
            loadAgendamentos();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    }
}

// Usuários
async function loadUsuarios() {
    try {
        usuarios = await api.get('/usuarios') || [];
        const tbody = document.getElementById('usuariosTable');
        tbody.innerHTML = usuarios.map(u => `
            <tr>
                <td>${u.id}</td>
                <td>${u.nome}</td>
                <td>${u.email}</td>
                <td><span class="badge badge-info">${u.perfil}</span></td>
                <td>
                    <button class="btn btn-warning" onclick="editUsuario(${u.id})">Editar</button>
                    <button class="btn btn-danger" onclick="deleteUsuario(${u.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar usuários:', error);
    }
}

function openUsuarioModal(usuario = null) {
    document.getElementById('usuarioModalTitle').textContent = usuario ? 'Editar Usuário' : 'Novo Usuário';
    document.getElementById('usuarioId').value = usuario ? usuario.id : '';
    document.getElementById('usuarioNome').value = usuario ? usuario.nome : '';
    document.getElementById('usuarioEmail').value = usuario ? usuario.email : '';
    document.getElementById('usuarioSenha').value = '';
    document.getElementById('usuarioPerfil').value = usuario ? usuario.perfil : 'FUNCIONARIO';
    ui.showModal('usuarioModal');
}

function editUsuario(id) {
    const usuario = usuarios.find(u => u.id === id);
    if (usuario) openUsuarioModal(usuario);
}

async function deleteUsuario(id) {
    if (confirm('Tem certeza que deseja excluir este usuário?')) {
        try {
            await api.delete(`/usuarios/${id}`);
            ui.showAlert('Usuário excluído com sucesso!');
            loadUsuarios();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    }
}

// Horários
async function loadHorarios() {
    try {
        horarios = await api.get('/horarios/funcionario/1') || [];
        if (funcionarios.length === 0) funcionarios = await api.get('/funcionarios') || [];
        
        const allHorarios = [];
        for (const func of funcionarios) {
            const h = await api.get(`/horarios/funcionario/${func.id}`);
            if (h) h.forEach(item => allHorarios.push({...item, funcionarioNome: func.nome}));
        }
        horarios = allHorarios;
        
        const tbody = document.getElementById('horariosTable');
        tbody.innerHTML = horarios.map(h => `
            <tr>
                <td>${h.id}</td>
                <td>${h.funcionarioNome || '-'}</td>
                <td>${h.diaSemana}</td>
                <td>${h.horaInicio}</td>
                <td>${h.horaFim}</td>
                <td>
                    <button class="btn btn-danger" onclick="deleteHorario(${h.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar horários:', error);
    }
}

async function openHorarioModal() {
    if (funcionarios.length === 0) funcionarios = await api.get('/funcionarios') || [];
    ui.populateSelect('horarioFuncionario', funcionarios, 'id', 'nome');
    ui.showModal('horarioModal');
}

async function deleteHorario(id) {
    if (confirm('Tem certeza que deseja excluir este horário?')) {
        try {
            await api.delete(`/horarios/${id}`);
            ui.showAlert('Horário excluído com sucesso!');
            loadHorarios();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    }
}

// Form handlers
function setupFormHandlers() {
    // Cliente
    document.getElementById('clienteForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('clienteId').value;
        const data = {
            nome: document.getElementById('clienteNome').value,
            telefone: document.getElementById('clienteTelefone').value,
            cpf: document.getElementById('clienteCpf').value || null,
            email: document.getElementById('clienteEmail').value || null,
            dataNascimento: document.getElementById('clienteDataNascimento').value || null,
            consentimentoDados: document.getElementById('clienteConsentimento').checked,
            consentimentoWhatsApp: document.getElementById('clienteConsentimento').checked
        };
        
        try {
            if (id) {
                await api.put(`/clientes/${id}`, data);
                ui.showAlert('Cliente atualizado com sucesso!');
            } else {
                await api.post('/clientes', data);
                ui.showAlert('Cliente cadastrado com sucesso!');
            }
            ui.hideModal('clienteModal');
            loadClientes();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    });

    // Funcionário
    document.getElementById('funcionarioForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('funcionarioId').value;
        const procedimentoIds = Array.from(document.getElementById('funcionarioProcedimentos').selectedOptions).map(o => parseInt(o.value));
        
        const data = {
            nome: document.getElementById('funcionarioNome').value,
            telefone: document.getElementById('funcionarioTelefone').value,
            cpf: document.getElementById('funcionarioCpf').value,
            especialidade: document.getElementById('funcionarioEspecialidade').value || null,
            procedimentoIds: procedimentoIds
        };
        
        try {
            if (id) {
                await api.put(`/funcionarios/${id}`, data);
                ui.showAlert('Funcionário atualizado com sucesso!');
            } else {
                await api.post('/funcionarios', data);
                ui.showAlert('Funcionário cadastrado com sucesso!');
            }
            ui.hideModal('funcionarioModal');
            loadFuncionarios();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    });

    // Procedimento
    document.getElementById('procedimentoForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('procedimentoId').value;
        const data = {
            nome: document.getElementById('procedimentoNome').value,
            descricao: document.getElementById('procedimentoDescricao').value,
            duracaoMinutos: parseInt(document.getElementById('procedimentoDuracao').value),
            valor: parseFloat(document.getElementById('procedimentoValor').value),
            categoria: document.getElementById('procedimentoCategoria').value
        };
        
        try {
            if (id) {
                await api.put(`/procedimentos/${id}`, data);
                ui.showAlert('Procedimento atualizado com sucesso!');
            } else {
                await api.post('/procedimentos', data);
                ui.showAlert('Procedimento cadastrado com sucesso!');
            }
            ui.hideModal('procedimentoModal');
            loadProcedimentos();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    });

    // Agendamento
    document.getElementById('agendamentoForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('agendamentoId').value;
        const data = {
            clienteId: parseInt(document.getElementById('agendamentoCliente').value),
            procedimentoId: parseInt(document.getElementById('agendamentoProcedimento').value),
            funcionarioId: parseInt(document.getElementById('agendamentoFuncionario').value),
            data: document.getElementById('agendamentoData').value,
            horaInicio: document.getElementById('agendamentoHora').value,
            observacoes: document.getElementById('agendamentoObservacoes').value || null
        };
        
        try {
            if (id) {
                await api.put(`/agendamentos/${id}`, data);
                ui.showAlert('Agendamento atualizado com sucesso!');
            } else {
                await api.post('/agendamentos', data);
                ui.showAlert('Agendamento realizado com sucesso!');
            }
            ui.hideModal('agendamentoModal');
            loadAgendamentos();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    });

    // Usuário
    document.getElementById('usuarioForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('usuarioId').value;
        const senha = document.getElementById('usuarioSenha').value;
        
        const data = {
            nome: document.getElementById('usuarioNome').value,
            email: document.getElementById('usuarioEmail').value,
            senha: senha || undefined,
            perfil: document.getElementById('usuarioPerfil').value
        };
        
        try {
            if (id) {
                await api.put(`/usuarios/${id}`, data);
                ui.showAlert('Usuário atualizado com sucesso!');
            } else {
                if (!senha) {
                    ui.showAlert('Senha é obrigatória para novo usuário', 'error');
                    return;
                }
                await api.post('/usuarios', data);
                ui.showAlert('Usuário cadastrado com sucesso!');
            }
            ui.hideModal('usuarioModal');
            loadUsuarios();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    });

    // Horário
    document.getElementById('horarioForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            funcionarioId: parseInt(document.getElementById('horarioFuncionario').value),
            diaSemana: document.getElementById('horarioDia').value,
            horaInicio: document.getElementById('horarioInicio').value,
            horaFim: document.getElementById('horarioFim').value
        };
        
        try {
            await api.post('/horarios', data);
            ui.showAlert('Horário cadastrado com sucesso!');
            ui.hideModal('horarioModal');
            loadHorarios();
        } catch (error) {
            ui.showAlert(error.message, 'error');
        }
    });
}
