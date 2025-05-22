/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLAdminProductoController implements Initializable {

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
    private ObservableList<Bebida> bebidas;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
        
    }    
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory("bebida"));
        colStock.setCellValueFactory(new PropertyValueFactory("stock"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory("stockMinimo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory("precio"));
    }
    
    private void cargarInformacionTabla() {
        try {
            BebidaDAOImpl bebidaDao = new BebidaDAOImpl();
            bebidas = FXCollections.observableArrayList();
            List<Bebida> bebidasDAO = bebidaDao.leerTodo();
            bebidas.addAll(bebidasDAO);
            tblProducto.setItems(bebidas);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los alumnos. Por favor intentelo más tarde");
            cerrarVentana();
        }

    }
    
    private void cerrarVentana() {
        ((Stage)tblProducto.getScene().getWindow()).close();
    }

    
}
