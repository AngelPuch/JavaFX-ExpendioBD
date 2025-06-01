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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.UsuarioDAOImpl;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLAdminUsuarioController implements Initializable, Notificacion {

    @FXML
    private TableView<Usuario> tblUsuario;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colApePaterno;
    @FXML
    private TableColumn colApeMaterno;
    @FXML
    private TableColumn colUsername;
    @FXML
    private TableColumn colTipoUsuario;
    @FXML
    private TextField tfBuscarUsuario;
    private ObservableList<Usuario> usuarios;

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
        irFormularioUsuario(false, null);
    }

    @FXML
    private void btnClicEditar(ActionEvent event) {
        Usuario usuarioSeleccionado = getUsuarioSeleccionado();
        if (usuarioSeleccionado != null) {
            irFormularioUsuario(true, usuarioSeleccionado);
        }
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
        Usuario usuarioSeleccionado = getUsuarioSeleccionado();
        
        if (usuarioSeleccionado != null) {
            boolean confirmado = Utilidad.mostrarAlertaConfirmacion("Confirmar eliminación",
                    "¿Estás seguro de eliminar al usuario?",
                    "Se eliminará al usuario: " + usuarioSeleccionado.getNombre()+ " de la lista de usuarios"); 
            if (confirmado) {
                try {
                    UsuarioDAOImpl usuarioDAOImpl = new UsuarioDAOImpl();
                    if (usuarioDAOImpl.eliminar(usuarioSeleccionado.getIdUsuario())) {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Eliminación exitosa",
                            "El usuario ha sido eliminado correctamente.");
                        operacionExitosa();
                    } else {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error",
                            "No se pudo eliminar el usuario. Intenta de nuevo.");
                    }
                } catch (Exception ex) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
                }
            }
        }
        
        
    }

    @Override
    public void operacionExitosa() {
        cargarInformacionTabla();
    }
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApePaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colApeMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colUsername.setCellValueFactory(new PropertyValueFactory("username"));
        colTipoUsuario.setCellValueFactory(new PropertyValueFactory("tipoUsuario"));
    }
    
    private void cargarInformacionTabla() {
        try {
            UsuarioDAOImpl usuarioDAOImpl = new UsuarioDAOImpl();
            usuarios = FXCollections.observableArrayList();
            List<Usuario> usuariosDAO = usuarioDAOImpl.leerTodo();
            usuarios.addAll(usuariosDAO);
            tblUsuario.setItems(usuarios);
            configurarFiltroBusqueda();
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar",
                    "Lo sentimos, por el momento no se puede mostrar la información de los usuarios. Por favor, inténtelo más tarde");
            cerrarVentana();
        }
    }
    
    private void irFormularioUsuario(boolean isEdicion, Usuario usuarioEdicion) {
        try {
            Stage escenarioAddProveedor = new Stage();
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLFormularioUsuario.fxml"));
            Parent vista = loader.load();
            FXMLFormularioUsuarioController controlador = loader.getController();
            controlador.inicializarInformacion(isEdicion, usuarioEdicion, this);
            Scene escena = new Scene(vista);
            escenarioAddProveedor.setScene(escena);
            escenarioAddProveedor.setTitle("Formulario proveedor");
            escenarioAddProveedor.initModality(Modality.APPLICATION_MODAL);
            escenarioAddProveedor.showAndWait();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private Usuario getUsuarioSeleccionado() {
        Usuario usuarioSeleccionado = tblUsuario.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            return usuarioSeleccionado;
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin selección",
                "Por favor, selecciona un usuario para eliminar.");
            return null;
        }
    }  
    
    private void configurarFiltroBusqueda() {
        Utilidad.activarFiltroBusqueda(tfBuscarUsuario, tblUsuario, usuarios, usuario ->
            usuario.getNombre()+ " " + usuario.getApellidoMaterno() + " " + usuario.getApellidoMaterno() + " " +
                    usuario.getUsername() + " " + usuario.getTipoUsuario());
    }
    
    private void cerrarVentana() {
        ((Stage)tblUsuario.getScene().getWindow()).close();
    }
    
    
}
