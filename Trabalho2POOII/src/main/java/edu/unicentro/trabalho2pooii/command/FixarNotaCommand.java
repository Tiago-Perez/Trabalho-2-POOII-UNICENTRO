package edu.unicentro.trabalho2pooii.command;

import edu.unicentro.trabalho2pooii.service.NotaService;
import edu.unicentro.trabalho2pooii.service.ServiceException;

// Concrete Command: fixa ou desfixa a nota. Alternar duas vezes volta ao estado original,
// então desfazer() é o mesmo que alternar de novo.
public class FixarNotaCommand implements ComandoNota {

    private final NotaService service;
    private final int userID;
    private final int notaID;
    private final String titulo;
    private boolean fixadoApos;

    public FixarNotaCommand(NotaService service, int userID, int notaID, String titulo) {
        this.service = service;
        this.userID = userID;
        this.notaID = notaID;
        this.titulo = titulo;
    }

    @Override
    public void executar() throws ServiceException {
        fixadoApos = service.alternarFixado(notaID, userID);
    }

    @Override
    public void desfazer() throws ServiceException {
        service.alternarFixado(notaID, userID);
    }

    @Override
    public String descricao() {
        return (fixadoApos ? "fixar" : "desafixar") + " a nota \"" + titulo + "\"";
    }
}
