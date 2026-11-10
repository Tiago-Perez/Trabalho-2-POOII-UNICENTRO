package edu.unicentro.trabalho2pooii.service;

import edu.unicentro.trabalho2pooii.command.ExcluirNotaCommand;
import edu.unicentro.trabalho2pooii.command.FixarNotaCommand;
import edu.unicentro.trabalho2pooii.command.HistoricoComandos;
import edu.unicentro.trabalho2pooii.command.SalvarNotaCommand;
import edu.unicentro.trabalho2pooii.dao.INota;
import edu.unicentro.trabalho2pooii.model.Nota;
import edu.unicentro.trabalho2pooii.model.NotaBuilder;
import edu.unicentro.trabalho2pooii.strategy.FixadasPrimeiro;
import edu.unicentro.trabalho2pooii.strategy.MaisRecentes;
import edu.unicentro.trabalho2pooii.strategy.PorTitulo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Verificação dos padrões sem precisar de banco nem de JavaFX:
 * o NotaService recebe um DAO em memória (possível por causa da injeção via INota).
 * Execute a main; qualquer falha lança AssertionError.
 */
public class VerificacaoPadroes {

    // DAO em memória que imita o banco: guarda e devolve cópias.
    static class DaoMemoria implements INota {
        final Map<Integer, Nota> tabela = new TreeMap<>();
        int proximoId = 1;

        public void inserir(Nota n) {
            n.setNotaID(proximoId++);
            n.setFixado(false);
            tabela.put(n.getNotaID(), NotaBuilder.de(n).build());
        }
        public void restaurar(Nota n) throws SQLException {
            if (tabela.containsKey(n.getNotaID())) throw new SQLException("duplicada", "23505");
            tabela.put(n.getNotaID(), NotaBuilder.de(n).build());
        }
        public void atualizar(Nota n) {
            Nota g = tabela.get(n.getNotaID());
            g.setNome(n.getNome());
            g.setTexto(n.getTexto());
        }
        public void excluir(int id) { tabela.remove(id); }
        public void definirFixado(int id, boolean f) { tabela.get(id).setFixado(f); }
        public Nota buscarPorId(int id) {
            Nota g = tabela.get(id);
            return g == null ? null : NotaBuilder.de(g).build();
        }
        public List<Nota> listarPorUsuario(int userID) {
            List<Nota> r = new ArrayList<>();
            for (Nota n : tabela.values()) {
                if (n.getUserID() == userID) r.add(NotaBuilder.de(n).build());
            }
            return r;
        }
    }

    static int checks = 0;

    static void confere(boolean condicao, String descricao) {
        if (!condicao) throw new AssertionError("FALHOU: " + descricao);
        checks++;
        System.out.println("  ok  " + descricao);
    }

    static String titulos(List<Nota> notas) {
        StringBuilder sb = new StringBuilder();
        for (Nota n : notas) sb.append(n.getNome()).append(n.isFixado() ? "*" : "").append(' ');
        return sb.toString().trim();
    }

    static Nota nova(int user, String nome, String texto) {
        return new NotaBuilder().userID(user).nome(nome).texto(texto).build();
    }

    public static void main(String[] args) throws Exception {
        final int ANA = 1, BIA = 2;
        DaoMemoria dao = new DaoMemoria();
        NotaService service = new NotaService(dao);
        HistoricoComandos historico = new HistoricoComandos();

        System.out.println("[Builder]");
        Nota b = new NotaBuilder().userID(ANA).nome("x").build();
        confere("".equals(b.getTexto()) && !b.isFixado(), "texto vazio e nao fixado por padrao");
        Nota copia = NotaBuilder.de(b).nome("y").build();
        confere("x".equals(b.getNome()) && "y".equals(copia.getNome()), "de(...) gera copia independente");

        System.out.println("[Command: criar, editar, fixar, excluir e desfazer em cadeia]");
        Nota notaC = nova(ANA, "Charlie", "c");
        Nota notaA = nova(ANA, "alfa", "a");
        Nota notaB = nova(ANA, "Bravo", "b");
        historico.executar(SalvarNotaCommand.nova(service, ANA, notaC));
        historico.executar(SalvarNotaCommand.nova(service, ANA, notaA));
        historico.executar(SalvarNotaCommand.nova(service, ANA, notaB));
        confere(dao.tabela.size() == 3, "tres notas criadas pelo comando");
        confere(historico.proximaDescricao().contains("Bravo"), "descricao do topo: " + historico.proximaDescricao());

        Nota editada = NotaBuilder.de(notaA).nome("  Alfa 2  ").texto("novo").build();
        historico.executar(SalvarNotaCommand.edicao(service, ANA, notaA, editada));
        confere("Alfa 2".equals(dao.tabela.get(notaA.getNotaID()).getNome()), "edicao gravou e o Service aplicou strip()");

        historico.executar(new FixarNotaCommand(service, ANA, notaC.getNotaID(), "Charlie"));
        confere(dao.tabela.get(notaC.getNotaID()).isFixado(), "FixarNotaCommand fixou a nota");

        Nota doBanco = dao.buscarPorId(notaB.getNotaID());
        historico.executar(new ExcluirNotaCommand(service, ANA, doBanco));
        confere(!dao.tabela.containsKey(notaB.getNotaID()), "ExcluirNotaCommand removeu a nota");

        historico.desfazer();
        confere(dao.tabela.containsKey(notaB.getNotaID()), "desfazer exclusao recriou a nota com o MESMO ID");
        historico.desfazer();
        confere(!dao.tabela.get(notaC.getNotaID()).isFixado(), "desfazer fixar desfixou");
        historico.desfazer();
        Nota aposDesfazerEdicao = dao.tabela.get(notaA.getNotaID());
        confere("alfa".equals(aposDesfazerEdicao.getNome()) && "a".equals(aposDesfazerEdicao.getTexto()),
                "desfazer edicao restaurou titulo e texto originais");
        historico.desfazer();
        historico.desfazer();
        historico.desfazer();
        confere(dao.tabela.isEmpty(), "desfazer as tres criacoes esvaziou o banco");
        confere(!historico.podeDesfazer() && historico.proximaDescricao() == null, "historico vazio");
        try {
            historico.desfazer();
            confere(false, "desfazer sem historico deveria falhar");
        } catch (IllegalArgumentException e) {
            confere(true, "desfazer sem historico lanca IllegalArgumentException");
        }

        System.out.println("[Command: comando que falha nao entra no historico]");
        try {
            historico.executar(SalvarNotaCommand.nova(service, ANA, nova(ANA, "   ", "sem titulo")));
            confere(false, "titulo em branco deveria falhar");
        } catch (IllegalArgumentException e) {
            confere(!historico.podeDesfazer(), "falha de validacao nao empilhou comando");
        }

        System.out.println("[Strategy]");
        Nota n1 = nova(ANA, "Charlie", ""), n2 = nova(ANA, "alfa", ""), n3 = nova(ANA, "Bravo", "");
        service.cadastrar(n1);
        service.cadastrar(n2);
        service.cadastrar(n3);
        service.alternarFixado(n3.getNotaID(), ANA);
        String fixadas = titulos(service.listar(ANA, new FixadasPrimeiro()));
        String titulo = titulos(service.listar(ANA, new PorTitulo()));
        String recentes = titulos(service.listar(ANA, new MaisRecentes()));
        confere("Bravo* Charlie alfa".equals(fixadas), "FixadasPrimeiro: " + fixadas);
        confere("alfa Bravo* Charlie".equals(titulo), "PorTitulo: " + titulo);
        confere("Bravo* alfa Charlie".equals(recentes), "MaisRecentes: " + recentes);
        confere(titulos(service.listar(ANA)).equals(fixadas), "listar(userID) usa a ordem original por padrao");
        confere(titulos(service.listar(ANA, null)).equals(fixadas), "estrategia null cai no padrao");

        System.out.println("[Service Layer: regras de negocio]");
        try {
            service.excluir(n1.getNotaID(), BIA);
            confere(false, "outro usuario nao pode excluir");
        } catch (IllegalArgumentException e) {
            confere(dao.tabela.containsKey(n1.getNotaID()), "excluir por nao-dono recusado: " + e.getMessage());
        }
        try {
            service.alternarFixado(n1.getNotaID(), BIA);
            confere(false, "outro usuario nao pode fixar");
        } catch (IllegalArgumentException e) {
            confere(!dao.tabela.get(n1.getNotaID()).isFixado(), "fixar por nao-dono recusado");
        }
        try {
            service.atualizar(NotaBuilder.de(n1).nome("hack").build(), BIA);
            confere(false, "outro usuario nao pode editar");
        } catch (IllegalArgumentException e) {
            confere("Charlie".equals(dao.tabela.get(n1.getNotaID()).getNome()), "editar por nao-dono recusado");
        }
        try {
            service.excluir(999, ANA);
            confere(false, "nota inexistente");
        } catch (IllegalArgumentException e) {
            confere(true, "nota inexistente: " + e.getMessage());
        }
        confere(service.alternarFixado(n3.getNotaID(), ANA) == false, "alternarFixado devolve o novo estado (desfixou)");
        try {
            service.restaurar(dao.buscarPorId(n1.getNotaID()));
            confere(false, "restaurar nota que ja existe");
        } catch (ServiceException e) {
            confere(true, "restaurar duplicada vira ServiceException: " + e.getMessage());
        }

        System.out.println("\n" + checks + " verificacoes passaram.");
    }
}
