package edu.unicentro.trabalho2pooii.controller;

import edu.unicentro.trabalho2pooii.command.ComandoNota;
import edu.unicentro.trabalho2pooii.command.ExcluirNotaCommand;
import edu.unicentro.trabalho2pooii.command.FixarNotaCommand;
import edu.unicentro.trabalho2pooii.command.SalvarNotaCommand;
import edu.unicentro.trabalho2pooii.main.Alertas;
import edu.unicentro.trabalho2pooii.main.GerenciadorTelas;
import edu.unicentro.trabalho2pooii.main.Sessao;
import edu.unicentro.trabalho2pooii.model.Nota;
import edu.unicentro.trabalho2pooii.model.NotaBuilder;
import edu.unicentro.trabalho2pooii.service.NotaService;
import edu.unicentro.trabalho2pooii.service.ServiceException;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NotaController {

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtTexto;
    @FXML private MenuItem miExcluir;
    @FXML private MenuItem miFixar;

    private final NotaService notaService = new NotaService();

    // null = nota nova
    private Nota nota;

    // Chamado pelo MenuController logo depois de abrir a tela.
    public void setNota(Nota nota) {
        this.nota = nota;
        if (nota == null) {
            miExcluir.setVisible(false);
            miFixar.setVisible(false);
        } else {
            txtTitulo.setText(nota.getNome());
            txtTexto.setText(nota.getTexto() == null ? "" : nota.getTexto());
            miFixar.setText(nota.isFixado() ? "Desfixar" : "Fixar");
        }
    }

    // Padrão Command (Client): cada ação abaixo monta um comando e o entrega ao histórico
    // da sessão (Invoker). O tratamento de erro, que se repetia em salvar/excluir/fixar,
    // ficou só aqui. Devolve true se o comando foi executado com sucesso.
    private boolean executar(ComandoNota comando) {
        try {
            Sessao.getHistorico().executar(comando);
            return true;
        } catch (IllegalArgumentException e) {
            Alertas.mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (ServiceException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, e.getMessage());
        }
        return false;
    }

    private int userIDLogado() {
        return Sessao.getUsuarioLogado().getUserID();
    }

    @FXML
    private void btnSalvarOnAction() {
        ComandoNota comando;
        if (nota == null) {
            // Padrão Builder: monta a nota nova nomeando cada campo.
            Nota nova = new NotaBuilder()
                    .userID(userIDLogado())
                    .nome(txtTitulo.getText())
                    .texto(txtTexto.getText())
                    .build();
            comando = SalvarNotaCommand.nova(notaService, userIDLogado(), nova);
        } else {
            // Padrão Builder: parte da nota existente e troca só título e texto.
            Nota editada = NotaBuilder.de(nota)
                    .nome(txtTitulo.getText())
                    .texto(txtTexto.getText())
                    .build();
            comando = SalvarNotaCommand.edicao(notaService, userIDLogado(), nota, editada);
        }
        if (executar(comando)) {
            voltarParaMenu();
        }
    }

    @FXML
    private void btnExcluirOnAction() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir esta nota?", ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);
        confirmacao.initOwner(GerenciadorTelas.getStage());
        if (confirmacao.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }
        if (executar(new ExcluirNotaCommand(notaService, userIDLogado(), nota))) {
            voltarParaMenu();
        }
    }

    @FXML
    private void btnVoltarOnAction() {
        voltarParaMenu();
    }

    private void voltarParaMenu() {
        try {
            GerenciadorTelas.trocarTela(GerenciadorTelas.pasta_view + "menu.fxml", "Notas");
        } catch (IOException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, "Não foi possível abrir o menu.");
        }
    }

    @FXML
    void miFixarOnAction() {
        if (nota == null) {
            Alertas.mostrar(Alert.AlertType.WARNING, "Salve a nota antes de fixá-la.");
            return;
        }
        // Quem decide fixar ou desfixar agora é o NotaService (alternarFixado).
        if (executar(new FixarNotaCommand(notaService, userIDLogado(), nota.getNotaID(), nota.getNome()))) {
            voltarParaMenu();
        }
    }
}
