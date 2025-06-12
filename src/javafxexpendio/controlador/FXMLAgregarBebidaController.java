/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
 *
 * @author Dell
 */
public class FXMLAgregarBebidaController implements Initializable {

    @FXML
    private TableView<Bebida> tvBebidas;
    @FXML
    private TableColumn<Bebida, String> colBebida;
    @FXML
    private TableColumn<Bebida, Integer> colStock;
    @FXML
    private TableColumn<Bebida, Integer> colStockMinimo;
    @FXML
    private TableColumn<Bebida, Double> colPrecio;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnCancelar;
    
    private ObservableList<Bebida> bebidas;
    private Bebida bebidaSeleccionada;
    @FXML
    private TableColumn colContenidoNeto;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarBebidas();
    }
    
    private void configurarTabla() {
        colBebida.setCellValueFactory(new PropertyValueFactory<>("bebida"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colContenidoNeto.setCellValueFactory(new PropertyValueFactory("contenidoNeto"));
        
        tvBebidas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                bebidaSeleccionada = newSelection;
            }
        });
    }
    
    private void cargarBebidas() {
        bebidas = FXCollections.observableArrayList();
        BebidaDAOImpl bebidaDAO = new BebidaDAOImpl();
        
        try {
            List<Bebida> listaBebidas = bebidaDAO.leerTodo();
            
            if (listaBebidas != null && !listaBebidas.isEmpty()) {
                bebidas.addAll(listaBebidas);
                tvBebidas.setItems(bebidas);
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Sin bebidas", 
                        "No hay bebidas disponibles en el sistema");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                    "Error", 
                    "Error al cargar las bebidas: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        if (bebidaSeleccionada != null) {
            Stage escenario = (Stage) btnAgregar.getScene().getWindow();
            escenario.setUserData(bebidaSeleccionada);
            escenario.close();
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                    "Selección requerida", 
                    "Debe seleccionar una bebida de la tabla");
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Stage escenario = (Stage) btnCancelar.getScene().getWindow();
        escenario.close();
    }
}