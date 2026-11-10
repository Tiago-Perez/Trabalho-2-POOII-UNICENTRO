package edu.unicentro.trabalho2pooii.command;

import edu.unicentro.trabalho2pooii.service.ServiceException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command - papel: Invoker. Executa os comandos e guarda os já executados numa pilha,
 * para que o último possa ser desfeito. Fica na Sessao porque as telas são recriadas
 * a cada navegação e o histórico precisa sobreviver a isso.
 */
public class HistoricoComandos {

    private static final int LIMITE = 20;

    private final Deque<ComandoNota> executados = new ArrayDeque<>();

    // Só entra no histórico se executar() terminar sem erro.
    public void executar(ComandoNota comando) throws ServiceException {
        comando.executar();
        executados.push(comando);
        if (executados.size() > LIMITE) {
            executados.removeLast();
        }
    }

    // Desfaz o último comando. Se desfazer() falhar, ele continua no topo da pilha.
    public void desfazer() throws ServiceException {
        ComandoNota ultimo = executados.peek();
        if (ultimo == null) {
            throw new IllegalArgumentException("Não há nada para desfazer.");
        }
        ultimo.desfazer();
        executados.pop();
    }

    public boolean podeDesfazer() {
        return !executados.isEmpty();
    }

    // Descrição do próximo comando a desfazer, ou null se o histórico está vazio.
    public String proximaDescricao() {
        ComandoNota ultimo = executados.peek();
        return ultimo == null ? null : ultimo.descricao();
    }
}
