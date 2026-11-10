package edu.unicentro.trabalho2pooii.command;

import edu.unicentro.trabalho2pooii.service.ServiceException;

/**
 * PADRÃO COMMAND (GoF) - papel: Command (interface).
 * Cada ação do usuário sobre uma nota vira um objeto com executar() e desfazer().
 * Concrete Commands: SalvarNotaCommand, ExcluirNotaCommand, FixarNotaCommand.
 * Receiver: NotaService. Invoker: HistoricoComandos. Client: NotaController.
 */
public interface ComandoNota {

    void executar() throws ServiceException;

    void desfazer() throws ServiceException;

    // Texto curto da ação, mostrado no item de menu "Desfazer: ...".
    String descricao();
}
