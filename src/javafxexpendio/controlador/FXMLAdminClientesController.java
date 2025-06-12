package javafxexpendio.controlador;

import java.io.IOException;
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
import javafxexpendio.modelo.dao.ClienteDAOImpl;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.utilidades.Utilidad;

public class FXMLAdminClientesController implements Initializable, Notificacion {

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
            configurarFiltroBusqueda();
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los clientes. "
                            + "Por favor intentelo más tarde.");
            cerrarVentana();
        }
    }

    @FXML
    private void btnClicRegistrarCliente(ActionEvent event) {
        irFormularioCliente(false, null);
    }
    
    @FXML
    private void btnClicEditar(ActionEvent event) {
        Cliente clienteSeleccionado = getClienteSeleccionado("Por favor, selecciona un cliente para editar.");
        if (clienteSeleccionado != null) {
            irFormularioCliente(true, clienteSeleccionado);
        }
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
        Cliente clienteSeleccionado = getClienteSeleccionado("Por favor, selecciona un cliente para eliminar.");
        if (clienteSeleccionado != null) {
            boolean confirmado = Utilidad.mostrarAlertaConfirmacion( "Confirmar eliminación",
                    "¿Estás seguro de eliminar al cliente?",
            "Se eliminara al cliente " + clienteSeleccionado.getNombre()+ " de la lista de clientes");
            if (confirmado) {
                try {
                    ClienteDAOImpl clienteDAOImpl = new ClienteDAOImpl();
                    if (clienteDAOImpl.eliminar(clienteSeleccionado.getIdCliente())) {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Eliminación exitosa",
                                "El cliente ha sido eliminado correctamente.");
                        operacionExitosa();
                    } else {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error",
                                "No se pudo eliminar el cliente. Intenta de nuevo.");
                    }
                } catch (SQLException ex) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
                }
            }
        }
    }

    
    private void irFormularioCliente(boolean isEdicion, Cliente clienteEdicion){
        try {
            Stage escenarioFormulario = new Stage();
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource(
                    "vista/FXMLFormularioCliente.fxml"));
            Parent vista = loader.load();
            FXMLFormularioClienteController controlador = loader.getController();
            controlador.inicializarInformacion(isEdicion, clienteEdicion, this);
            Scene escena = new Scene(vista);
            escenarioFormulario.setScene(escena);
            escenarioFormulario.setTitle("Formulario cliente");
            escenarioFormulario.initModality(Modality.APPLICATION_MODAL);
            escenarioFormulario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void configurarFiltroBusqueda() {
        Utilidad.activarFiltroBusqueda(tfBuscar, tvClientes, clientes, cliente ->
            cliente.getNombre()+ " " + cliente.getCorreo()+ " " + cliente.getTelefono()+ " " + cliente.getDireccion()
        );
    }
    
    private void cerrarVentana() {
        ((Stage)tvClientes.getScene().getWindow()).close();
    }

    @Override
    public void operacionExitosa() {
        cargarInformacionTabla();
    }

    private Cliente getClienteSeleccionado(String contenido) {
        Cliente clienteSeleccionado = tvClientes.getSelectionModel().getSelectedItem();
    
        if (clienteSeleccionado != null) {
            return clienteSeleccionado;
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin selección",
                contenido);
            return null;
        }
    }
    
}
