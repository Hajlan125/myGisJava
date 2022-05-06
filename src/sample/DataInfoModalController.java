package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.SQLException;


public class DataInfoModalController {
    @FXML Button saveBtn;
    @FXML TextField nameField, addressField;
    Connection connection;
    DbConnector connector;
    Object object;
    Controller controller;

    public void initialize() throws SQLException, ClassNotFoundException {
        connector = new DbConnector();
        connection = connector.getConnection();
    }

    public void initData(Object object) {
        this.object = object;
        if (object.getId() != 0) {
            nameField.setText(object.getName());addressField.setText(object.getAddress());
        }
    }

    @FXML private void saveData() throws SQLException {
        object.setName(nameField.getText());
        object.setAddress(addressField.getText());
        connector.setObject(object);
        saveBtn.getScene().getWindow().hide();
    }

    @FXML private void close() {

    }
}
