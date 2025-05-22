/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafxexpendio.modelo.pojo.Cliente;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLRegistrarVentaController implements Initializable {

    @FXML
    private TableView<?> tvBebidasVenta;
    @FXML
    private TableColumn colBebidas;
    @FXML
    private TableColumn colCantidad;
    @FXML
    private TableColumn colPrecioUnitario;
    @FXML
    private TableColumn colTotal;
    @FXML
    private ComboBox<Cliente> cbCliente;
    @FXML
    private Label lbTotalCompra;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnClicConfirmarCompra(ActionEvent event) {
    }

    @FXML
    private void btnClicAgregarBebida(ActionEvent event) {
    }

    @FXML
    private void btnClicEliminarBebida(ActionEvent event) {
    }
    
}
