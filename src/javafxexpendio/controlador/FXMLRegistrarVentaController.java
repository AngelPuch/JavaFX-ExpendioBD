package javafxexpendio.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.DatePicker;
import javafxexpendio.modelo.dao.VentaDAOImpl;
import javafxexpendio.modelo.pojo.Promocion;

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
    @FXML
    private DatePicker dpFechaVenta;
    private Map<Integer, Promocion> promocionesAplicadas = new HashMap<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarClientes();
        listaDetalleVenta = FXCollections.observableArrayList();
        tvBebidasVenta.setItems(listaDetalleVenta);
        configurarTabla();
        
        dpFechaVenta.setValue(LocalDate.now());
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
        if (validarDatosVenta()) {
            try {
                List<Map<String, Object>> detallesVenta = new ArrayList<>();
            
                for (DetalleVenta detalle : listaDetalleVenta) {
                    Map<String, Object> detalleMap = new HashMap<>();
                    detalleMap.put("idBebida", detalle.getBebida().getIdBebida());
                    detalleMap.put("cantidad", detalle.getCantidad());
                
                    if (promocionesAplicadas.containsKey(detalle.getBebida().getIdBebida())) {
                        detalleMap.put("idPromocion", promocionesAplicadas.get(detalle.getBebida().getIdBebida()).getIdPromocion());
                    }
                
                    detallesVenta.add(detalleMap);
                }
                String folioFactura;
                boolean requiereFactura = Utilidad.mostrarAlertaConfirmacion("Confirmar factura", "¿El cliente quiere facturar?", "Se generara el folio de la factura automáticamente");
                if (requiereFactura) {
                    folioFactura = "F-" + System.currentTimeMillis();
                } else {
                    folioFactura = "NULL";
                }
                
                
                Date fecha = Date.from(dpFechaVenta.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                
                Integer idClienteParaVenta = null;
                Cliente clienteSeleccionado = cbCliente.getSelectionModel().getSelectedItem();
                
                if (clienteSeleccionado != null) {
                    idClienteParaVenta = clienteSeleccionado.getIdCliente();
                }
                
                VentaDAOImpl ventaDAO = new VentaDAOImpl();
                Map<String, Object> resultado = ventaDAO.registrarVenta(
                        idClienteParaVenta,
                        dpFechaVenta.getValue(),
                        folioFactura,
                        detallesVenta
                );
            
                if ((boolean) resultado.get("exito")) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Venta registrada", 
                            "La venta ha sido registrada correctamente.");
                
                    listaDetalleVenta.clear();
                    promocionesAplicadas.clear();
                    lbTotalCompra.setText("0.0");
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                            "No hay suficiente stock en una bebida para poder realizar la compra. Verifica por favor.");
                }
            
            } catch (SQLException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de conexión", 
                        "No se pudo conectar con la base de datos: " + ex.getMessage());
            }
        }
    }
    
    private boolean validarDatosVenta() {
        if (listaDetalleVenta.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", 
                    "Debe agregar al menos una bebida a la venta.");
            return false;
        }
    
        if (dpFechaVenta == null || dpFechaVenta.getValue() == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", 
                    "Debe seleccionar una fecha para la venta.");
            return false;
        }
    
        return true;
    }

    @FXML
    private void btnClicAgregarBebida(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLBebidas.fxml"));
            Parent root = loader.load();

            FXMLBebidasController bebidasController = loader.getController();
            bebidasController.setBebidaSeleccionListener(this);
            bebidasController.setClicAgregarBebida(true);
            if (cbCliente.getSelectionModel().getSelectedItem() == null) {
                bebidasController.setClicAgregarBebidaPedido(true);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Seleccionar Bebida");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            mostrarTotalCompra();
        } catch (IOException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar la ventana", "No se puedo cargar la seleccion de bebidas.");
        }
    }

    @FXML
    private void btnClicEliminarBebida(ActionEvent event) {
        if (tvBebidasVenta.getSelectionModel().getSelectedItem() != null) {
            listaDetalleVenta.remove(tvBebidasVenta.getSelectionModel().getSelectedIndex());
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin selección",
                "Por favor, selecciona una bebida primero.");
        }
        
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
    public void onBebidaSeleccionada(Bebida bebida, Promocion promocion) {
        
        DetalleVenta detalle = new DetalleVenta();
        detalle.setBebida(bebida);
        detalle.setCantidad(1); 
        detalle.setPrecioBebida(bebida.getPrecio());
        detalle.setTotal(bebida.getPrecio());
        listaDetalleVenta.add(detalle);
        if (promocion != null) {
            promocionesAplicadas.put(bebida.getIdBebida(), promocion);
        }
    }
    
}
