/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxexpendio.modelo.dao.ClienteDAOImpl;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLAdminClientesController implements Initializable {

    @FXML
    private TableView<Cliente> tvClientes;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colCorreo;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TextField tfBuscar;
    private ObservableList<Cliente> clientes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
    }    
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory("direccion"));
    }
    
    private void cargarInformacionTabla() {
        try {
            clientes = FXCollections.observableArrayList();
            ClienteDAOImpl clienteDAOImpl = new ClienteDAOImpl();
            ArrayList<Cliente> clientesDAO = (ArrayList<Cliente>) clienteDAOImpl.leerTodo();
            clientes.addAll(clientesDAO);
            tvClientes.setItems(clientes);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los clientes. "
                            + "Por favor intentelo más tarde.");
        }
    }

    @FXML
    private void btnClicRegistrarCliente(ActionEvent event) {
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
    }

    @FXML
    private void btnClicActualizar(ActionEvent event) {
    }
    
}
