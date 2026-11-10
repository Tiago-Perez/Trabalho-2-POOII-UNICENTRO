package edu.unicentro.trabalho2pooii.main;

import edu.unicentro.trabalho2pooii.command.HistoricoComandos;
import edu.unicentro.trabalho2pooii.model.Usuario;
import edu.unicentro.trabalho2pooii.strategy.EstrategiaOrdenacao;
import edu.unicentro.trabalho2pooii.strategy.FixadasPrimeiro;

public final class Sessao {

    private static Usuario usuarioLogado;

    // Estado da sessão que precisa sobreviver à troca de telas:
    // histórico do "desfazer" (Command) e ordenação escolhida (Strategy).
    private static HistoricoComandos historico = new HistoricoComandos();
    private static EstrategiaOrdenacao estrategia = new FixadasPrimeiro();

    private Sessao() {
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static HistoricoComandos getHistorico() {
        return historico;
    }

    public static EstrategiaOrdenacao getEstrategia() {
        return estrategia;
    }

    public static void setEstrategia(EstrategiaOrdenacao nova) {
        estrategia = nova;
    }

    public static void encerrar() {
        usuarioLogado = null;
        historico = new HistoricoComandos();
        estrategia = new FixadasPrimeiro();
    }
}
