package edu.unicentro.trabalho2pooii.dao;

import edu.unicentro.trabalho2pooii.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO implements IUsuario {

    @Override
    public void inserir(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO users (nome, senha)
                VALUES (?, ?) RETURNING userid
                """;
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("O banco não retornou o ID do usuário cadastrado.");
                }
                usuario.setUserID(rs.getInt("userid"));
            }
        }
    }

    @Override
    public Usuario buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT userid, nome, senha FROM users WHERE nome = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("userid"),
                            rs.getString("nome"),
                            rs.getString("senha")
                    );
                }
                return null;
            }
        }
    }
}
