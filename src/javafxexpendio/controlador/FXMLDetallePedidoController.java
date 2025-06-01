package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
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
import javafxexpendio.modelo.dao.interfaz.PedidoProveedorDAO;
import javafxexpendio.modelo.dao.PedidoProveedorDAOImpl;
import javafxexpendio.modelo.pojo.DetallePedidoProveedor;
import javafxexpendio.utilidades.Utilidad;

public class FXMLDetallePedidoController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TableView<DetallePedidoProveedor> tvDetallePedido;
    @FXML
    private TableColumn colBebida;
    @FXML
    private TableColumn colCantidad;
    @FXML
    private TableColumn colPrecioEstimado;
    @FXML
    private TableColumn colSubtotal;
    @FXML
    private Button btnCerrar;
    
    private ObservableList<DetallePedidoProveedor> detallesPedido;
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
        colBebida.setCellValueFactory(new PropertyValueFactory("bebida"));
        colCantidad.setCellValueFactory(new PropertyValueFactory("cantidad"));
        colPrecioEstimado.setCellValueFactory(new PropertyValueFactory("precioEstimado"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory("subtotal"));
        
        tvDetallePedido.setItems(detallesPedido);
    }
    
    private void cargarDetallePedido() {
        try {
            detallesPedido.clear();
            ObservableList<DetallePedidoProveedor> listaDetalles = pedidoProveedorDAO.obtenerDetallePedidoProveedor(idPedido);
            detallesPedido.addAll(listaDetalles);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar el detalle del pedido: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicCerrar(ActionEvent event) {
        Utilidad.getEscenarioComponente(lblTitulo).close();
    }
}