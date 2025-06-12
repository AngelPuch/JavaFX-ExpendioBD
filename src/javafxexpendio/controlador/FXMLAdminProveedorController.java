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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.ProveedorDAOImpl;
import javafxexpendio.modelo.pojo.Proveedor;
import javafxexpendio.utilidades.Utilidad;


public class FXMLAdminProveedorController implements Initializable, Notificacion {

    @FXML
    private TableView<Proveedor> tblProveedor;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colCorreo;
    @FXML
    private TextField tfBuscarProveedor;
    private ObservableList<Proveedor> proveedores;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
    }    

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        irFormularioProveedor(false, null);
    }


    @FXML
    private void btnClicEliminar(ActionEvent event) {
        Proveedor proveedorSeleccionado = getProveedorSeleccionado();

        if (proveedorSeleccionado != null) {
            boolean confirmado = Utilidad.mostrarAlertaConfirmacion("Confirmar eliminación",
                    "¿Estás seguro de eliminar al proveedor?",
            "Se eliminará al proveedor: " + proveedorSeleccionado.getRazonSocial() + " de la lista de proveedores");

            if (confirmado) {
                try {
                    ProveedorDAOImpl proveedorDAO = new ProveedorDAOImpl();
                    if (proveedorDAO.eliminar(proveedorSeleccionado.getIdProveedor())) {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Eliminación exitosa",
                            "El proveedor ha sido eliminado correctamente.");
                        operacionExitosa();

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

    @FXML
    private void btnClicEditar(ActionEvent event) {
        Proveedor proveedorSeleccionado = getProveedorSeleccionado();
        if (proveedorSeleccionado != null) {
            irFormularioProveedor(true, proveedorSeleccionado);
        }
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
            configurarFiltroBusqueda();
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los alumnos. Por favor intentelo más tarde");
            cerrarVentana();
        }

    }
    
    private void irFormularioProveedor(boolean isEdicion, Proveedor proveedorEdicion) {
        try {
            Stage escenarioAddProveedor = new Stage();
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLFormularioProveedor.fxml"));
            Parent vista = loader.load();
            FXMLFormularioProveedorController controlador = loader.getController();
            controlador.inicializarInformacion(isEdicion, proveedorEdicion, this);
            Scene escena = new Scene(vista);
            escenarioAddProveedor.setScene(escena);
            escenarioAddProveedor.setTitle("Formulario proveedor");
            escenarioAddProveedor.initModality(Modality.APPLICATION_MODAL);
            escenarioAddProveedor.showAndWait();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private Proveedor getProveedorSeleccionado() {
        Proveedor proveedorSeleccionado = tblProveedor.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado != null) {
            return proveedorSeleccionado;
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin selección",
                "Por favor, selecciona un proveedor para eliminar.");
            return null;
        }
    }
    
    private void configurarFiltroBusqueda() {
        Utilidad.activarFiltroBusqueda(tfBuscarProveedor, tblProveedor, proveedores, proveedor ->
            proveedor.getRazonSocial()+ " " + proveedor.getCorreo() + " " + proveedor.getTelefono()
        );
    }
    
    private void cerrarVentana() {
        ((Stage)tblProveedor.getScene().getWindow()).close();
    }

    @Override
    public void operacionExitosa() {
        cargarInformacionTabla();
    }
    
}
