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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLPrincipalAdminController implements Initializable {

    @FXML
    private AnchorPane apCentral;
    @FXML
    private Label lbNombreVentana;
    private Usuario usuarioSesion;
    @FXML
    private Label lbNombreUsuario;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
    
    public void inicializarInformacion(Usuario usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
        cargarInformacion();
    }

    @FXML
    private void btnClicProveedor(ActionEvent event) {
        cargarEscenas("vista/FXMLAdminProveedor.fxml", false);
        lbNombreVentana.setText("Proveedores | Gestión de proveedores");
    }

    @FXML
    private void btnClicProducto(ActionEvent event) {
        cargarEscenas("vista/FXMLAdminProducto.fxml", true);
        lbNombreVentana.setText("Productos | Gestión de inventario");
    }

    @FXML
    private void btnClicVentas(ActionEvent event) {
    }

    @FXML
    private void btnClicPromociones(ActionEvent event) {
    }

    @FXML
    private void btnClicPedidos(ActionEvent event) {
    }

    @FXML
    private void btnClicUsuarios(ActionEvent event) {
        cargarEscenas("vista/FXMLAdminUsuario.fxml", false);
        lbNombreVentana.setText("Usuarios | Gestión de usuarios");
    }

    @FXML
    private void btnClicCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader cargador = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLInicioSesion.fxml"));
            Parent vistaInicioSesion = cargador.load();
            Scene escenaInicio = new Scene(vistaInicioSesion);
            Stage escenarioActual = Utilidad.getEscenarioComponente(lbNombreVentana);
            escenarioActual.setScene(escenaInicio);
            escenarioActual.setTitle("Inicio de sesión");
            escenarioActual.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void cargarInformacion() {
        if (usuarioSesion != null) {
            lbNombreUsuario.setText(usuarioSesion.toString());
        }
    }
    
    private void cargarEscenas(String ruta,  boolean isClicProducto) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource(ruta));
            Parent root = loader.load();
            if (isClicProducto) {
                FXMLAdminProductoController controlador = loader.getController();
                controlador.inicializarInformacion(usuarioSesion);
            }
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
