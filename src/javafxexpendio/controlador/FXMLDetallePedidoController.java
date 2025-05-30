package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.PedidoProveedorDAO;
import javafxexpendio.modelo.dao.PedidoProveedorDAOImpl;
import javafxexpendio.utilidades.Utilidad;

public class FXMLDetallePedidoController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TableView<Map<String, Object>> tvDetallePedido;
    @FXML
    private TableColumn<Map<String, Object>, String> colBebida;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colCantidad;
    @FXML
    private TableColumn<Map<String, Object>, Double> colPrecioEstimado;
    @FXML
    private TableColumn<Map<String, Object>, Double> colSubtotal;
    @FXML
    private Button btnCerrar;
    
    private ObservableList<Map<String, Object>> detallesPedido;
    private PedidoProveedorDAO pedidoProveedorDAO;
    private int idPedido;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pedidoProveedorDAO = new PedidoProveedorDAOImpl();
        detallesPedido = FXCollections.observableArrayList();
        
        configurarTabla();
    }
    
    public void inicializarDetalle(int idPedido) {
        this.idPedido = idPedido;
        lblTitulo.setText("Detalle de Pedido #" + idPedido);
        cargarDetallePedido();
    }
    
    private void configurarTabla() {
        colBebida.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("bebida")));
        colCantidad.setCellValueFactory(data -> new SimpleIntegerProperty((int) data.getValue().get("cantidad")).asObject());
        colPrecioEstimado.setCellValueFactory(data -> new SimpleDoubleProperty((double) data.getValue().get("precioEstimado")).asObject());
        colSubtotal.setCellValueFactory(data -> new SimpleDoubleProperty((double) data.getValue().get("subtotal")).asObject());
        
        tvDetallePedido.setItems(detallesPedido);
    }
    
    private void cargarDetallePedido() {
        try {
            detallesPedido.clear();
            ObservableList<Map<String, Object>> listaDetalles = pedidoProveedorDAO.obtenerDetallePedidoProveedor(idPedido);
            detallesPedido.addAll(listaDetalles);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar el detalle del pedido: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicCerrar(ActionEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}