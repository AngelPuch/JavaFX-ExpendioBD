package javafxexpendio.controlador;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.interfaz.VentaDAO;
import javafxexpendio.modelo.dao.VentaTablaDAOImpl;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.modelo.pojo.Venta;
import javafxexpendio.modelo.pojo.VentaTabla;
import javafxexpendio.utilidades.GeneradorReportesPDF;
import javafxexpendio.utilidades.Utilidad;

public class FXMLAdminVentaController implements Initializable {

    @FXML
    private AnchorPane apVentas;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;
    @FXML
    private Button btnFiltrar;
    @FXML
    private Button btnLimpiarFiltro;
    @FXML
    private TableView<VentaTabla> tvVentas;
    @FXML
    private TableColumn<VentaTabla, Date> colFecha;
    @FXML
    private TableColumn<VentaTabla, String> colCliente;
    @FXML
    private TableColumn<VentaTabla, String> colFolioFactura;
    @FXML
    private TableColumn<VentaTabla, Double> colTotal;
    @FXML
    private TableColumn<VentaTabla, Integer> colNumProductos;
    @FXML
    private Button btnVerDetalle;
    @FXML
    private Button btnGenerarReporte;
    @FXML
    private Label lbTotalVentas;
    @FXML
    private Label lbIngresosTotales;
    @FXML
    private Label lbProductoMasVendido;
    
    private ObservableList<VentaTabla> listaVentas;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarVentas();
        cargarEstadisticas();

        btnVerDetalle.setDisable(true);
        btnGenerarReporte.setDisable(true);
        
        tvVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnVerDetalle.setDisable(!haySeleccion);
            btnGenerarReporte.setDisable(!haySeleccion);
        });
    }
    
    @FXML
    private void btnFiltrarClic(ActionEvent event) {
        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();
        
        if (fechaInicio == null || fechaFin == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", 
                    "Debe seleccionar ambas fechas para filtrar");
            return;
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Fechas inválidas", 
                    "La fecha de inicio debe ser anterior a la fecha fin");
            return;
        }
        
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            ArrayList<VentaTabla> ventasFiltradas = ventaTablaDAOImpl.obtenerVentasPorRangoFechasTabla(
                    Date.valueOf(fechaInicio), 
                    Date.valueOf(fechaFin));
            
            listaVentas = FXCollections.observableArrayList(ventasFiltradas);
            tvVentas.setItems(listaVentas);
            
            mostrarEstadisticasVenta();
            
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al filtrar ventas", e.getMessage());
        }
    }
    
    @FXML
    private void btnLimpiarFiltroClic(ActionEvent event) {
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        cargarVentas();
    }
    
    @FXML
    private void btnVerDetalleClic(ActionEvent event) {
        VentaTabla ventaSeleccionada = tvVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada != null) {
            try {
                VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
                Venta venta = ventaTablaDAOImpl.obtenerVentaPorId(ventaSeleccionada.getIdVenta());
                
                if (venta != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxexpendio/vista/FXMLDetalleVenta.fxml"));
                    Parent root = loader.load();
                    
                    FXMLDetalleVentaController controlador = loader.getController();
                    controlador.inicializarVenta(venta, ventaSeleccionada.getTotal(), ventaSeleccionada.getNumProductos());
                    
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Detalle de Venta #" + ventaSeleccionada.getIdVenta());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                            "No se encontró la venta seleccionada");
                }
                
            } catch (Exception e) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                        "No se pudo abrir la ventana de detalles: " + e.getMessage());
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar una venta para ver sus detalles");
        }
    }

    @FXML
    private void btnGenerarReporteClic(ActionEvent event) {
        VentaTabla ventaSeleccionada = tvVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada != null) {
            try {
                VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
                Venta venta = ventaTablaDAOImpl.obtenerVentaPorId(ventaSeleccionada.getIdVenta());
                ArrayList<DetalleVenta> detalles = ventaTablaDAOImpl.obtenerDetallesVenta(ventaSeleccionada.getIdVenta());

                if (venta != null && detalles != null) {
                    String rutaArchivo = GeneradorReportesPDF.generarReporteVenta(venta, detalles, ventaSeleccionada);
                    GeneradorReportesPDF.abrirArchivoPDF(rutaArchivo);

                    Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Reporte generado", 
                            "El reporte de la venta #" + ventaSeleccionada.getIdVenta() + 
                            " ha sido generado correctamente.");
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                            "No se encontraron datos completos para la venta seleccionada");
                }
            } catch (Exception e) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", 
                        "No se pudo generar el reporte: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Selección requerida", 
                    "Debe seleccionar una venta para generar su reporte");
        }
    }
    
    private void configurarTabla() {
        colFecha.setCellValueFactory(new PropertyValueFactory("fecha"));
        colCliente.setCellValueFactory(new PropertyValueFactory("nombreCliente"));
        colFolioFactura.setCellValueFactory(new PropertyValueFactory("folioFactura"));
        colTotal.setCellValueFactory(new PropertyValueFactory("total"));
        colNumProductos.setCellValueFactory(new PropertyValueFactory("numProductos"));
    }
    
    private void cargarVentas() {
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            ArrayList<VentaTabla> ventas = ventaTablaDAOImpl.obtenerTodasLasVentasTabla();
            listaVentas = FXCollections.observableArrayList(ventas);
            tvVentas.setItems(listaVentas);
            
            mostrarEstadisticasVenta();
            
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar ventas", e.getMessage());
        }
    }
    
    private void cargarEstadisticas() {
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            String productoMasVendido = ventaTablaDAOImpl.obtenerProductoMasVendido();
            lbProductoMasVendido.setText(productoMasVendido);
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar estadísticas", e.getMessage());
        }
    }
    
    private void mostrarEstadisticasVenta() {
        lbTotalVentas.setText(String.valueOf(listaVentas.size()));

        double ingresosTotales = 0.0;
        for (VentaTabla venta : listaVentas) {
            ingresosTotales += venta.getTotal();
        }
        lbIngresosTotales.setText("$" + String.format("%.2f", ingresosTotales));
    }

}