package edu.unicentro.trabalho2pooii.command;

import edu.unicentro.trabalho2pooii.model.Nota;
import edu.unicentro.trabalho2pooii.model.NotaBuilder;
import edu.unicentro.trabalho2pooii.service.NotaService;
import edu.unicentro.trabalho2pooii.service.ServiceException;

// Concrete Command: cria uma nota nova ou grava a edição de uma existente.
public class SalvarNotaCommand implements ComandoNota {

    private final NotaService service;
    private final int userID;
    private final Nota antes;   // null quando a nota é nova
    private final Nota depois;

    private SalvarNotaCommand(NotaService service, int userID, Nota antes, Nota depois) {
        this.service = service;
        this.userID = userID;
        this.antes = antes;
        this.depois = depois;
    }

    public static SalvarNotaCommand nova(NotaService service, int userID, Nota nova) {
        return new SalvarNotaCommand(service, userID, null, nova);
    }

    // Guarda uma cópia da nota como estava, para poder restaurá-la ao desfazer.
    public static SalvarNotaCommand edicao(NotaService service, int userID, Nota original, Nota editada) {
        return new SalvarNotaCommand(service, userID, NotaBuilder.de(original).build(), editada);
    }

    @Override
    public void executar() throws ServiceException {
        if (antes == null) {
            service.cadastrar(depois);          // o DAO preenche o notaID em "depois"
        } else {
            service.atualizar(depois, userID);
        }
    }

    @Override
    public void desfazer() throws ServiceException {
        if (antes == null) {
            service.excluir(depois.getNotaID(), userID);
        } else {
            service.atualizar(antes, userID);
        }
    }

    @Override
    public String descricao() {
        return (antes == null ? "criar a nota \"" : "editar a nota \"") + depois.getNome() + "\"";
    }
}
