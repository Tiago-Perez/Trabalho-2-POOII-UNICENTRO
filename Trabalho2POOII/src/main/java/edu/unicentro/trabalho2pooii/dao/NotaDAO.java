package edu.unicentro.trabalho2pooii.dao;

import edu.unicentro.trabalho2pooii.model.Nota;
import edu.unicentro.trabalho2pooii.model.NotaBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotaDAO implements INota {

    @Override
    public void inserir(Nota nota) throws SQLException {
        String sql = """
                INSERT INTO notas (userid, nome, texto, fixado)
                VALUES (?, ?, ?, FALSE) RETURNING notaid
                """;
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, nota.getUserID());
            stmt.setString(2, nota.getNome());
            stmt.setString(3, nota.getTexto());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("O banco não retornou o ID da nota cadastrada.");
                }
                nota.setNotaID(rs.getInt("notaid"));
            }
        }
    }

    @Override
    public void restaurar(Nota nota) throws SQLException {
        String sql = "INSERT INTO notas (notaid, userid, nome, texto, fixado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, nota.getNotaID());
            stmt.setInt(2, nota.getUserID());
            stmt.setString(3, nota.getNome());
            stmt.setString(4, nota.getTexto());
            stmt.setBoolean(5, nota.isFixado());
            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizar(Nota nota) throws SQLException {
        String sql = "UPDATE notas SET nome = ?, texto = ? WHERE notaid = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nota.getNome());
            stmt.setString(2, nota.getTexto());
            stmt.setInt(3, nota.getNotaID());
            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(int notaID) throws SQLException {
        String sql = "DELETE FROM notas WHERE notaid = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, notaID);
            stmt.executeUpdate();
        }
    }

    @Override
    public void definirFixado(int notaID, boolean fixado) throws SQLException {
        String sql = "UPDATE notas SET fixado = ? WHERE notaid = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setBoolean(1, fixado);
            stmt.setInt(2, notaID);
            stmt.executeUpdate();
        }
    }

    @Override
    public Nota buscarPorId(int notaID) throws SQLException {
        String sql = "SELECT notaid, userid, nome, texto, fixado FROM notas WHERE notaid = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, notaID);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Nota> listarPorUsuario(int userID) throws SQLException {
        List<Nota> lista = new ArrayList<>();
        // Sem ORDER BY de exibição: quem decide a ordem é a EstrategiaOrdenacao (Service).
        String sql = "SELECT notaid, userid, nome, texto, fixado FROM notas "
                + "WHERE userid = ? ORDER BY notaid";
        try (Connection con = Conexao.getConexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // Padrão Builder: monta a Nota a partir da linha do banco, nomeando cada campo.
    private Nota mapear(ResultSet rs) throws SQLException {
        return new NotaBuilder()
                .notaID(rs.getInt("notaid"))
                .userID(rs.getInt("userid"))
                .nome(rs.getString("nome"))
                .texto(rs.getString("texto"))
                .fixado(rs.getBoolean("fixado"))
                .build();
    }
}
