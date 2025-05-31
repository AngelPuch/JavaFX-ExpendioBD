package javafxexpendio.controlador;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.interfaz.CompraDAO;
import javafxexpendio.modelo.dao.CompraDAOImpl;
import javafxexpendio.modelo.dao.PedidoCompraDAOImpl;
import javafxexpendio.modelo.dao.ProveedorDAOImpl;
import javafxexpendio.modelo.pojo.Compra;
import javafxexpendio.modelo.pojo.DetalleCompra;
import javafxexpendio.modelo.pojo.DetallePedidoProveedor;
import javafxexpendio.modelo.pojo.PedidoProveedor;
import javafxexpendio.modelo.pojo.Proveedor;
import javafxexpendio.utilidades.Utilidad;

public class FXMLAdminCompraController implements Initializable {

    @FXML
    private ComboBox<Proveedor> cbProveedor;
    @FXML
    private DatePicker dpFechaCompra;
    @FXML
    private TextField tfFolioFactura;
    @FXML
    private TableView<PedidoProveedor> tvPedidosPendientes;
    @FXML
    private TableColumn<PedidoProveedor, Integer> colIdPedido;
    @FXML
    private TableColumn<PedidoProveedor, LocalDate> colFechaPedido;
    @FXML
    private TableColumn<PedidoProveedor, String> colEstadoPedido;
    @FXML
    private TableColumn<PedidoProveedor, Integer> colTotalProductos;
    @FXML
    private TableColumn<PedidoProveedor, Integer> colTotalUnidades;
    @FXML
    private TableColumn<PedidoProveedor, Double> colTotalEstimado;
    @FXML
    private Button btnContinuar;
    @FXML
    private TableView<DetalleCompra> tvDetallePedido;
    @FXML
    private TableColumn<DetalleCompra, String> colBebida;
    @FXML
    private TableColumn<DetalleCompra, Integer> colCantidadPedido;
    @FXML
    private TableColumn<DetalleCompra, Integer> colCantidadCompra;
    @FXML
    private TableColumn<DetalleCompra, Double> colPrecioCompra;
    @FXML
    private TableColumn<DetalleCompra, Double> colTotal;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnCancelar;
    
    private ObservableList<Proveedor> proveedores;
    private ObservableList<PedidoProveedor> pedidosPendientes;
    private ObservableList<DetalleCompra> detallesCompra;
    private List<DetallePedidoProveedor> detallesPedidoOriginal;
    
    private ProveedorDAOImpl proveedorDAO;
    private CompraDAO compraDAO;
    
    private PedidoProveedor pedidoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        proveedorDAO = new ProveedorDAOImpl();
        
        compraDAO = new CompraDAOImpl();
        
        proveedores = FXCollections.observableArrayList();
        pedidosPendientes = FXCollections.observableArrayList();
        detallesCompra = FXCollections.observableArrayList();
        detallesPedidoOriginal = new ArrayList<>();
        
        configurarTablas();
        configurarComboBoxes();
        
        // Inicializar fecha con la fecha actual
        dpFechaCompra.setValue(LocalDate.now());
        
        // Cargar proveedores
        cargarProveedores();
        
        // Configurar listener para el combo de proveedores
        cbProveedor.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarPedidosPendientes(newValue.getIdProveedor());
            } else {
                pedidosPendientes.clear();
            }
        });
        
        // Inicialmente deshabilitar secciones que dependen de selección previa
        habilitarSeccionDetalle(false);
    }
    
    private void configurarTablas() {
        // Configurar tabla de pedidos pendientes
        colIdPedido.setCellValueFactory(new PropertyValueFactory<>("idPedidoProveedor"));
        colFechaPedido.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstadoPedido.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTotalProductos.setCellValueFactory(new PropertyValueFactory<>("totalProductos"));
        colTotalUnidades.setCellValueFactory(new PropertyValueFactory<>("totalUnidades"));
        colTotalEstimado.setCellValueFactory(new PropertyValueFactory<>("totalEstimado"));
        
        tvPedidosPendientes.setItems(pedidosPendientes);
        
        // Configurar tabla de detalle de pedido
        colBebida.setCellValueFactory(new PropertyValueFactory<>("bebida"));
        colCantidadPedido.setCellValueFactory(cellData -> {
            int idBebida = cellData.getValue().getIdBebida();
            for (DetallePedidoProveedor detalle : detallesPedidoOriginal) {
                if (detalle.getIdBebida() == idBebida) {
                    return new SimpleIntegerProperty(detalle.getCantidad()).asObject();
                }
            }
            return new SimpleIntegerProperty(0).asObject();
        });
        colCantidadCompra.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioBebida"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        tvDetallePedido.setItems(detallesCompra);
    }
    
    private void configurarComboBoxes() {
        // Configurar cómo se muestra el proveedor en el ComboBox
        cbProveedor.setCellFactory(param -> new javafx.scene.control.ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getRazonSocial());
                }
            }
        });
        
        cbProveedor.setButtonCell(new javafx.scene.control.ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getRazonSocial());
                }
            }
        });
    }
    
    private void cargarProveedores() {
        try {
            proveedores.clear();
            List<Proveedor> listaProveedores = proveedorDAO.leerTodo();
            proveedores.addAll(listaProveedores);
            cbProveedor.setItems(proveedores);
        } catch (Exception ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar los proveedores: " + ex.getMessage());
        }
    }
    
    private void cargarPedidosPendientes(int idProveedor) {
        try {
            pedidosPendientes.clear();
            PedidoCompraDAOImpl pedidoCompraDAOImpl =  new PedidoCompraDAOImpl();
            List<PedidoProveedor> pedidos = pedidoCompraDAOImpl.obtenerPedidosPendientesPorProveedor(idProveedor);
            pedidosPendientes.addAll(pedidos);
        } catch (Exception ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar los pedidos pendientes: " + ex.getMessage());
        }
    }
    
    private void cargarDetallePedido(int idPedidoProveedor) {
        try {
            detallesCompra.clear();
            detallesPedidoOriginal.clear();
            PedidoCompraDAOImpl pedidoCompraDAOImpl =  new PedidoCompraDAOImpl();
            List<DetallePedidoProveedor> detalles = pedidoCompraDAOImpl.obtenerDetallePedidoProveedor(idPedidoProveedor);
            detallesPedidoOriginal.addAll(detalles);
            
            for (DetallePedidoProveedor detalle : detalles) {
                DetalleCompra detalleCompra = new DetalleCompra();
                detalleCompra.setIdBebida(detalle.getIdBebida());
                detalleCompra.setBebida(detalle.getBebida());
                detalleCompra.setCantidad(detalle.getCantidad());
                detalleCompra.setPrecioBebida(detalle.getPrecioEstimado());
                
                detallesCompra.add(detalleCompra);
            }
            
        } catch (Exception ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al cargar el detalle del pedido: " + ex.getMessage());
        }
    }
    
    private void habilitarSeccionDetalle(boolean habilitar) {
        tvDetallePedido.setDisable(!habilitar);
        btnActualizar.setDisable(!habilitar);
        btnEliminar.setDisable(!habilitar);
        btnRegistrar.setDisable(!habilitar);
    }

    @FXML
    private void btnClicContinuar(ActionEvent event) {
        pedidoSeleccionado = tvPedidosPendientes.getSelectionModel().getSelectedItem();
        
        if (pedidoSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un pedido para continuar");
            return;
        }
        
        // Cargar detalle del pedido seleccionado
        cargarDetallePedido(pedidoSeleccionado.getIdPedidoProveedor());
        
        // Habilitar sección de detalle
        habilitarSeccionDetalle(true);
    }

    @FXML
    private void btnClicActualizar(ActionEvent event) {
        DetalleCompra detalleSeleccionado = tvDetallePedido.getSelectionModel().getSelectedItem();
        
        if (detalleSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un producto para actualizar");
            return;
        }
        
        // Diálogo para actualizar cantidad
        TextInputDialog dialogCantidad = new TextInputDialog(String.valueOf(detalleSeleccionado.getCantidad()));
        dialogCantidad.setTitle("Actualizar Cantidad");
        dialogCantidad.setHeaderText("Actualizar cantidad de " + detalleSeleccionado.getBebida());
        dialogCantidad.setContentText("Cantidad:");
        
        Optional<String> resultCantidad = dialogCantidad.showAndWait();
        if (resultCantidad.isPresent()) {
            try {
                int nuevaCantidad = Integer.parseInt(resultCantidad.get());
                
                if (nuevaCantidad <= 0) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Cantidad inválida", 
                            "La cantidad debe ser mayor a cero");
                    return;
                }
                
                // Diálogo para actualizar precio
                TextInputDialog dialogPrecio = new TextInputDialog(String.format("%.2f", detalleSeleccionado.getPrecioBebida()));
                dialogPrecio.setTitle("Actualizar Precio");
                dialogPrecio.setHeaderText("Actualizar precio de " + detalleSeleccionado.getBebida());
                dialogPrecio.setContentText("Precio:");
                
                Optional<String> resultPrecio = dialogPrecio.showAndWait();
                if (resultPrecio.isPresent()) {
                    try {
                        double nuevoPrecio = Double.parseDouble(resultPrecio.get());
                        
                        if (nuevoPrecio <= 0) {
                            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Precio inválido", 
                                    "El precio debe ser mayor a cero");
                            return;
                        }
                        
                        // Actualizar detalle
                        detalleSeleccionado.setCantidad(nuevaCantidad);
                        detalleSeleccionado.setPrecioBebida(nuevoPrecio);
                        
                        // Actualizar tabla
                        tvDetallePedido.refresh();
                        
                    } catch (NumberFormatException ex) {
                        Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Formato inválido", 
                                "El precio debe ser un número válido");
                    }
                }
                
            } catch (NumberFormatException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Formato inválido", 
                        "La cantidad debe ser un número entero");
            }
        }
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
        DetalleCompra detalleSeleccionado = tvDetallePedido.getSelectionModel().getSelectedItem();
        
        if (detalleSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar un producto para eliminar");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este producto del pedido?");
        alert.setContentText("Esta acción no se puede deshacer.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            detallesCompra.remove(detalleSeleccionado);
        }
    }

    @FXML
    private void btnClicRegistrar(ActionEvent event) {
        if (detallesCompra.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Compra vacía", 
                    "No hay productos en la compra");
            return;
        }
        
        if (dpFechaCompra.getValue() == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Fecha requerida", 
                    "Debe seleccionar una fecha para la compra");
            return;
        }
        
        Proveedor proveedorSeleccionado = cbProveedor.getValue();
        if (proveedorSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Proveedor no seleccionado", 
                    "Debe seleccionar un proveedor para la compra");
            return;
        }
        
        try {
            // Crear objeto Compra
            Compra compra = new Compra();
            compra.setFecha(dpFechaCompra.getValue());
            compra.setIdProveedor(proveedorSeleccionado.getIdProveedor());
            compra.setFolioFactura(tfFolioFactura.getText());
            
            // Registrar compra
            Map<String, Object> resultado = compraDAO.registrarCompra(
                    compra, 
                    new ArrayList<>(detallesCompra), 
                    pedidoSeleccionado.getIdPedidoProveedor()
            );
            
            if ((boolean) resultado.get("exito")) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Compra registrada", 
                        "La compra ha sido registrada correctamente con ID: " + resultado.get("idCompra"));
                
                // Limpiar formulario
                limpiarFormulario();
                
                
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                        "No se pudo registrar la compra: " + resultado.get("mensaje"));
            }
            
        } catch (Exception ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                    "Error al registrar la compra: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        if (!detallesCompra.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar cancelación");
            alert.setHeaderText("¿Está seguro de cancelar la compra?");
            alert.setContentText("Se perderán todos los productos agregados a la compra.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                limpiarFormulario();
                Stage stage = (Stage) btnCancelar.getScene().getWindow();
                stage.close();
            }
        } else {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        }
    }
    
    private void limpiarFormulario() {
        cbProveedor.setValue(null);
        dpFechaCompra.setValue(LocalDate.now());
        tfFolioFactura.clear();
        pedidosPendientes.clear();
        detallesCompra.clear();
        detallesPedidoOriginal.clear();
        pedidoSeleccionado = null;
        habilitarSeccionDetalle(false);
    }
}