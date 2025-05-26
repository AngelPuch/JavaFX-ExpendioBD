/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.modelo.dao.ClienteDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.utilidades.BebidaSeleccionListener;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLRegistrarVentaController implements Initializable, BebidaSeleccionListener {

    @FXML
    private TableView<DetalleVenta> tvBebidasVenta;
    @FXML
    private TableColumn<DetalleVenta, String> colBebidas;
    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> colPrecioUnitario;
    @FXML
    private TableColumn<DetalleVenta, Double> colTotal;
    @FXML
    private ComboBox<Cliente> cbCliente;
    @FXML
    private Label lbTotalCompra;
    ObservableList<Cliente> clientes;
    ObservableList<DetalleVenta> listaDetalleVenta;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarClientes();
        listaDetalleVenta = FXCollections.observableArrayList();
        tvBebidasVenta.setItems(listaDetalleVenta);
        configurarTabla();
    }    
    
    private void cargarClientes() {
        try {
            clientes = FXCollections.observableArrayList();
            ClienteDAOImpl clienteDAOImpl = new ClienteDAOImpl();
            ArrayList<Cliente> clientesDAO = (ArrayList<Cliente>) clienteDAOImpl.leerTodo();
            clientes.addAll(clientesDAO);
            cbCliente.setItems(clientes);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de los clientes. "
                            + "Por favor intentelo más tarde.");
        }
    }
    
    private void configurarTabla() {
        colBebidas.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBebida().getBebida()));
        
        colCantidad.setCellValueFactory(new PropertyValueFactory("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory("precioBebida"));
        colTotal.setCellValueFactory(new PropertyValueFactory("total"));

        tvBebidasVenta.setEditable(true);
        colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colCantidad.setOnEditCommit(event -> {
            DetalleVenta detalle = event.getRowValue();
            detalle.setCantidad(event.getNewValue());
            detalle.setTotal(detalle.getCantidad() * detalle.getPrecioBebida());
            tvBebidasVenta.refresh();
            mostrarTotalCompra();
        });
    }

    @FXML
    private void btnClicConfirmarCompra(ActionEvent event) {
    }

    @FXML
    private void btnClicAgregarBebida(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLBebidas.fxml"));
            Parent root = loader.load();

            FXMLBebidasController bebidasController = loader.getController();
            bebidasController.setBebidaSeleccionListener(this);
            bebidasController.setClicAgregarBebida(true);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Seleccionar Bebida");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            mostrarTotalCompra();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnClicEliminarBebida(ActionEvent event) {
        listaDetalleVenta.remove(tvBebidasVenta.getSelectionModel().getSelectedIndex());
        tvBebidasVenta.refresh();
        mostrarTotalCompra();
    }
    
    private void mostrarTotalCompra() {
        double totalCompra = 0;
        for (DetalleVenta dv : listaDetalleVenta) { 
            totalCompra = totalCompra + dv.getTotal();
        }
        lbTotalCompra.setText(String.valueOf(totalCompra));
    }

    @Override
    public void onBebidaSeleccionada(Bebida bebida) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setBebida(bebida);
        detalle.setCantidad(1); // Por defecto
        detalle.setPrecioBebida(bebida.getPrecio());
        detalle.setTotal(bebida.getPrecio());
        listaDetalleVenta.add(detalle);
    }
    
}
