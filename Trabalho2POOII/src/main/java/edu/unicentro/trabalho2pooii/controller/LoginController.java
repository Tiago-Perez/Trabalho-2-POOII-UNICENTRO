package edu.unicentro.trabalho2pooii.controller;

import edu.unicentro.trabalho2pooii.main.Alertas;
import edu.unicentro.trabalho2pooii.main.GerenciadorTelas;
import edu.unicentro.trabalho2pooii.main.Sessao;
import edu.unicentro.trabalho2pooii.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import edu.unicentro.trabalho2pooii.service.ServiceException;
import edu.unicentro.trabalho2pooii.service.UsuarioService;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button btnCriaConta;

    @FXML
    private Button btnEntrar;

    @FXML
    private TextField lblNome;

    @FXML
    private PasswordField pfSenha;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    void btnCriaContaOnAction(ActionEvent event) {
        try {
            GerenciadorTelas.trocarTela(GerenciadorTelas.pasta_view + "cadastrar.fxml", "Cadastro");
        } catch (IOException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, "Não foi possível abrir a tela de cadastro.");
        }
    }

    @FXML
    void btnEntrarOnAction(ActionEvent event) {
        try {
            Usuario usuario = usuarioService.autenticar(lblNome.getText(), pfSenha.getText());
            Sessao.setUsuarioLogado(usuario);
        } catch (IllegalArgumentException e) {
            Alertas.mostrar(Alert.AlertType.WARNING, e.getMessage());
            return;
        } catch (ServiceException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, e.getMessage());
            return;
        }
        fezLogin();
    }

    @FXML
    void lblNomeOnAction(ActionEvent event) {
        pfSenha.requestFocus();
    }

    @FXML
    void pfSenhaOnAction(ActionEvent event) {
        btnEntrarOnAction(event);
    }

    private void fezLogin(){
        try {
            GerenciadorTelas.trocarTela(GerenciadorTelas.pasta_view + "menu.fxml", "Notas");
        } catch (IOException e) {
            Alertas.mostrar(Alert.AlertType.ERROR, "Não foi possível fazer login.");
        }
    }

}
