package javafxexpendio.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.modelo.pojo.DetallePedido;
import javafxexpendio.modelo.dao.ClienteDAOImpl;
import javafxexpendio.modelo.dao.PedidoClienteDAOImpl;
import javafxexpendio.modelo.dao.interfaz.PedidoClienteDAO;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Promocion;
import javafxexpendio.utilidades.BebidaSeleccionListener;
import javafxexpendio.utilidades.Utilidad;

public class FXMLFormularioPedidoClienteController implements Initializable, BebidaSeleccionListener {

    @FXML
    private TableView<DetallePedido> tvBebidasPedidos;
    @FXML
    private TableColumn<DetallePedido, String> colBebidas;
    @FXML
    private TableColumn<DetallePedido, Integer> colCantidad;
    @FXML
    private TableColumn<DetallePedido, Double> colPrecioUnitario;
    @FXML
    private ComboBox<Cliente> cbCliente;
    @FXML
    private DatePicker dpFechaLimitePedido;

    private ObservableList<DetallePedido> listaDetallePedido;
    private ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private PedidoClienteDAO pedidoClienteDAO = new PedidoClienteDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarClientes();
        inicializarTabla();
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.leerTodo();
            cbCliente.setItems(FXCollections.observableArrayList(clientes));
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar clientes",
                    "No se pudieron cargar los clientes: " + ex.getMessage());
        }
    }

    private void inicializarTabla() {
        listaDetallePedido = FXCollections.observableArrayList();
        tvBebidasPedidos.setItems(listaDetallePedido);

        colBebidas.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getBebida().getBebida()));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioBebida"));
        

        tvBebidasPedidos.setEditable(true);
        colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colCantidad.setOnEditCommit(event -> {
            DetallePedido detalle = event.getRowValue();
            detalle.setCantidad(event.getNewValue());
            tvBebidasPedidos.refresh();
        });
    }

    @FXML
    private void btnClicAgregarBebida(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLBebidas.fxml"));
            Parent root = loader.load();

            FXMLBebidasController bebidasController = loader.getController();
            bebidasController.setBebidaSeleccionListener(this);
            bebidasController.setClicAgregarBebida(true);
            bebidasController.setClicAgregarBebidaPedido(true);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Seleccionar Bebida");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de bebidas: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicEliminarBebida(ActionEvent event) {
        DetallePedido detalleSeleccionado = tvBebidasPedidos.getSelectionModel().getSelectedItem();
        if (detalleSeleccionado != null) {
            listaDetallePedido.remove(detalleSeleccionado);
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Atención", "Seleccione una bebida para eliminar.");
        }
    }

    @FXML
    private void btnClicConfirmarPedido(ActionEvent event) {
        Cliente cliente = cbCliente.getValue();
        if (cliente == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione un cliente.");
            return;
        }
        LocalDate fechaLimite = dpFechaLimitePedido.getValue();
        if (fechaLimite == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione una fecha límite.");
            return;
        }
        if (listaDetallePedido.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", "Agregue bebidas al pedido.");
            return;
        }

        List<Map<String, Object>> detalles = new ArrayList<>();
        for (DetallePedido dp : listaDetallePedido) {
            Map<String, Object> detalle = new HashMap<>();
            detalle.put("idBebida", dp.getBebida().getIdBebida());
            detalle.put("cantidad", dp.getCantidad());
            detalles.add(detalle);
        }

        try {
            Map<String, Object> resultado = pedidoClienteDAO.registrarPedidoCliente(cliente.getIdCliente(), fechaLimite, detalles);
            Utilidad.mostrarAlertaSimple((boolean) resultado.get("exito") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    "Resultado", (String) resultado.get("mensaje"));
            if ((boolean) resultado.get("exito")) {
                listaDetallePedido.clear();
                cbCliente.getSelectionModel().clearSelection();
                dpFechaLimitePedido.setValue(LocalDate.now());
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo registrar el pedido: " + ex.getMessage());
        }
        ((Stage) tvBebidasPedidos.getScene().getWindow()).close();
    }

    @Override
    public void onBebidaSeleccionada(Bebida bebida, Promocion promocion) {
        boolean encontrada = false;
        for (DetallePedido dp : listaDetallePedido) {
            if (dp.getBebida().getIdBebida() == bebida.getIdBebida()) {
                dp.setCantidad(dp.getCantidad() + 1);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            DetallePedido nuevoDetalle = new DetallePedido();
            nuevoDetalle.setBebida(bebida);
            nuevoDetalle.setCantidad(1);
            nuevoDetalle.setPrecioBebida(bebida.getPrecio());
            listaDetallePedido.add(nuevoDetalle);
        }
        tvBebidasPedidos.refresh();
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        boolean confirmar = Utilidad.mostrarAlertaConfirmacion("Confirmar cancelar", "¿Estás seguro de cancelar el pedido?", "Se cancelará la seleccion actual");
        if (confirmar) {
         ((Stage) tvBebidasPedidos.getScene().getWindow()).close();   
        }
    }
}