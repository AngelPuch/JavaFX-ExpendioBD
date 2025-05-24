package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
            pfPassword.setDisable(false);
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

        // Validar tipo de usuario
        if (cbTipoUsuario.getValue() == null) {
            lbTipoUsuarioError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbTipoUsuarioError.setText("");
        }
        
        // Validar password solo si es nuevo usuario o si se ha ingresado algo en el campo
        if (!isEdicion) {
            // Para nuevo usuario, siempre validar password
            if (!validarPassword()) {
                esValido = false;
            }
        } else {
            // Para edición, validar password solo si se ha ingresado algo
            String password = pfPassword.getText().trim();
            if (!password.isEmpty() && !validarPassword()) {
                esValido = false;
            } else {
                lbPasswordError.setText("");
            }
        }

        return esValido;
    }
    
    private void asignarTipoOperacion() {
        if (!isEdicion) {
            Usuario usuario = obtenerUsuarioNuevo();
            guardarUsuario(usuario);
        } else {
            Usuario usuario = obtenerUsuarioEdicion();
            actualizarUsuario(usuario);
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
    
    private void actualizarUsuario(Usuario usuario) {
        try {
            UsuarioDAOImpl usuarioDAOImpl = new UsuarioDAOImpl();
            if (usuarioDAOImpl.actualizar(usuario)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Usuario actualizado", 
                        "El usuario fue actualizado con éxito.");
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al actualizar", 
                        "No se pudo actualizar el usuario, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private Usuario obtenerUsuarioNuevo() {
        Usuario usuario = new Usuario();
        usuario.setNombre(tfNombre.getText().trim());
        usuario.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        usuario.setApellidoMaterno(tfApellidoMaterno.getText().trim());
        usuario.setUsername(tfUsername.getText().trim());
        usuario.setPassword(Utilidad.hashearPassword(pfPassword.getText().trim()));
        usuario.setIdTipoUsuario(cbTipoUsuario.getValue().getIdTipoUsuario());
        usuario.setTipoUsuario(cbTipoUsuario.getValue().getTipo());

        return usuario;
    }
    
    private Usuario obtenerUsuarioEdicion() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(usuarioEdicion.getIdUsuario());
        usuario.setNombre(tfNombre.getText().trim());
        usuario.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        usuario.setApellidoMaterno(tfApellidoMaterno.getText().trim());
        usuario.setUsername(tfUsername.getText().trim());
        
        // Si el campo de contraseña está vacío, mantener la contraseña actual
        String password = pfPassword.getText().trim();
        if (password.isEmpty()) {
            usuario.setPassword(usuarioEdicion.getPassword());
        } else {
            usuario.setPassword(Utilidad.hashearPassword(password));
        }
        
        usuario.setIdTipoUsuario(cbTipoUsuario.getValue().getIdTipoUsuario());
        usuario.setTipoUsuario(cbTipoUsuario.getValue().getTipo());

        return usuario;
    }
    
    private void cargarInformacionEdicion() {
        if (usuarioEdicion != null) {
            tfNombre.setText(usuarioEdicion.getNombre());
            tfApellidoPaterno.setText(usuarioEdicion.getApellidoPaterno());
            tfApellidoMaterno.setText(usuarioEdicion.getApellidoMaterno() != null ? usuarioEdicion.getApellidoMaterno() : "");
            tfUsername.setText(usuarioEdicion.getUsername());
            for (TipoUsuario tipo : tiposUsuarios) {
                if (tipo.getIdTipoUsuario() == usuarioEdicion.getIdTipoUsuario()) {
                    cbTipoUsuario.setValue(tipo);
                    break;
                }
            }
        }
    }
    
    private boolean validarPassword() {
        boolean esValido = true;
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
        
        return esValido;
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