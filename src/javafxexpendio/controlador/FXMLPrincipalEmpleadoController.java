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
import javafxexpendio.utilidades.SesionUsuario;
import javafxexpendio.utilidades.Utilidad;

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
        cargarEscenas("vista/FXMLAdminClientes.fxml", false);
    }

    @FXML
    private void btnClicRegistrarVenta(ActionEvent event) {
        cargarEscenas("vista/FXMLRegistrarVenta.fxml", false);
    }

    @FXML
    private void btnClicBebidas(ActionEvent event) {
        cargarEscenas("vista/FXMLBebidas.fxml", true);
    }

    @FXML
    private void btnClicRegistrarPedido(ActionEvent event) {
        cargarEscenas("vista/FXMLEmpleadoPedido.fxml", false);
    }

    @FXML
    private void btnClicCerrarSesion(ActionEvent event) {
        try {
            SesionUsuario.getInstancia().cerrarSesion();
            FXMLLoader cargador = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLInicioSesion.fxml"));
            Parent vistaInicioSesion = cargador.load();
            Scene escenaInicio = new Scene(vistaInicioSesion);
            Stage escenarioActual = Utilidad.getEscenarioComponente(lbNombreUsuario);
            escenarioActual.setScene(escenaInicio);
            escenarioActual.setTitle("Inicio de sesión");
            escenarioActual.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void cargarEscenas(String ruta, boolean clicBebida) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource(ruta));
            Parent root = loader.load();
            
            if (clicBebida) {
                FXMLBebidasController bebidasController = loader.getController();
                bebidasController.setClicAgregarBebida(false);
                bebidasController.setClicAgregarBebidaPedido(false);
            }

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
