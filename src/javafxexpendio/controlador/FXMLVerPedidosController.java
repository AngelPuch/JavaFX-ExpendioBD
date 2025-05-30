package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.PedidoProveedorDAO;
import javafxexpendio.modelo.dao.PedidoProveedorDAOImpl;
import javafxexpendio.utilidades.Utilidad;

public class FXMLVerPedidosController implements Initializable {

    @FXML
    private TableView<Map<String, Object>> tvPedidos;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colIdPedido;
    @FXML
    private TableColumn<Map<String, Object>, LocalDate> colFecha;
    @FXML
    private TableColumn<Map<String, Object>, String> colProveedor;
    @FXML
    private TableColumn<Map<String, Object>, String> colEstado;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colTotalProductos;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colTotalUnidades;
    @FXML
    private TableColumn<Map<String, Object>, Double> colTotalEstimado;
    @FXML
    private Button btnVerDetalle;
    @FXML
    private Button btnCancelarPedido;
    @FXML
    private Button btnCerrar;
    
    private ObservableList<Map<String, Object>> pedidos;
    private PedidoProveedorDAO pedidoProveedorDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pedidoProveedorDAO = new PedidoProveedorDAOImpl();
        pedidos = FXCollections.observableArrayList();
        
        configurarTabla();
        cargarPedidos();
    }
    
    private void configurarTabla() {
        colIdPedido.setCellValueFactory(data -> new SimpleIntegerProperty((int) data.getValue().get("idPedidoProveedor")).asObject());
        colFecha.setCellValueFactory(data -> new SimpleObjectProperty<>((LocalDate) data.getValue().get("fecha")));
        colProveedor.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("proveedor")));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("estado")));
        colTotalProductos.setCellValueFactory(data -> new SimpleIntegerProperty((int) data.getValue().get("totalProductos")).asObject());
        colTotalUnidades.setCellValueFactory(data -> new SimpleIntegerProperty((int) data.getValue().get("totalUnidades")).asObject());
        colTotalEstimado.setCellValueFactory(data -> new SimpleDoubleProperty((double) data.getValue().get("totalEstimado")).asObject());
        
        tvPedidos.setItems(pedidos);
    }
    
    private void cargarPedidos() {
        try {
            pedidos.clear();
            ObservableList<Map<String, Object>> listaPedidos = pedidoProveedorDAO.obtenerPedidosPendientes();
            pedidos.addAll(listaPedidos);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar los pedidos: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicVerDetalle(ActionEvent event) {
        Map<String, Object> pedidoSeleccionado = tvPedidos.getSelectionModel().getSelectedItem();
        
        if (pedidoSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un pedido para ver su detalle");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxexpendio/vista/FXMLDetallePedido.fxml"));
            Parent root = loader.load();
            
            FXMLDetallePedidoController controller = loader.getController();
            controller.inicializarDetalle((int) pedidoSeleccionado.get("idPedidoProveedor"));
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Detalle de Pedido");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al abrir la ventana de detalle: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicCancelarPedido(ActionEvent event) {
        Map<String, Object> pedidoSeleccionado = tvPedidos.getSelectionModel().getSelectedItem();
        
        if (pedidoSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un pedido para cancelarlo");
            return;
        }
        
        String estado = (String) pedidoSeleccionado.get("estado");
        if (!"En espera".equals(estado)) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Operación no permitida", 
                    "Solo se pueden cancelar pedidos en estado 'En espera'");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar cancelación");
        alert.setHeaderText("¿Está seguro de cancelar el pedido?");
        alert.setContentText("Esta acción no se puede deshacer.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                int idPedido = (int) pedidoSeleccionado.get("idPedidoProveedor");
                boolean cancelado = pedidoProveedorDAO.cancelarPedidoProveedor(idPedido);
                
                if (cancelado) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Pedido cancelado", 
                            "El pedido ha sido cancelado correctamente");
                    
                    // Recargar pedidos
                    cargarPedidos();
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                            "No se pudo cancelar el pedido");
                }
                
            } catch (SQLException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                        "Error al cancelar el pedido: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void btnClicCerrar(ActionEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}