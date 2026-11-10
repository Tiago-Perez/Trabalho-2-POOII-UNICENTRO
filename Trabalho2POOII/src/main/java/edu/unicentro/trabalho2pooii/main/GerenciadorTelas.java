package edu.unicentro.trabalho2pooii.main;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class GerenciadorTelas {

    public static final String pasta_view = "/edu/unicentro/trabalho2pooii/view/";
    private static Stage stage1;

    private GerenciadorTelas() {
    }

    public static void setStage(Stage stagePrincipal) {
        stage1 = stagePrincipal;
    }

    public static Stage getStage() {
        return stage1;
    }

    public static <T> T trocarTela(String arquivoFXML, String titulo) throws IOException {
        if (stage1 == null) {
            throw new IllegalStateException("O Stage principal ainda não foi definido.");
        }

        URL recurso = GerenciadorTelas.class.getResource(arquivoFXML);
        if (recurso == null) {
            throw new IOException("Arquivo FXML não encontrado: " + arquivoFXML);
        }

        FXMLLoader loader = new FXMLLoader(recurso);
        Parent raiz = loader.load();
        stage1.setScene(new Scene(raiz));
        stage1.setTitle(titulo);
        stage1.sizeToScene();
        stage1.show();
        return loader.getController();
    }
}
