package javafxexpendio.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.JavaFXAppExpendio;

import javafxexpendio.modelo.dao.PedidoClienteDAOImpl;
import javafxexpendio.utilidades.Utilidad;

public class FXMLEmpleadoPedidoController implements Initializable {

    @FXML
    private TableView<Map<String, Object>> tvBebidasVenta;
    @FXML
    private TableColumn<Map<String, Object>, String> colBebidas;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colCantidad;
    @FXML
    private TableColumn<Map<String, Object>, Double> colPrecioUnitario;
    @FXML
    private TableColumn<Map<String, Object>, Double> colTotal;
    @FXML
    private DatePicker dpFechaVenta;
    @FXML
    private Label lbTotalCompra;
    @FXML
    private TableView<Map<String, Object>> tvPedidos;
    @FXML
    private TableColumn<Map<String, Object>, String> colCliente;
    @FXML
    private TableColumn<Map<String, Object>, String> colEstado;
    @FXML
    private TableColumn<Map<String, Object>, LocalDate> colFechaCreacion;
    @FXML
    private TableColumn<Map<String, Object>, LocalDate> colFechaLimite;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colProductos;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colUnidades;
    @FXML
    private TableColumn<Map<String, Object>, Double> colTotalEstimado;

    private PedidoClienteDAOImpl pedidoClienteDAO = new PedidoClienteDAOImpl();
    @FXML
    private Button btnConfirmarCompra;
    @FXML
    private Button btnCancelarPedido;
    @FXML
    private Button btnVerDetalle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarColumnasPedidos();
        inicializarColumnasDetalle();
        cargarPedidosPendientes();
        tvBebidasVenta.setDisable(true);
        dpFechaVenta.setDisable(true);
        btnConfirmarCompra.setDisable(true);
        deshabilitarControlesPedido(true);
        dpFechaVenta.setValue(LocalDate.now());

        tvPedidos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean seleccionado = newSel != null;
            deshabilitarControlesPedido(!seleccionado);
            if (!seleccionado) {
                tvBebidasVenta.getItems().clear();
                lbTotalCompra.setText("0.00");
            }
        });
    }

    private void inicializarColumnasPedidos() {
        colCliente.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().get("cliente").toString()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().get("estado").toString()));
        colFechaCreacion.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
                (LocalDate) data.getValue().get("fechaCreacion")));
        colFechaLimite.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
                (LocalDate) data.getValue().get("fecha_limite")));
        colProductos.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
                (Integer) data.getValue().get("total_productos")).asObject());
        colUnidades.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
                (Integer) data.getValue().get("total_unidades")).asObject());
        colTotalEstimado.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(
                (Double) data.getValue().get("total_estimado")).asObject());
    }

    private void inicializarColumnasDetalle() {
        colBebidas.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().get("bebida").toString()));
        colCantidad.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
                (Integer) data.getValue().get("cantidad")).asObject());
        colPrecioUnitario.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(
                (Double) data.getValue().get("precio_bebida")).asObject());
        colTotal.setCellValueFactory(data -> {
            double subtotal = (Integer) data.getValue().get("cantidad") * (Double) data.getValue().get("precio_bebida");
            return new javafx.beans.property.SimpleDoubleProperty(subtotal).asObject();
        });
    }

    private void cargarPedidosPendientes() {
        try {
            List<Map<String, Object>> pedidos = pedidoClienteDAO.obtenerPedidosPendientes();
            tvPedidos.setItems(FXCollections.observableArrayList(pedidos));
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los pedidos: " + ex.getMessage());
        }
    }

    private void deshabilitarControlesPedido(boolean deshabilitar) {
        btnCancelarPedido.setDisable(deshabilitar);
        btnVerDetalle.setDisable(deshabilitar);
    }

    @FXML
    private void btnClicVerDetalle(ActionEvent event) {
        Map<String, Object> pedido = tvPedidos.getSelectionModel().getSelectedItem();
        if (pedido == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Atención", "Seleccione un pedido para ver detalle.");
            return;
        }
        tvBebidasVenta.setDisable(false);
        dpFechaVenta.setDisable(false);
        btnConfirmarCompra.setDisable(false);
        int idPedido = (int) pedido.get("idPedidoCliente");
        try {
            List<Map<String, Object>> detalles = pedidoClienteDAO.obtenerDetallePedido(idPedido);
            tvBebidasVenta.setItems(FXCollections.observableArrayList(detalles));
            actualizarTotalCompra();
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo obtener el detalle: " + ex.getMessage());
        }
    }

    @FXML
    private void btnClicCancelarPedido(ActionEvent event) {
        Map<String, Object> pedido = tvPedidos.getSelectionModel().getSelectedItem();
        if (pedido == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Atención", "Seleccione un pedido para cancelar.");
            return;
        }
        int idPedido = (int) pedido.get("idPedidoCliente");
        boolean confirmar = Utilidad.mostrarAlertaConfirmacion("Confirmar cancelar", "¿Estás seguro de cancelar el pedido?", "Se cancelará el pedido seleccionado");
        if (confirmar) {
            try {
                boolean cancelado = pedidoClienteDAO.cancelarPedidoCliente(idPedido);
                
                if (cancelado) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Pedido cancelado", 
                            "El pedido ha sido cancelado correctamente");
                    cargarPedidosPendientes();
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                            "No se pudo cancelar el pedido");
                }
                
                    tvBebidasVenta.getItems().clear();
                    lbTotalCompra.setText("0.00");
                    deshabilitarControlesPedido(true);
            } catch (SQLException ex) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo cancelar el pedido: " + ex.getMessage());
                }
        }
    }
    

    @FXML
    private void btnClicConfirmarCompra(ActionEvent event) {
        Map<String, Object> pedido = tvPedidos.getSelectionModel().getSelectedItem();
        if (pedido == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Atención", "Seleccione un pedido para confirmar la compra.");
            return;
        }
        int idPedido = (int) pedido.get("idPedidoCliente");
        LocalDate fechaVenta = dpFechaVenta.getValue();
        if (fechaVenta == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Atención", "Seleccione una fecha para la venta.");
            return;
        }
        String folioFactura;
        boolean requiereFactura = Utilidad.mostrarAlertaConfirmacion("Confirmar factura", "¿El cliente quiere facturar?", "Se generara el folio de la factura automáticamente");
        if (requiereFactura) {
            folioFactura = "F-" + System.currentTimeMillis();
        } else {
            folioFactura = "NULL";
        }
        try {
            Map<String, Object> resultado = pedidoClienteDAO.generarVentaDesdePedido(idPedido, fechaVenta, folioFactura);
            
            if ((boolean) resultado.get("exito")) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Venta registrada", 
                            "La venta ha sido registrada correctamente.");
                boolean actualizado = pedidoClienteDAO.actualizarEstadoPedido(idPedido, 2);
                if (!actualizado) {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el estado del pedido.");
                }
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                            "No hay suficiente stock en una bebida para poder realizar la compra. Verifica por favor.");
            }
            cargarPedidosPendientes();
            tvBebidasVenta.getItems().clear();
            lbTotalCompra.setText("0.00");
            tvBebidasVenta.setDisable(true);
            dpFechaVenta.setDisable(true);
            btnConfirmarCompra.setDisable(true);
            deshabilitarControlesPedido(true);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                            "No hay suficiente stock en una bebida para poder realizar la compra. Verifica por favor.");
        }
    }

    private void actualizarTotalCompra() {
        double total = 0;
        for (Map<String, Object> detalle : tvBebidasVenta.getItems()) {
            int cantidad = (int) detalle.get("cantidad");
            double precio = (double) detalle.get("precio_bebida");
            total += cantidad * precio;
        }
        lbTotalCompra.setText(String.format("%.2f", total));
    }

    @FXML
    private void btnClicAgregarPedido(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLFormularioPedidoCliente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Agregar Pedido");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarPedidosPendientes();
            tvBebidasVenta.setDisable(true);
            dpFechaVenta.setDisable(true);
            btnConfirmarCompra.setDisable(true);
        } catch (IOException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo abrir el formulario de pedido: " + ex.getMessage());
        }
    }
}