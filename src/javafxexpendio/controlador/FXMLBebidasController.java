/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.utilidades.BebidaSeleccionListener;
import javafxexpendio.utilidades.Utilidad;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.VentaDAOImpl;
import javafxexpendio.modelo.pojo.Promocion;

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
    private BebidaSeleccionListener listener;
    private boolean clicAgregarBebida;
    private boolean clicAgregarBebidaPedido;
    private Map<Integer, Promocion> promocionesSeleccionadas = new HashMap<>();
    
    public void setBebidaSeleccionListener(BebidaSeleccionListener listener) {
        this.listener = listener;
    }
    
    public void setClicAgregarBebida(boolean clicAgregarBebida) {
        this.clicAgregarBebida = clicAgregarBebida;
    }

    public void setClicAgregarBebidaPedido(boolean clicAgregarBebidaPedido) {
        this.clicAgregarBebidaPedido = clicAgregarBebidaPedido;
    }
    
    

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
            configurarFiltroBusqueda();
        } catch (SQLException ex) {
             Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de las bebidas. "
                            + "Por favor intentelo más tarde.");
        }
    }

    @FXML
    private void clicTablaBebidas(MouseEvent event) {
        if (clicAgregarBebida) {
            if (event.getClickCount() == 2 && !tvBebidas.getSelectionModel().isEmpty()) {
                Bebida bebidaSeleccionada = tvBebidas.getSelectionModel().getSelectedItem();
                if (bebidaSeleccionada.getStock() > 0) {
                    verificarPromocionesDisponibles(bebidaSeleccionada);
                    ((Stage) tvBebidas.getScene().getWindow()).close();
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se puede añadir esta bebida, no hay stock disponible.");
                }
            }
        }
    }
    
    private void verificarPromocionesDisponibles(Bebida bebida) {
        try {
            VentaDAOImpl ventaDAO = new VentaDAOImpl();
            List<Map<String, Object>> promocionesDisponibles = ventaDAO.obtenerPromocionesDisponibles(bebida.getIdBebida());
        
            if (!promocionesDisponibles.isEmpty() && !clicAgregarBebidaPedido) {
                // Hay promociones disponibles, mostrar diálogo
                Map<String, Object> datosPromocion = promocionesDisponibles.get(0);
                boolean aplicarPromocion = mostrarDialogoPromocion(bebida, datosPromocion);
            
                if (aplicarPromocion) {
                    // Crear objeto Promocion con los datos
                    Promocion promocion = new Promocion();
                    promocion.setIdPromocion((int) datosPromocion.get("idPromocion"));
                    promocion.setDescuento((double) datosPromocion.get("descuento"));
                    promocion.setDescripcion((String) datosPromocion.get("descripcion"));
                    LocalDate localFechaInicio = (LocalDate) datosPromocion.get("fechaInicio");
                    LocalDate localFechaFin = (LocalDate) datosPromocion.get("fechaFin");

                    promocion.setFechaInicio(Date.from(localFechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    promocion.setFechaFin(Date.from(localFechaFin.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    promocion.setBebida(bebida);
                    
                    // Aplicar descuento al precio de la bebida
                    double precioConDescuento = ventaDAO.calcularPrecioConDescuento(bebida.getPrecio(), promocion.getDescuento());
                
                    // Crear una copia de la bebida con el precio modificado
                    Bebida bebidaConPromocion = new Bebida();
                    bebidaConPromocion.setIdBebida(bebida.getIdBebida());
                    bebidaConPromocion.setBebida(bebida.getBebida() + " (Promoción aplicada)");
                    bebidaConPromocion.setPrecio(precioConDescuento);
                    bebidaConPromocion.setStock(bebida.getStock());
                
                    // Pasar la bebida con promoción al listener
                    if (listener != null) {
                        listener.onBebidaSeleccionada(bebidaConPromocion, promocion);
                    }
                } else {
                    // Pasar la bebida sin promoción
                    if (listener != null) {
                        listener.onBebidaSeleccionada(bebida, null);
                    }
                }
            } else {
                // No hay promociones, pasar la bebida normalmente
                if (listener != null) {
                    listener.onBebidaSeleccionada(bebida, null);
                }
            }
        
            ((Stage) tvBebidas.getScene().getWindow()).close();
        
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "No se pudieron verificar las promociones disponibles: " + ex.getMessage());
        
            // En caso de error, pasar la bebida sin promoción
            if (listener != null) {
                listener.onBebidaSeleccionada(bebida, null);
            }
            ((Stage) tvBebidas.getScene().getWindow()).close();
        }   
    }

    private boolean mostrarDialogoPromocion(Bebida bebida, Map<String, Object> promocion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Promoción disponible");
        alert.setHeaderText("¡Hay una promoción disponible para " + bebida.getBebida() + "!");
    
        double descuento = (double) promocion.get("descuento");
        String descripcion = (String) promocion.get("descripcion");
    
        alert.setContentText("Descripción: " + descripcion + "\n" +
                             "Descuento: " + descuento + "%\n\n" +
                             "¿Desea aplicar esta promoción?");
    
        ButtonType btnSi = new ButtonType("Sí");
        ButtonType btnNo = new ButtonType("No");
    
        alert.getButtonTypes().setAll(btnSi, btnNo);
    
        return alert.showAndWait().get() == btnSi;
    }
    
    private void configurarFiltroBusqueda() {
        Utilidad.activarFiltroBusqueda(tfBuscar, tvBebidas, bebidas, bebida ->
            bebida.getBebida() + " " + bebida.getPrecio() + " " + bebida.getStock()
        );
    }
      
}
