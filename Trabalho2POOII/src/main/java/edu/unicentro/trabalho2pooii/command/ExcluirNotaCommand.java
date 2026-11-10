package edu.unicentro.trabalho2pooii.command;

import edu.unicentro.trabalho2pooii.model.Nota;
import edu.unicentro.trabalho2pooii.model.NotaBuilder;
import edu.unicentro.trabalho2pooii.service.NotaService;
import edu.unicentro.trabalho2pooii.service.ServiceException;

// Concrete Command: exclui a nota; desfazer recria a nota com o mesmo ID, título, texto e fixado.
public class ExcluirNotaCommand implements ComandoNota {

    private final NotaService service;
    private final int userID;
    private final Nota copia;   // retrato da nota antes de ser excluída

    public ExcluirNotaCommand(NotaService service, int userID, Nota nota) {
        this.service = service;
        this.userID = userID;
        this.copia = NotaBuilder.de(nota).build();
    }

    @Override
    public void executar() throws ServiceException {
        service.excluir(copia.getNotaID(), userID);
    }

    @Override
    public void desfazer() throws ServiceException {
        service.restaurar(copia);
    }

    @Override
    public String descricao() {
        return "excluir a nota \"" + copia.getNome() + "\"";
    }
}
