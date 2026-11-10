package edu.unicentro.trabalho2pooii.controller;

import edu.unicentro.trabalho2pooii.main.Alertas;
import edu.unicentro.trabalho2pooii.main.GerenciadorTelas;
import edu.unicentro.trabalho2pooii.model.Usuario;
import edu.unicentro.trabalho2pooii.service.ServiceException;
import edu.unicentro.trabalho2pooii.service.UsuarioService;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastrarController {

    @FXML private Button btnCriarConta;
    @FXML private Button btnLogin;
    @FXML private TextField lblNome;
    @FXML private PasswordField pfSenha;
    @FXML private PasswordField pfConfirmarSenha;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    void btnCriarContaOnAction(ActionEvent event) {
        Usuario usuario = new Usuario(lblNome.getText(), pfSenha.getText());
        try {
            usuarioService.cadastrar(usuario, pfConfirmarSenha.getText());
        } catch (IllegalArgumentException e) {
            // Erro de preenchimento: nome vazio, senhas diferentes etc.
            Alertas.mostrar(Alert.AlertType.WARNING, e.getMessage());
            return;
        } catch (ServiceException e) {
            // Falha na operação: nome repetido, banco fora do ar etc.
            Alertas.mostrar(Alert.AlertType.ERROR, e.getMessage());
            return;
        }

        Alertas.mostrar(Alert.AlertType.INFORMATION, "Conta criada com sucesso! Faça login para continuar.");
        voltarParaLogin();
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        voltarParaLogin();
    }

    private void voltarParaLogin() {
        try {
            GerenciadorTelas.trocarTela(GerenciadorTelas.pasta_view + "login.fxml", "Login");
        } catch (IOException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, "Não foi possível abrir a tela de login.");
        }
    }

    @FXML
    void lblNomeOnAction(ActionEvent event) {
        pfSenha.requestFocus();
    }

    @FXML
    void pfSenhaOnAction(ActionEvent event) {
        pfConfirmarSenha.requestFocus();
    }

    @FXML
    void pfConfirmarSenhaOnAction(ActionEvent event) {
        btnCriarContaOnAction(event);
    }
}
