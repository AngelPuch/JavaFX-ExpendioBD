/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.dao.InicioSesionDAOImpl;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxexpendio.JavaFXAppExpendio;

public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfUser;
    @FXML
    private Label lbUserError;
    @FXML
    private Label lbPasswordError;
    @FXML
    private PasswordField pfPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection conexionBD = ConexionBD.abrirConexion();
        if (conexionBD != null) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Conexión Base de datos");
            alerta.setHeaderText("Conexión establecida.");
            alerta.setContentText("La conexión con tu base de datos se ha realizado correctamente");
            alerta.show();
        }
    }    

    @FXML
    private void btnClicIniciarSesion(ActionEvent event) {
        String username = tfUser.getText();
        String password = pfPassword.getText();

        if (validarCampos(username, password)) {
            validarCredenciales(username, password);
        }
    }

    private boolean validarCampos(String username, String password) {
        boolean camposValidos = true;

        lbUserError.setText("");
        lbPasswordError.setText("");

        if (username.isEmpty()) {
            lbUserError.setText("Usuario obligatorio.");
            camposValidos = false;
        }
        if (password.isEmpty()) {
            lbPasswordError.setText("Contraseña obligatoria");
            camposValidos = false;
        }

        return camposValidos;
    }

    private void validarCredenciales(String username, String password) {
        try {
            InicioSesionDAOImpl inicioSesionDAO = new InicioSesionDAOImpl();
            Usuario usuarioSesion = inicioSesionDAO.verificarCredenciales(username, password);
            if (usuarioSesion != null) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Credenciales correctas", 
                        "Bienvenido(a) " + usuarioSesion + " al sistema.");
                irPantallaPrincipal(usuarioSesion);
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Credenciales incorrectas", 
                        "Usuario y/o contraseña incorrectos, por favor verifica la información");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Problemas de conexión", ex.getMessage());
        }  
    }

    private void irPantallaPrincipal(Usuario usuarioSesion) {
        if (usuarioSesion.getTipoUsuario().equalsIgnoreCase("administrador")) {
            cargarPantallaAdministrador();
        } else if (usuarioSesion.getTipoUsuario().equalsIgnoreCase("empleado")) {
            cargarPantallaEmpleado();
        }
    }

    private void cargarPantallaAdministrador() {
        try {
            Stage escenarioBase = (Stage) pfPassword.getScene().getWindow();
            Parent vista = FXMLLoader.load(JavaFXAppExpendio.class.getResource("vista/FXMLPrincipalAdmin.fxml"));
            Scene escenaPrincipal = new Scene(vista);
            escenarioBase.setScene(escenaPrincipal);
            escenarioBase.setTitle("Home");
            escenarioBase.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarPantallaEmpleado() {
        try {
            Stage escenarioBase = (Stage) pfPassword.getScene().getWindow();
            Parent vista = FXMLLoader.load(JavaFXAppExpendio.class.getResource("vista/FXMLPrincipalEmpleado.fxml"));
            Scene escenaPrincipal = new Scene(vista);
            escenarioBase.setScene(escenaPrincipal);
            escenarioBase.setTitle("Home");
            escenarioBase.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
