module edu.unicentro.trabalho2pooii {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens edu.unicentro.trabalho2pooii.controller to javafx.fxml;
    exports edu.unicentro.trabalho2pooii.main;
}
