/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.UsuarioDAOImpl;
import javafxexpendio.modelo.pojo.TipoUsuario;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLFormularioUsuarioController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private ComboBox<TipoUsuario> cbTipoUsuario;
    @FXML
    private Label lbNombreError;
    @FXML
    private Label lbApellidoPaternoError;
    @FXML
    private Label lbPasswordError;
    @FXML
    private Label lbTipoUsuarioError;
    ObservableList<TipoUsuario> tiposUsuarios;
    private Notificacion observador;
    private Usuario usuarioEdicion;
    private boolean isEdicion;
    @FXML
    private TextField tfUsername;
    @FXML
    private Label lbUsernameError;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTipoUsuarios();
    }    
    
    public void inicializarInformacion(boolean isEdicion, Usuario usuarioEdicion, Notificacion observador) {
        this.isEdicion = isEdicion;
        this.usuarioEdicion = usuarioEdicion;
        this.observador = observador;
        if (isEdicion) {
            cargarInformacionEdicion();
        }
    }
    
    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            asignarTipoOperacion();
            Utilidad.getEscenarioComponente(tfNombre).close();
            limpiarCampos();   
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Utilidad.getEscenarioComponente(tfNombre).close();
    }
    
    private void cargarTipoUsuarios() {
        try {
            tiposUsuarios = FXCollections.observableArrayList();
            UsuarioDAOImpl usuarioDAOImpl = new UsuarioDAOImpl();
            tiposUsuarios.setAll(usuarioDAOImpl.leerTipoUsuario());
            cbTipoUsuario.setItems(tiposUsuarios);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean validarCampos() {
        boolean esValido = true;

        // Validar nombre
        if (tfNombre.getText().trim().isEmpty()) {
            lbNombreError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbNombreError.setText("");
        }

        // Validar apellido paterno
        if (tfApellidoPaterno.getText().trim().isEmpty()) {
            lbApellidoPaternoError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbApellidoPaternoError.setText("");
        }
        
        // Validar username
        if (tfUsername.getText().trim().isEmpty()) {
            lbUsernameError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbUsernameError.setText("");
        }

        // Validar contraseña
        String password = pfPassword.getText().trim();
        if (password.isEmpty()) {
            lbPasswordError.setText("*Campo obligatorio");
            esValido = false;
        } else if (!esPasswordSeguro(password)) {
            lbPasswordError.setText("8c, A-Z, a-z, 0-9, símbolo");
            esValido = false;
        } else {
            lbPasswordError.setText("");
        }

        // Validar tipo de usuario
        if (cbTipoUsuario.getValue() == null) {
            lbTipoUsuarioError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbTipoUsuarioError.setText("");
        }

        return esValido;
    }
    
    private void asignarTipoOperacion() {
        if (!isEdicion) {
            Usuario usuario = obtenerUsuarioNuevo();
            guardarUsuario(usuario);
            
        }
    }
    
    private void guardarUsuario(Usuario usuario) {
        try {
            UsuarioDAOImpl usuarioDAOImpl = new UsuarioDAOImpl();
            if (usuarioDAOImpl.crear(usuario)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Usuario registrado", 
                        "El usuario fue registrado con éxito.");
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo registrar el usuario, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private Usuario obtenerUsuarioNuevo() {
        Usuario usuario = new Usuario();
        if (isEdicion && usuarioEdicion != null) {
            usuario.setIdUsuario(usuarioEdicion.getIdUsuario());
        }
        usuario.setNombre(tfNombre.getText().trim());
        usuario.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        usuario.setApellidoMaterno(tfApellidoMaterno.getText().trim());
        usuario.setUsername(tfUsername.getText().trim());
        usuario.setPassword(Utilidad.hashearContraseña(pfPassword.getText().trim()));
        usuario.setIdTipoUsuario(cbTipoUsuario.getValue().getIdTipoUsuario());
        usuario.setTipoUsuario(cbTipoUsuario.getValue().getTipo());

        return usuario;
    }

    
    private void cargarInformacionEdicion() {
        //TODO
    }
    
    private boolean esPasswordSeguro(String password) {
        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*\\d.*") &&
               password.matches(".*[!@#\\$%\\^&\\*\\(\\)\\-\\+=].*");
    }
    
    private void limpiarCampos() {
        tfNombre.clear();
        tfApellidoPaterno.clear();
        tfApellidoMaterno.clear();
        tfUsername.clear();
        pfPassword.clear();
        
        lbNombreError.setText("");
        lbApellidoPaternoError.setText("");
        lbUsernameError.setText("");
        lbPasswordError.setText("");
        lbTipoUsuarioError.setText("");
    }
    
}
