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
import javafx.scene.layout.AnchorPane;
import javafxexpendio.JavaFXAppExpendio;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLPrincipalAdminController implements Initializable {

    @FXML
    private AnchorPane apCentral;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnClicProveedor(ActionEvent event) {
        cargarEscenas("vista/FXMLAdminProveedor.fxml");
    }

    @FXML
    private void btnClicProducto(ActionEvent event) {
        cargarEscenas("vista/FXMLAdminProducto.fxml");
    }
    
    @FXML
    private void btnClicInicio(ActionEvent event) {
    }
    
    private void cargarEscenas(String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource(ruta));
            Parent root = loader.load();
            apCentral.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
