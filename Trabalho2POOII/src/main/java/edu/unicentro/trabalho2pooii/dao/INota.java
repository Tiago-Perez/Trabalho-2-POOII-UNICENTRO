package edu.unicentro.trabalho2pooii.dao;

import edu.unicentro.trabalho2pooii.model.Nota;
import java.sql.SQLException;
import java.util.List;

public interface INota {

    void inserir(Nota nota) throws SQLException;

    // Reinsere uma nota que já teve ID, mantendo o ID original.
    // Usado pelo "desfazer" de uma exclusão (padrão Command).
    void restaurar(Nota nota) throws SQLException;

    void atualizar(Nota nota) throws SQLException;

    void excluir(int notaID) throws SQLException;

    void definirFixado(int notaID, boolean fixado) throws SQLException;

    // Devolve a nota com esse ID, ou null se não existir.
    // Permite ao Service conferir se a nota pertence ao usuário antes de alterá-la.
    Nota buscarPorId(int notaID) throws SQLException;

    // Lista só as notas do usuário informado, em ordem de criação (notaid).
    // A ordem de exibição é responsabilidade da camada de serviço (padrão Strategy).
    List<Nota> listarPorUsuario(int userID) throws SQLException;
}
