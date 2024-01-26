package de.realwhimsy.afktraderpoe;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class FileDialogController implements Initializable {

    @FXML
    public TextField chooseFileTextField;

    @FXML
    private Button chooseFileButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chooseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());

            if (file != null) {
                chooseFileTextField.setText(file.getAbsolutePath());
            }
        });
    }
}
