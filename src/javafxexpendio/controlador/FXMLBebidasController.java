/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLBebidasController implements Initializable {

    @FXML
    private TableView<Bebida> tvBebidas;
    @FXML
    private TableColumn colBebida;
    @FXML
    private TableColumn colPrecio;
    @FXML
    private TableColumn colStock;
    @FXML
    private TextField tfBuscar;
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
        colBebida.setCellValueFactory(new PropertyValueFactory("bebida"));
        colPrecio.setCellValueFactory(new PropertyValueFactory("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory("stock"));
    }
    
    private void cargarInformacionTabla(){
        try {
            bebidas = FXCollections.observableArrayList();
            BebidaDAOImpl bebidaDAOImpl =  new BebidaDAOImpl();
            ArrayList<Bebida> bebidasDAO = (ArrayList<Bebida>) bebidaDAOImpl.leerTodo();
            bebidas.addAll(bebidasDAO);
            tvBebidas.setItems(bebidas);
        } catch (SQLException ex) {
             Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de las bebidas. "
                            + "Por favor intentelo más tarde.");
        }
    }
    
}
