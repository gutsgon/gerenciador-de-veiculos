const isLocalHost = ['localhost', '127.0.0.1'].includes(window.location.hostname);

const API_BASE = isLocalHost ? 'http://localhost:8080' : window.location.origin;

document.addEventListener("DOMContentLoaded", () => {
    const tabelaBody = document.querySelector("#tabelaVeiculos tbody");
    const estadoLista = document.querySelector("#estadoLista");

    const filterForm = document.getElementById("filterForm");
    const btnLimparFiltros = document.getElementById("btnLimparFiltros");
    const btnNovoVeiculo = document.getElementById("btnNovoVeiculo");

    const modalBackdrop = document.getElementById("modalBackdrop");
    const modalTitulo = document.getElementById("modalTitulo");
    const btnFecharModal = document.getElementById("btnFecharModal");
    const btnCancelarVeiculo = document.getElementById("btnCancelarVeiculo");
    const veiculoForm = document.getElementById("veiculoForm");
    const mensagemErro = document.getElementById("mensagemErro");
    const modalConteudoDetalhes = document.getElementById("modalConteudoDetalhes");

    // Elementos dos Modais Customizados
    const alertMessage = document.getElementById('alertMessage');
    const confirmModal = document.getElementById('confirmModal');
    const confirmText = document.getElementById('confirmText');
    const confirmOk = document.getElementById('confirmOk');
    const confirmCancel = document.getElementById('confirmCancel');


    const campoId = document.getElementById("veiculoId");
    const campoTipo = document.getElementById("tipoVeiculo");
    const campoModelo = document.getElementById("modelo");
    const campoFabricante = document.getElementById("fabricante");
    const campoAno = document.getElementById("ano");
    const campoPreco = document.getElementById("preco");
    const campoCor = document.getElementById("cor");
    const campoQtdPortas = document.getElementById("quantidadePortas");
    const campoTipoCombustivel = document.getElementById("tipoCombustivel");
    const campoCilindrada = document.getElementById("cilindrada");

    // Seletores que usam as classes definidas no style.css
    const camposCarro = document.querySelectorAll(".tipo-carro");
    const camposMoto = document.querySelectorAll(".tipo-moto");

    let editando = false;
    let tipoOriginalEdicao = null;
    

    
    function showAlert(message, type = 'error') {
        alertMessage.textContent = message;
        alertMessage.classList.remove('error', 'success');
        alertMessage.classList.add(type);

        alertMessage.classList.remove('hidden');
        
        // Esconde após 5 segundos
        setTimeout(() => {
            alertMessage.classList.add('hidden');
        }, 5000);
    }

    
    function showConfirm(message) {
        return new Promise(resolve => {
            confirmText.textContent = message;
            confirmModal.classList.remove('hidden');

            const handleConfirm = () => {
                confirmModal.classList.add('hidden');
                confirmOk.removeEventListener('click', handleConfirm);
                confirmCancel.removeEventListener('click', handleCancel);
                resolve(true);
            };

            const handleCancel = () => {
                confirmModal.classList.add('hidden');
                confirmOk.removeEventListener('click', handleConfirm);
                confirmCancel.removeEventListener('click', handleCancel);
                resolve(false);
            };

            confirmOk.addEventListener('click', handleConfirm);
            confirmCancel.addEventListener('click', handleCancel);
        });
    }


   
    async function tratarRespostaOuLancar(resp) {
        if (resp.ok) {
            return resp;
        }

        let mensagem = `Erro da API: ${resp.status}`;

        try {
            const erroBody = await resp.json();
            if (erroBody && erroBody.message) {
                mensagem = erroBody.message;
            }
        } catch (e) {
            try {
                const txt = await resp.text();
                if (txt) {
                    mensagem = `${mensagem} - ${txt}`;
                }
            } catch (_) {
            }
        }

        throw new Error(mensagem);
    }

    function abrirModal(titulo, isDetail = false) {
        modalTitulo.textContent = titulo;
        veiculoForm.classList.toggle('hidden', isDetail);
        modalConteudoDetalhes.classList.toggle('hidden', !isDetail);
        modalBackdrop.classList.remove("hidden");
        mensagemErro.textContent = "";
    }

    function fecharModal() {
        modalBackdrop.classList.add("hidden");
        veiculoForm.reset();
        campoId.value = "";
        campoTipo.disabled = false;
        tipoOriginalEdicao = null;
        editando = false;
        atualizarCamposEspecificos();
        // Garante que o formulário está visível ao fechar
        veiculoForm.classList.remove('hidden');
        modalConteudoDetalhes.classList.add('hidden');
    }

    function atualizarCamposEspecificos() {
        const tipo = campoTipo.value;

        camposCarro.forEach(el => {
            el.style.display = tipo === "CARRO" ? "flex" : "none";
            el.querySelectorAll('input, select').forEach(input => input.required = (tipo === "CARRO"));
        });

        camposMoto.forEach(el => {
            el.style.display = tipo === "MOTO" ? "flex" : "none";
            el.querySelectorAll('input, select').forEach(input => input.required = (tipo === "MOTO"));
        });
    }

    campoTipo.addEventListener("change", () => {
        if (editando && tipoOriginalEdicao) {
            campoTipo.value = tipoOriginalEdicao;
            return;
        }
        atualizarCamposEspecificos();
    });

    btnFecharModal.addEventListener("click", fecharModal);
    btnCancelarVeiculo.addEventListener("click", fecharModal);

    btnNovoVeiculo.addEventListener("click", () => {
        veiculoForm.reset();
        campoId.value = "";
        campoTipo.disabled = false;
        editando = false;
        tipoOriginalEdicao = null;
        atualizarCamposEspecificos();
        abrirModal("Novo veículo");
    });

    async function carregarVeiculos() {
        estadoLista.textContent = "Carregando veículos...";
        tabelaBody.innerHTML = "";

        const filtroTipo = document.getElementById("filtroTipo").value;
        const filtroModelo = document.getElementById("filtroModelo").value;
        const filtroCor = document.getElementById("filtroCor").value;
        const filtroAno = document.getElementById("filtroAno").value;

        try {
            const resp = await fetch(`${API_BASE}/veiculos`);
            await tratarRespostaOuLancar(resp);
            let data = await resp.json();

            if (filtroTipo) {
                data = data.filter(v => {
                    const tipo = v.tipo_veiculo || v.tipoVeiculo;
                    return tipo === filtroTipo;
                });
            }

            if (filtroModelo) {
                const m = filtroModelo.toLowerCase();
                data = data.filter(v => (v.modelo || "").toLowerCase().includes(m));
            }

            if (filtroCor) {
                const c = filtroCor.toLowerCase();
                data = data.filter(v => (v.cor || "").toLowerCase().includes(c));
            }

            if (filtroAno) {
                const anoNum = Number(filtroAno);
                if (!Number.isNaN(anoNum)) {
                    data = data.filter(v => v.ano === anoNum);
                }
            }

            if (!data || data.length === 0) {
                estadoLista.textContent = "Nenhum veículo encontrado.";
                return;
            }

            data.forEach(v => {
                const tipoVeiculo = v.tipo_veiculo || v.tipoVeiculo || "-";
                const tr = document.createElement("tr");

                let esp1 = "-"; 
                let esp2 = "-"; 

                if (tipoVeiculo === "CARRO") {
                    esp1 = v.quantidadePortas ? `${v.quantidadePortas} portas` : "-";
                    esp2 = v.tipoCombustivel || "-";
                } else if (tipoVeiculo === "MOTO") {
                    esp1 = v.cilindrada ? `${v.cilindrada} cc` : "-";
                    esp2 = "-"; 
                }

                tr.innerHTML = `
                    <td>${v.id}</td>
                    <td>${v.modelo}</td>
                    <td>${v.fabricante}</td>
                    <td>${v.ano}</td>
                    <td>${v.cor || '-'}</td>
                    <td>${(v.preco ?? 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
                    <td>${tipoVeiculo}</td>
                    <td class="campo-especifico">${esp1}</td>
                    <td class="campo-especifico">${esp2}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm" data-acao="detalhar">Detalhar</button>
                        <button class="btn btn-primary btn-sm" data-acao="editar">Editar</button>
                        <button class="btn btn-danger btn-sm" data-acao="excluir">Excluir</button>
                    </td>
                `;

                tr.querySelectorAll("button").forEach(btn => {
                    btn.addEventListener("click", () => {
                        const acao = btn.dataset.acao;
                        if (acao === "detalhar") {
                            mostrarDetalhes(v);
                        } else if (acao === "editar") {
                            abrirEdicao(v);
                        } else if (acao === "excluir") {
                            excluirVeiculo(v.id);
                        }
                    });
                });

                tabelaBody.appendChild(tr);
            });

            estadoLista.textContent = `Total: ${data.length} veículo(s).`;
        } catch (err) {
            console.error(err);
            estadoLista.textContent = err.message || "Erro ao carregar veículos.";
            showAlert(err.message || "Erro ao carregar veículos.");
        }
    }

    function mostrarDetalhes(v) {
        const tipo = v.tipo_veiculo || v.tipoVeiculo;
        
        let extraHtml = "";
        let tipoLabel = "";

        if (tipo === "CARRO") {
            tipoLabel = "Carro";
            extraHtml = `
                <p><strong>Quantidade de Portas:</strong> ${v.quantidadePortas || '-'}</p>
                <p><strong>Tipo de Combustível:</strong> ${v.tipoCombustivel || '-'}</p>
            `;
        } else if (tipo === "MOTO") {
            tipoLabel = "Moto";
            extraHtml = `
                <p><strong>Cilindrada (cc):</strong> ${v.cilindrada || '-'}</p>
            `;
        } else {
            tipoLabel = "Veículo Desconhecido";
        }

        modalConteudoDetalhes.innerHTML = `
            <p><strong>ID:</strong> ${v.id}</p>
            <p><strong>Tipo:</strong> ${tipoLabel} (${tipo})</p>
            <p><strong>Modelo:</strong> ${v.modelo}</p>
            <p><strong>Fabricante:</strong> ${v.fabricante}</p>
            <p><strong>Ano:</strong> ${v.ano}</p>
            <p><strong>Cor:</strong> ${v.cor}</p>
            <p><strong>Preço:</strong> ${(v.preco ?? 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</p>
            <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #1f2937;">
                <h4>Detalhes Específicos</h4>
                ${extraHtml}
            </div>
        `;

        abrirModal(`Detalhes do Veículo #${v.id}`, true);
    }

    function abrirEdicao(v) {
        const tipo = v.tipo_veiculo || v.tipoVeiculo;

        if (tipo !== "CARRO" && tipo !== "MOTO") {
            showAlert("Tipo de veículo inválido para edição.");
            return;
        }

        editando = true;
        tipoOriginalEdicao = tipo;

        campoId.value = v.id;
        campoTipo.value = tipo;
        campoTipo.disabled = true; 
        atualizarCamposEspecificos();

        campoModelo.value = v.modelo;
        campoFabricante.value = v.fabricante;
        campoAno.value = v.ano;
        campoPreco.value = v.preco;
        campoCor.value = v.cor;

        if (tipo === "CARRO") {
            campoQtdPortas.value = v.quantidadePortas || "";
            campoTipoCombustivel.value = v.tipoCombustivel || "";
            campoCilindrada.value = "";
        } else if (tipo === "MOTO") {
            campoCilindrada.value = v.cilindrada || "";
            campoQtdPortas.value = "";
            campoTipoCombustivel.value = "";
        }

        abrirModal(`Editar ${tipo === "CARRO" ? "carro" : "moto"} #${v.id}`);
    }

    veiculoForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        mensagemErro.textContent = "";

        const id = campoId.value || null;
        const tipo = campoTipo.value;

        if (!tipo) {
            mensagemErro.textContent = "Selecione o tipo de veículo.";
            return;
        }

        const precoNum = Number(campoPreco.value);
        const anoNum = Number(campoAno.value);
        if (precoNum <= 0 || Number.isNaN(anoNum)) {
             mensagemErro.textContent = "Preço deve ser maior que zero e Ano deve ser um número válido.";
             return;
        }


        const basePayload = {
            modelo: campoModelo.value,
            fabricante: campoFabricante.value,
            ano: anoNum,
            preco: precoNum,
            cor: campoCor.value
        };

        let url = "";
        let method = "";
        let payload = {};

        if (tipo === "CARRO") {
            const qtdPortasNum = Number(campoQtdPortas.value);
            const tipoComb = campoTipoCombustivel.value;
            
            payload = {
                ...basePayload,
                quantidadePortas: qtdPortasNum,
                tipoCombustivel: tipoComb
            };

            if (!qtdPortasNum || !tipoComb) {
                mensagemErro.textContent = "Informe quantidade de portas e tipo de combustível.";
                return;
            }
            
            if (Number.isNaN(qtdPortasNum) || qtdPortasNum < 1) {
                 mensagemErro.textContent = "Quantidade de portas inválida.";
                 return;
            }


            if (id) {
                url = `${API_BASE}/veiculos/carros`;
                method = "PATCH";
                payload.id = Number(id);
            } else {
                url = `${API_BASE}/veiculos/carros`;
                method = "POST";
            }
        } else if (tipo === "MOTO") {
            const cilindradaNum = Number(campoCilindrada.value);

            payload = {
                ...basePayload,
                cilindrada: cilindradaNum
            };

            if (!cilindradaNum) {
                mensagemErro.textContent = "Informe a cilindrada.";
                return;
            }
            
            if (Number.isNaN(cilindradaNum) || cilindradaNum < 50) {
                 mensagemErro.textContent = "Cilindrada inválida (mínimo 50cc).";
                 return;
            }


            if (id) {
                url = `${API_BASE}/veiculos/motos`;
                method = "PATCH";
                payload.id = Number(id);
            } else {
                url = `${API_BASE}/veiculos/motos`;
                method = "POST";
            }
        } else {
            mensagemErro.textContent = "Tipo de veículo inválido.";
            return;
        }

        try {
            const resp = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            await tratarRespostaOuLancar(resp);

            fecharModal();
            await carregarVeiculos();
            showAlert(`Veículo salvo com sucesso! ID: ${id || 'Novo'}`, 'success');

        } catch (err) {
            console.error(err);
            mensagemErro.textContent =
                err.message || "Erro ao salvar veículo. Veja o console para detalhes.";
        }
    });

    async function excluirVeiculo(id) {
        const confirmado = await showConfirm(`Confirma excluir o veículo ID ${id}?`);
        if (!confirmado) return;

        try {
            const resp = await fetch(`${API_BASE}/veiculos/${id}`, {
                method: "DELETE"
            });

            await tratarRespostaOuLancar(resp);

            await carregarVeiculos();
            showAlert(`Veículo ID ${id} excluído com sucesso.`, 'success');
        } catch (err) {
            console.error(err);
            showAlert(err.message || "Erro ao excluir veículo.");
        }
    }

    filterForm.addEventListener("submit", (e) => {
        e.preventDefault();
        carregarVeiculos();
    });

    btnLimparFiltros.addEventListener("click", () => {
        filterForm.reset();
        carregarVeiculos();
    });

    carregarVeiculos();
});