/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.modelo.pojo.Usuario;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLPrincipalEmpleadoController implements Initializable {
    
    private Usuario usuarioSesion;

    @FXML
    private AnchorPane apCentral;
    @FXML
    private Label lbNombreUsuario;
    @FXML
    private Label lbUsername;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void inicializarInformacion(Usuario usuarioSesion){
        this.usuarioSesion = usuarioSesion;
        cargarInformacionUsuario();
    }
    
    private void cargarInformacionUsuario(){
        if (usuarioSesion != null) {
            lbNombreUsuario.setText(usuarioSesion.toString());
            lbUsername.setText(usuarioSesion.getUsername());
        }
    }


    @FXML
    private void btnClicClientes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLAdminClientes.fxml"));
            Parent root = loader.load();

            apCentral.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnClicRegistrarVenta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLRegistrarVenta.fxml"));
            Parent root = loader.load();

            apCentral.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnClicBebidas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLBebidas.fxml"));
            Parent root = loader.load();

            apCentral.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
