package edu.unicentro.trabalho2pooii.service;

import edu.unicentro.trabalho2pooii.dao.INota;
import edu.unicentro.trabalho2pooii.dao.NotaDAO;
import edu.unicentro.trabalho2pooii.model.Nota;
import edu.unicentro.trabalho2pooii.strategy.EstrategiaOrdenacao;
import edu.unicentro.trabalho2pooii.strategy.FixadasPrimeiro;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * SERVICE LAYER - concentra as regras de negócio das notas:
 * validação, verificação de dono, alternância de "fixado" e ordenação (Strategy).
 * O Controller não conhece o DAO nem o SQL; só chama este serviço.
 */
public class NotaService {

    private static final int TAMANHO_MAX_TITULO = 255;

    private final INota notaDAO;
    private final EstrategiaOrdenacao ordenacaoPadrao = new FixadasPrimeiro();

    public NotaService() {
        this(new NotaDAO());
    }

    // O DAO entra pelo construtor: o serviço depende da interface INota, não de NotaDAO.
    public NotaService(INota notaDAO) {
        this.notaDAO = Objects.requireNonNull(notaDAO, "notaDAO");
    }

    public void cadastrar(Nota nota) throws ServiceException {
        validar(nota);
        try {
            notaDAO.inserir(nota);
        } catch (SQLException e) {
            throw traduzirFalha("cadastrar", e);
        }
    }

    // Recria uma nota excluída, com o mesmo ID (usado pelo desfazer de exclusão).
    public void restaurar(Nota nota) throws ServiceException {
        validar(nota);
        if (nota.getNotaID() <= 0) {
            throw new IllegalArgumentException("Nota inválida.");
        }
        try {
            notaDAO.restaurar(nota);
        } catch (SQLException e) {
            throw traduzirFalha("restaurar", e);
        }
    }

    // userID é o usuário que está pedindo a alteração; só o dono pode alterar a nota.
    public void atualizar(Nota nota, int userID) throws ServiceException {
        validar(nota);
        try {
            buscarDoUsuario(nota.getNotaID(), userID);
            notaDAO.atualizar(nota);
        } catch (SQLException e) {
            throw traduzirFalha("atualizar", e);
        }
    }

    public void excluir(int notaID, int userID) throws ServiceException {
        try {
            buscarDoUsuario(notaID, userID);
            notaDAO.excluir(notaID);
        } catch (SQLException e) {
            throw traduzirFalha("excluir", e);
        }
    }

    // Regra de negócio: inverte o estado atual de fixado. Devolve o novo estado.
    // (Antes, o NotaController decidia isso com !nota.isFixado().)
    public boolean alternarFixado(int notaID, int userID) throws ServiceException {
        try {
            Nota atual = buscarDoUsuario(notaID, userID);
            boolean novoEstado = !atual.isFixado();
            notaDAO.definirFixado(notaID, novoEstado);
            return novoEstado;
        } catch (SQLException e) {
            throw traduzirFalha("fixar", e);
        }
    }

    public List<Nota> listar(int userID) throws ServiceException {
        return listar(userID, ordenacaoPadrao);
    }

    // Strategy: o Service busca as notas e delega a ordem à estratégia recebida.
    public List<Nota> listar(int userID, EstrategiaOrdenacao estrategia) throws ServiceException {
        EstrategiaOrdenacao usada = estrategia != null ? estrategia : ordenacaoPadrao;
        try {
            return usada.ordenar(notaDAO.listarPorUsuario(userID));
        } catch (SQLException e) {
            throw traduzirFalha("listar", e);
        }
    }

    // Devolve a nota só se ela existir e pertencer ao usuário.
    private Nota buscarDoUsuario(int notaID, int userID) throws SQLException {
        if (notaID <= 0) {
            throw new IllegalArgumentException("Nota inválida.");
        }
        Nota nota = notaDAO.buscarPorId(notaID);
        if (nota == null) {
            throw new IllegalArgumentException("Nota não encontrada.");
        }
        if (nota.getUserID() != userID) {
            throw new IllegalArgumentException("Esta nota não pertence ao usuário logado.");
        }
        return nota;
    }

    private void validar(Nota nota) {
        if (nota == null) {
            throw new IllegalArgumentException("A nota deve ser informada.");
        }
        if (nota.getUserID() <= 0) {
            throw new IllegalArgumentException("A nota precisa pertencer a um usuário.");
        }
        if (nota.getNome() == null || nota.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o título da nota!");
        }
        if (nota.getNome().strip().length() > TAMANHO_MAX_TITULO) {
            throw new IllegalArgumentException("O título pode ter no máximo " + TAMANHO_MAX_TITULO + " caracteres!");
        }
        nota.setNome(nota.getNome().strip());
    }

    private ServiceException traduzirFalha(String operacao, SQLException causa) {
        // 23503 = violação de chave estrangeira (usuário não existe)
        if ("23503".equals(causa.getSQLState())) {
            return new ServiceException("O usuário da nota não existe.", causa);
        }
        return new ServiceException("Não foi possível " + operacao + " a nota.", causa);
    }
}
