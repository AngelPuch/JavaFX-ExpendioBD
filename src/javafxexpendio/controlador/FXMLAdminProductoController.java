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
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLAdminProductoController implements Initializable, Notificacion {

    @FXML
    private TableView<Bebida> tblProducto;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colStock;
    @FXML
    private TableColumn colStockMinimo;
    @FXML
    private TableColumn colPrecio;
    @FXML
    private TableColumn colContenidoNeto;
    @FXML
    private TextField tfBuscarBebida;
    private ObservableList<Bebida> bebidas;
    Usuario usuarioSesion;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
        
    }    
    
    public void inicializarInformacion(Usuario usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }
    
    @FXML
    private void btnClicAgregar(ActionEvent event) {
        irFormularioBebida(false, null);
    }


    @FXML
    private void btnClicEliminar(ActionEvent event) {
        Bebida bebidaSeleccionada = getBebidaSeleccionado();
        
        if (bebidaSeleccionada != null) {
            boolean confirmado = Utilidad.mostrarAlertaConfirmacion( "Confirmar eliminación",
                    "¿Estás seguro de eliminar la bebida?",
            "Se eliminará la bebida: " + bebidaSeleccionada.getBebida() + " de la lista de bebidas");

            if (confirmado) {
                try {
                    BebidaDAOImpl bebidaDAOImpl = new BebidaDAOImpl();
                    if (bebidaDAOImpl.eliminar(bebidaSeleccionada.getIdBebida())) {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Eliminación exitosa",
                        "La bebida ha sido eliminada correctamente.");
                        operacionExitosa();
                    } else {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error",
                        "No se pudo eliminar la bebida. Intenta de nuevo.");
                    }
                } catch (SQLException ex) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
                }
            }
        }
        
    }

    @FXML
    private void btnClicEditar(ActionEvent event) {
        Bebida bebidaSeleccionada = getBebidaSeleccionado();
        if (bebidaSeleccionada != null) {
            irFormularioBebida(true, bebidaSeleccionada);
        }
    }
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory("bebida"));
        colStock.setCellValueFactory(new PropertyValueFactory("stock"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory("stockMinimo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory("precio"));
        colContenidoNeto.setCellValueFactory(new PropertyValueFactory("contenidoNeto"));
    }
    
    private void cargarInformacionTabla() {
        try {
            BebidaDAOImpl bebidaDao = new BebidaDAOImpl();
            bebidas = FXCollections.observableArrayList();
            List<Bebida> bebidasDAO = bebidaDao.leerTodo();
            bebidas.addAll(bebidasDAO);
            tblProducto.setItems(bebidas);
            configurarFiltroBusqueda();
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los alumnos. Por favor intentelo más tarde");
            cerrarVentana();
        }
    }
    
    private void irFormularioBebida(boolean isEdicion, Bebida bebidaEdicion) {
        try{
            Stage escenarioAddProducto = new Stage();
            FXMLLoader loader = new FXMLLoader(javafxexpendio.JavaFXAppExpendio.class.getResource("vista/FXMLFormularioBebida.fxml"));
            Parent vista = loader.load();
            FXMLFormularioBebidaController controlador = loader.getController();
            controlador.inicializarInformacion(isEdicion, bebidaEdicion, this, usuarioSesion);
            Scene escena = new Scene(vista);
            escenarioAddProducto.setScene(escena);
            escenarioAddProducto.setTitle("Formulario producto");
            escenarioAddProducto.initModality(Modality.APPLICATION_MODAL);
            escenarioAddProducto.showAndWait();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private Bebida getBebidaSeleccionado() {
        Bebida bebidaSeleccionada = tblProducto.getSelectionModel().getSelectedItem();
    
        if (bebidaSeleccionada != null) {
            return bebidaSeleccionada;
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin selección",
                "Por favor, selecciona una bebida para eliminar.");
            return null;
        }
    }
    
    private void configurarFiltroBusqueda() {
        Utilidad.activarFiltroBusqueda(tfBuscarBebida, tblProducto, bebidas, bebida ->
            bebida.getBebida() + " " + bebida.getPrecio() + " " + bebida.getStock()
        );
    }
    
    private void cerrarVentana() {
        ((Stage)tblProducto.getScene().getWindow()).close();
    }

    @Override
    public void operacionExitosa() {
        cargarInformacionTabla();
    }
    
}
