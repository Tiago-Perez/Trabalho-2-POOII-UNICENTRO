package edu.unicentro.trabalho2pooii.dao;

import edu.unicentro.trabalho2pooii.model.Usuario;
import java.sql.SQLException;

public interface IUsuario {

    void inserir(Usuario usuario) throws SQLException;

    // Devolve o usuário com esse nome, ou null se não existir.
    Usuario buscarPorNome(String nome) throws SQLException;
}
