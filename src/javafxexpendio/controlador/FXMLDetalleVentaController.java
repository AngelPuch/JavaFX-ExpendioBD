package javafxexpendio.controlador;

import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.VentaDAO;
import javafxexpendio.modelo.dao.VentaTablaDAOImpl;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.modelo.pojo.Venta;

public class FXMLDetalleVentaController implements Initializable {

    @FXML
    private Label lbIdVenta;
    @FXML
    private Label lbFecha;
    @FXML
    private Label lbCliente;
    @FXML
    private Label lbTotal;
    @FXML
    private TableView<DetalleVenta> tvDetalles;
    @FXML
    private TableColumn<DetalleVenta, String> colBebida;
    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> colPrecio;
    @FXML
    private TableColumn<DetalleVenta, Double> colTotal;
    @FXML
    private Button btnCerrar;
    
    private Venta venta;
    private double totalVenta;
    private int numProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }
    
    /**
     * Inicializa la ventana con los datos de la venta seleccionada.
     */
    public void inicializarVenta(Venta venta, double totalVenta, int numProductos) {
        this.venta = venta;
        this.totalVenta = totalVenta;
        this.numProductos = numProductos;
        mostrarDatosVenta();
        cargarDetallesVenta();
    }
    
    /**
     * Configura las columnas de la tabla de detalles.
     */
    private void configurarTabla() {
        colBebida.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBebida().getBebida()));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioBebida"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }
    
    /**
     * Muestra los datos básicos de la venta.
     */
    private void mostrarDatosVenta() {
        lbIdVenta.setText(String.valueOf(venta.getIdVenta()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        lbFecha.setText(sdf.format(venta.getFecha()));
        
        String nombreCliente = venta.getCliente() != null ? venta.getCliente().getNombre() : "Venta directa";
        lbCliente.setText(nombreCliente);
        
        lbTotal.setText("$" + String.format("%.2f", totalVenta));
    }
    
    /**
     * Carga los detalles de la venta desde la base de datos.
     */
    private void cargarDetallesVenta() {
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            ArrayList<DetalleVenta> detalles = ventaTablaDAOImpl.obtenerDetallesVenta(venta.getIdVenta());
            
            ObservableList<DetalleVenta> listaDetalles = FXCollections.observableArrayList(detalles);
            tvDetalles.setItems(listaDetalles);
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los detalles de la venta: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Maneja el evento de clic en el botón Cerrar.
     */
    @FXML
    private void btnCerrarClic(ActionEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Muestra una alerta con el mensaje y tipo especificados.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}