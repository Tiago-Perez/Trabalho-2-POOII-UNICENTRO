package edu.unicentro.trabalho2pooii.main;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage1) throws IOException {
        GerenciadorTelas.setStage(stage1);
        stage1.setResizable(false);

        GerenciadorTelas.trocarTela(GerenciadorTelas.pasta_view + "login.fxml", "Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
