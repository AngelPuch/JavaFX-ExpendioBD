/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafxexpendio.modelo.dao.InicioSesionDAOImpl;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfUser;
    @FXML
    private TextField tfPassword;
    @FXML
    private Label lbUserError;
    @FXML
    private Label lbPasswordError;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnClicIniciarSesion(ActionEvent event) {
        String username = tfUser.getText();
        String password = tfPassword.getText();
        
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
        
    }
    
}
