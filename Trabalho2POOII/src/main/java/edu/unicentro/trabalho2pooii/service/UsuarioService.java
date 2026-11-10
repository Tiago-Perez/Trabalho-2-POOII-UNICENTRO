package edu.unicentro.trabalho2pooii.service;

import edu.unicentro.trabalho2pooii.dao.IUsuario;
import edu.unicentro.trabalho2pooii.dao.UsuarioDAO;
import edu.unicentro.trabalho2pooii.model.Usuario;
import java.sql.SQLException;

public class UsuarioService {

    private static final int TAMANHO_MAX_NOME = 255;

    private final IUsuario usuarioDAO = new UsuarioDAO();

    public void cadastrar(Usuario usuario, String confirmaSenha) throws ServiceException {
        validar(usuario);
        if (!usuario.getSenha().equals(confirmaSenha)) {
            throw new IllegalArgumentException("As senhas não coincidem!");
        }
        try {
            usuarioDAO.inserir(usuario);
        } catch (SQLException e) {
            throw traduzirFalha("cadastrar", e);
        }
    }

    // Devolve o usuário logado, ou lança ServiceException se nome/senha estiverem errados.
    public Usuario autenticar(String nome, String senha) throws ServiceException {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome!");
        }
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Informe a senha!");
        }
        try {
            Usuario usuario = usuarioDAO.buscarPorNome(nome.strip());
            if (usuario == null || !usuario.getSenha().equals(senha)) {
                throw new ServiceException("Nome ou senha incorretos.");
            }
            return usuario;
        } catch (SQLException e) {
            throw traduzirFalha("fazer login com", e);
        }
    }

    private void validar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("O usuário deve ser informado.");
        }
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome!");
        }
        if (usuario.getNome().strip().length() > TAMANHO_MAX_NOME) {
            throw new IllegalArgumentException("O nome pode ter no máximo " + TAMANHO_MAX_NOME + " caracteres!");
        }
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new IllegalArgumentException("Informe a senha!");
        }
        usuario.setNome(usuario.getNome().strip());
    }

    private ServiceException traduzirFalha(String operacao, SQLException causa) {
        // 23505 = violação de UNIQUE (nome repetido)
        if ("23505".equals(causa.getSQLState())) {
            return new ServiceException("Já existe um usuário com esse nome.", causa);
        }
        return new ServiceException("Não foi possível " + operacao + " o usuário.", causa);
    }
}
