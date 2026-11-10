package edu.unicentro.trabalho2pooii.controller;

import edu.unicentro.trabalho2pooii.main.Alertas;
import edu.unicentro.trabalho2pooii.main.GerenciadorTelas;
import edu.unicentro.trabalho2pooii.main.Sessao;
import edu.unicentro.trabalho2pooii.model.Nota;
import edu.unicentro.trabalho2pooii.model.Usuario;
import edu.unicentro.trabalho2pooii.service.NotaService;
import edu.unicentro.trabalho2pooii.service.ServiceException;
import edu.unicentro.trabalho2pooii.strategy.EstrategiaOrdenacao;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class MenuController {

    @FXML private Label lblBemVindo;
    @FXML private FlowPane flowNotas;
    @FXML private Menu menuOrdenar;
    @FXML private MenuItem miDesfazer;

    private final NotaService notaService = new NotaService();
    private Usuario usuario;

    @FXML
    private void initialize() {
        usuario = Sessao.getUsuarioLogado();
        if (usuario == null) {
            Alertas.mostrar(Alert.AlertType.WARNING, "Nenhum usuário logado.");
            return;
        }

        lblBemVindo.setText("Bem vindo, " + usuario.getNome());
        montarMenuOrdenar();
        carregarNotas();
    }

    // Padrão Strategy (lado da interface): um item de menu por estratégia disponível.
    // Escolher um item troca a estratégia da sessão e recarrega a lista.
    private void montarMenuOrdenar() {
        ToggleGroup grupo = new ToggleGroup();
        for (EstrategiaOrdenacao estrategia : EstrategiaOrdenacao.disponiveis()) {
            RadioMenuItem item = new RadioMenuItem(estrategia.nome());
            item.setToggleGroup(grupo);
            item.setSelected(estrategia.nome().equals(Sessao.getEstrategia().nome()));
            item.setOnAction(e -> {
                Sessao.setEstrategia(estrategia);
                carregarNotas();
            });
            menuOrdenar.getItems().add(item);
        }
    }

    private void carregarNotas() {
        flowNotas.getChildren().clear();
        try {
            for (Nota n : notaService.listar(usuario.getUserID(), Sessao.getEstrategia())) {
                flowNotas.getChildren().add(criarCartao(n));
            }
        } catch (ServiceException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, e.getMessage());
        }
        atualizarMenuDesfazer();
    }

    // O item "Desfazer" mostra qual ação seria desfeita e fica desabilitado sem histórico.
    private void atualizarMenuDesfazer() {
        String proxima = Sessao.getHistorico().proximaDescricao();
        miDesfazer.setDisable(proxima == null);
        miDesfazer.setText(proxima == null ? "Desfazer" : "Desfazer: " + proxima);
    }

    // Monta o cartão de preview de uma nota.
    private VBox criarCartao(Nota nota) {
        Label titulo = new Label((nota.isFixado() ? "Fixado: " : "") + nota.getNome());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        titulo.setWrapText(true);

        Label preview = new Label(resumir(nota.getTexto(), 120));
        preview.setWrapText(true);

        VBox cartao = new VBox(6, titulo, preview);
        cartao.setPrefWidth(190);
        cartao.setMaxHeight(160);
        cartao.setStyle("-fx-border-color: gray; -fx-border-radius: 8; "
                + "-fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");

        // Clicar no cartão abre a nota em tela cheia.
        cartao.setOnMouseClicked(e -> abrirNota(nota));
        return cartao;
    }

    // Corta o texto longo para caber no cartão.
    private String resumir(String texto, int max) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= max) {
            return texto;
        }
        return texto.substring(0, max).strip() + "...";
    }

    // nota == null significa "criar uma nota nova".
    private void abrirNota(Nota nota) {
        try {
            NotaController c = GerenciadorTelas.trocarTela(
                    GerenciadorTelas.pasta_view + "nota.fxml", "Nota");
            c.setNota(nota);
        } catch (IOException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, "Não foi possível abrir a nota.");
        }
    }

    @FXML
    private void btnNovaNotaOnAction() {
        abrirNota(null);
    }

    // Padrão Command: desfaz a última ação do histórico da sessão e recarrega a lista.
    @FXML
    private void btnDesfazerOnAction() {
        try {
            Sessao.getHistorico().desfazer();
        } catch (IllegalArgumentException e) {
            Alertas.mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (ServiceException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, e.getMessage());
        }
        carregarNotas();
    }

    @FXML
    private void btnSairOnAction() {
        Sessao.encerrar();
        try {
            GerenciadorTelas.trocarTela(GerenciadorTelas.pasta_view + "login.fxml", "Login");
        } catch (IOException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, "Não foi possível abrir a tela de login.");
        }
    }
}
