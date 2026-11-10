package edu.unicentro.trabalho2pooii.main;

import javafx.scene.control.Alert;

public final class Alertas {

    private Alertas() {
    }

    public static void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Notas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.initOwner(GerenciadorTelas.getStage());
        alerta.showAndWait();
    }
}
