/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.dao.ProveedorDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Proveedor;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLAdminProveedorController implements Initializable {

    @FXML
    private TableView<Proveedor> tblProveedor;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colCorreo;
    private ObservableList<Proveedor> proveedores;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
    }    
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory("razonSocial"));
        colTelefono.setCellValueFactory(new PropertyValueFactory("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
    }
    
    private void cargarInformacionTabla() {
        try {
            ProveedorDAOImpl proveedorDAOImpl = new ProveedorDAOImpl();
            proveedores = FXCollections.observableArrayList();
            List<Proveedor> proveedoresDAO = proveedorDAOImpl.leerTodo();
            proveedores.addAll(proveedoresDAO);
            tblProveedor.setItems(proveedores);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los alumnos. Por favor intentelo más tarde");
            cerrarVentana();
        }

    }
    
    private void cerrarVentana() {
        ((Stage)tblProveedor.getScene().getWindow()).close();
    }

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        try{
            Stage escenarioAddProducto = new Stage();
            Parent vista = FXMLLoader.load(JavaFXAppExpendio.class.getResource("vista/FXMLRegistrarProveedor.fxml"));
            Scene escena = new Scene(vista);
            escenarioAddProducto.setScene(escena);
            escenarioAddProducto.setTitle("Agregar proveedor");
            escenarioAddProducto.initModality(Modality.APPLICATION_MODAL);
            escenarioAddProducto.showAndWait();
            cargarInformacionTabla();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnClicActualizar(ActionEvent event) {
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
        Proveedor proveedorSeleccionado = tblProveedor.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin selección",
                "Por favor, selecciona un proveedor para eliminar.");
            return;
        }

        boolean confirmado = Utilidad.mostrarAlertaConfirmacion(
            "Confirmar eliminación",
            "¿Seguro que desea eliminar al proveedor: " + proveedorSeleccionado.getRazonSocial() + "?"
        );

        if (confirmado) {
            try {
                ProveedorDAOImpl proveedorDAO = new ProveedorDAOImpl();
                if (proveedorDAO.eliminar(proveedorSeleccionado.getIdProveedor())) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Eliminación exitosa",
                        "El proveedor ha sido eliminado correctamente.");
                    cargarInformacionTabla();
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error",
                        "No se pudo eliminar el proveedor. Intenta de nuevo.");
                }
            } catch (SQLException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
            }
        }
    }
    
}
