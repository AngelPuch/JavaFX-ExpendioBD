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
import javafxexpendio.modelo.dao.interfaz.VentaDAO;
import javafxexpendio.modelo.dao.VentaTablaDAOImpl;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.modelo.pojo.Venta;
import javafxexpendio.utilidades.Utilidad;

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
    private TableColumn colCantidad;
    @FXML
    private TableColumn colTotal;
    @FXML
    private TableColumn colPrecioUnitario;
    @FXML
    private TableColumn colPrecioDescuento;
    @FXML
    private Button btnCerrar;
    @FXML
    private Label lbRfc; // VARIABLE AGREGADA

    private Venta venta;
    private double totalVenta;
    private int numProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    public void inicializarVenta(Venta venta, double totalVenta, int numProductos) {
        this.venta = venta;
        this.totalVenta = totalVenta;
        this.numProductos = numProductos;
        mostrarDatosVenta();
        cargarDetallesVenta();
    }

    private void configurarTabla() {
        colBebida.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBebida().getBebida()));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioBebida"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colPrecioDescuento.setCellValueFactory(new PropertyValueFactory("precioConDescuento"));
    }

    private void mostrarDatosVenta() {
        lbIdVenta.setText(String.valueOf(venta.getIdVenta()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        lbFecha.setText(sdf.format(venta.getFecha()));
        
        // LÓGICA MODIFICADA PARA CLIENTE Y RFC
        String nombreCliente = (venta.getCliente() != null) ? venta.getCliente().getNombre() : "Venta directa";
        String rfcCliente = (venta.getCliente() != null) ? venta.getCliente().getRfc() : "Sin RFC";
        
        lbCliente.setText(nombreCliente);
        lbRfc.setText(rfcCliente);
        
        lbTotal.setText("$" + String.format("%.2f", totalVenta));
    }

    private void cargarDetallesVenta() {
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            ArrayList<DetalleVenta> detalles = ventaTablaDAOImpl.obtenerDetallesVenta(venta.getIdVenta());
            
            ObservableList<DetalleVenta> listaDetalles = FXCollections.observableArrayList(detalles);
            tvDetalles.setItems(listaDetalles);
            
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "No se pudieron cargar los detalles de la venta: " + e.getMessage());
        }
    }

    @FXML
    private void btnCerrarClic(ActionEvent event) {
        Utilidad.getEscenarioComponente(lbCliente).close();
    }
}