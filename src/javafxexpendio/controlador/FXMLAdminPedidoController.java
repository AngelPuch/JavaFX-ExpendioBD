package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.interfaz.PedidoProveedorDAO;
import javafxexpendio.modelo.dao.PedidoProveedorDAOImpl;
import javafxexpendio.modelo.dao.ProveedorDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.DetallePedidoProveedor;
import javafxexpendio.modelo.pojo.PedidoProveedor;
import javafxexpendio.modelo.pojo.ProductoStockMinimo;
import javafxexpendio.modelo.pojo.Proveedor;
import javafxexpendio.utilidades.Utilidad;

public class FXMLAdminPedidoController implements Initializable {

    @FXML
    private TableView<ProductoStockMinimo> tvProductosStockMinimo;
    @FXML
    private TableColumn colBebidaMin;
    @FXML
    private TableColumn colStockActual;
    @FXML
    private TableColumn colStockMinimo;
    @FXML
    private TableColumn colPrecio;
    @FXML
    private TextField tfCantidad;
    @FXML
    private Button btnAgregar;
    @FXML
    private ComboBox<Proveedor> cbProveedor;
    @FXML
    private TableView<DetallePedidoProveedor> tvPedidoActual;
    @FXML
    private TableColumn colBebidaPedido;
    @FXML
    private TableColumn colCantidadPedido;
    @FXML
    private TableColumn colPrecioPedido;
    @FXML
    private TableColumn colTotalPedido;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnVerPedidos;
    
    private ObservableList<ProductoStockMinimo> productosStockMinimo;
    private ObservableList<DetallePedidoProveedor> detallesPedido;
    private ObservableList<Proveedor> proveedores;
    
    private PedidoProveedorDAO pedidoProveedorDAO;
    private ProveedorDAOImpl proveedorDAO;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pedidoProveedorDAO = new PedidoProveedorDAOImpl();
        proveedorDAO = new ProveedorDAOImpl();
        
        productosStockMinimo = FXCollections.observableArrayList();
        detallesPedido = FXCollections.observableArrayList();
        proveedores = FXCollections.observableArrayList();
        
        configurarTablas();
        cargarProveedores();
        cargarProductosStockMinimo();
    }
    
    private void configurarTablas() {
        colBebidaMin.setCellValueFactory(new PropertyValueFactory("nombreBebida"));
        colStockActual.setCellValueFactory(new PropertyValueFactory("stock"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory("stockMinimo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory("precio"));
        
        colBebidaPedido.setCellValueFactory(new PropertyValueFactory("bebida"));
        colCantidadPedido.setCellValueFactory(new PropertyValueFactory("cantidad"));
        colPrecioPedido.setCellValueFactory(new PropertyValueFactory("precioEstimado"));
        colTotalPedido.setCellValueFactory(new PropertyValueFactory("subtotal"));
        
        tvProductosStockMinimo.setItems(productosStockMinimo);
        tvPedidoActual.setItems(detallesPedido);
    }
    
    private void cargarProveedores() {
        try {
            proveedores.setAll(proveedorDAO.leerTodo());
            cbProveedor.setItems(proveedores);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar los proveedores: " + ex.getMessage());
        }
    }
    
    private void cargarProductosStockMinimo() {
        try {
            productosStockMinimo.clear();
            List<ProductoStockMinimo> productos = pedidoProveedorDAO.obtenerProductosStockMinimo();
            productosStockMinimo.addAll(productos);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar los productos con stock mínimo: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        ProductoStockMinimo productoSeleccionado = tvProductosStockMinimo.getSelectionModel().getSelectedItem();
        
        if (productoSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un producto para agregar al pedido");
            return;
        }
        
        String cantidadTexto = tfCantidad.getText();
        if (cantidadTexto.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Campo vacío", 
                    "Debe ingresar la cantidad a pedir");
            return;
        }
        
        try {
            int cantidad = Integer.parseInt(cantidadTexto);
            
            if (cantidad <= 0) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Cantidad inválida", 
                        "La cantidad debe ser mayor a cero");
                return;
            }
            
            boolean productoExistente = false;
            for (DetallePedidoProveedor detalle : detallesPedido) {
                if (detalle.getIdBebida() == productoSeleccionado.getIdBebida()) {
                    detalle.setCantidad(detalle.getCantidad() + cantidad);
                    productoExistente = true;
                    break;
                }
            }
            if (!productoExistente) {
                DetallePedidoProveedor detalle = new DetallePedidoProveedor(
                        productoSeleccionado.getIdBebida(),
                        productoSeleccionado.getNombreBebida(),
                        cantidad,
                        productoSeleccionado.getPrecio()
                );
                detallesPedido.add(detalle);
            }
           
            tvPedidoActual.refresh();
            tfCantidad.clear();
            
        } catch (NumberFormatException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Formato inválido", 
                    "La cantidad debe ser un número entero");
        }
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
        DetallePedidoProveedor detalleSeleccionado = tvPedidoActual.getSelectionModel().getSelectedItem();
        
        if (detalleSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un producto del pedido para eliminarlo");
            return;
        }
        
        detallesPedido.remove(detalleSeleccionado);
    }

    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (detallesPedido.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Pedido vacío", 
                    "No hay productos en el pedido");
            return;
        }

        Proveedor proveedorSeleccionado = cbProveedor.getValue();
        if (proveedorSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Proveedor no seleccionado", 
                    "Debe seleccionar un proveedor para el pedido");
            return;
        }

        try {
            List<Map<String, Object>> detallesMap = new ArrayList<>();
            for (DetallePedidoProveedor detalle : detallesPedido) {
                Map<String, Object> detalleMap = new HashMap<>();
                detalleMap.put("idBebida", detalle.getIdBebida());
                detalleMap.put("cantidad", detalle.getCantidad());
                detalleMap.put("precioEstimado", detalle.getPrecioEstimado());
                detallesMap.add(detalleMap);
            }

            PedidoProveedor nuevoPedido = new PedidoProveedor();

            boolean exito = pedidoProveedorDAO.registrarPedidoProveedor(
                    LocalDate.now(), 
                    proveedorSeleccionado.getIdProveedor(), 
                    "Pedido generado desde sistema", 
                    detallesMap,
                    nuevoPedido
            );

            if (exito) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Pedido registrado", 
                        "El pedido #" + nuevoPedido.getIdPedidoProveedor() + " ha sido registrado correctamente");
                limpiarFormulario();

                cargarProductosStockMinimo();
            }

        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al registrar el pedido: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        if (!detallesPedido.isEmpty()) {           
            boolean confirmado = Utilidad.mostrarAlertaConfirmacion("Confirmar cancelación", "¿Está seguro de cancelar el pedido?", 
                    "Se perderán todos los productos agregados al pedido.");
            if (confirmado) {
                limpiarFormulario();
            }
        } else {
            limpiarFormulario();
        }
    }
    
    private void limpiarFormulario() {
        detallesPedido.clear();
        cbProveedor.setValue(null);
        tfCantidad.clear();
    }

    @FXML
    private void btnClicVerPedidos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxexpendio/vista/FXMLVerPedidos.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Pedidos Registrados");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarProductosStockMinimo();
            
        } catch (Exception ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al abrir la ventana de pedidos: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicAddBebida(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxexpendio/vista/FXMLAgregarBebida.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Agregar Bebida");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        if (stage.getUserData() != null) {
            Bebida bebidaSeleccionada = (Bebida) stage.getUserData();
            
            boolean bebidaExistente = false;
            for (DetallePedidoProveedor detalle : detallesPedido) {
                if (detalle.getIdBebida() == bebidaSeleccionada.getIdBebida()) {
                    bebidaExistente = true;
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                            "Bebida duplicada", 
                            "La bebida seleccionada ya está en la lista del pedido");
                    break;
                }
            }
            
            if (!bebidaExistente) {
                Optional<String> result = Utilidad.mostrarDialogoEntrada("1", "Cantidad", "Ingrese la cantidad a pedir", 
                        "Cantidad:");
                if (result.isPresent()) {
                    try {
                        int cantidad = Integer.parseInt(result.get());
                        
                        if (cantidad <= 0) {
                            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                                    "Cantidad inválida", 
                                    "La cantidad debe ser mayor a cero");
                            return;
                        }
                        
                        DetallePedidoProveedor detalle = new DetallePedidoProveedor(
                                bebidaSeleccionada.getIdBebida(),
                                bebidaSeleccionada.getBebida(),
                                cantidad,
                                bebidaSeleccionada.getPrecio()
                        );
                        detallesPedido.add(detalle);
                        tvPedidoActual.refresh();
                        
                    } catch (NumberFormatException ex) {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                                "Formato inválido", 
                                "La cantidad debe ser un número entero");
                    }
                }
            }
        }
    } catch (Exception ex) {
        Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                "Error", 
                "Error al abrir la ventana de agregar bebida: " + ex.getMessage());
    }
    }
}