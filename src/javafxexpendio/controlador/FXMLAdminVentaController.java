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
import javafxexpendio.modelo.dao.VentaDAO;
import javafxexpendio.modelo.dao.VentaTablaDAOImpl;
import javafxexpendio.modelo.pojo.Venta;
import javafxexpendio.modelo.pojo.VentaTabla;

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
    private Button btnExportarExcel;
    @FXML
    private Label lbTotalVentas;
    @FXML
    private Label lbIngresosTotales;
    @FXML
    private Label lbProductoMasVendido;
    
    private ObservableList<VentaTabla> listaVentas;

    /**
     * Inicializa el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarVentas();
        cargarEstadisticas();
        
        // Configurar botones inicialmente deshabilitados hasta seleccionar una venta
        btnVerDetalle.setDisable(true);
        btnGenerarReporte.setDisable(true);
        
        // Listener para habilitar botones cuando se selecciona una venta
        tvVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnVerDetalle.setDisable(!haySeleccion);
            btnGenerarReporte.setDisable(!haySeleccion);
        });
        
        // Establecer el texto del label de ingresos (para evitar problemas con el símbolo $)
        lbIngresosTotales.setText("$0.00");
    }
    
    /**
     * Configura las columnas de la tabla de ventas.
     */
    private void configurarTabla() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFolioFactura.setCellValueFactory(new PropertyValueFactory<>("folioFactura"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colNumProductos.setCellValueFactory(new PropertyValueFactory<>("numProductos"));
    }
    
    /**
     * Carga todas las ventas desde la base de datos.
     */
    private void cargarVentas() {
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            ArrayList<VentaTabla> ventas = ventaTablaDAOImpl.obtenerTodasLasVentasTabla();
            listaVentas = FXCollections.observableArrayList(ventas);
            tvVentas.setItems(listaVentas);
            
            // Actualizar contador de ventas
            lbTotalVentas.setText(String.valueOf(listaVentas.size()));
            
            // Calcular ingresos totales
            double ingresosTotales = 0.0;
            for (VentaTabla venta : listaVentas) {
                ingresosTotales += venta.getTotal();
            }
            lbIngresosTotales.setText("$" + String.format("%.2f", ingresosTotales));
            
        } catch (Exception e) {
            mostrarAlerta("Error al cargar ventas", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Carga las estadísticas de ventas.
     */
    private void cargarEstadisticas() {
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            String productoMasVendido = ventaTablaDAOImpl.obtenerProductoMasVendido();
            lbProductoMasVendido.setText(productoMasVendido);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar estadísticas", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Maneja el evento de clic en el botón Filtrar.
     */
    @FXML
    private void btnFiltrarClic(ActionEvent event) {
        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();
        
        if (fechaInicio == null || fechaFin == null) {
            mostrarAlerta("Datos incompletos", "Debe seleccionar ambas fechas para filtrar", Alert.AlertType.WARNING);
            return;
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta("Fechas inválidas", "La fecha de inicio debe ser anterior a la fecha fin", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            VentaTablaDAOImpl ventaTablaDAOImpl = new VentaTablaDAOImpl();
            ArrayList<VentaTabla> ventasFiltradas = ventaTablaDAOImpl.obtenerVentasPorRangoFechasTabla(
                    Date.valueOf(fechaInicio), 
                    Date.valueOf(fechaFin));
            
            listaVentas = FXCollections.observableArrayList(ventasFiltradas);
            tvVentas.setItems(listaVentas);
            
            // Actualizar contador de ventas filtradas
            lbTotalVentas.setText(String.valueOf(listaVentas.size()));
            
            // Calcular ingresos totales filtrados
            double ingresosTotales = 0.0;
            for (VentaTabla venta : listaVentas) {
                ingresosTotales += venta.getTotal();
            }
            lbIngresosTotales.setText("$" + String.format("%.2f", ingresosTotales));
            
        } catch (Exception e) {
            mostrarAlerta("Error al filtrar ventas", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Maneja el evento de clic en el botón Limpiar Filtro.
     */
    @FXML
    private void btnLimpiarFiltroClic(ActionEvent event) {
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        cargarVentas();
    }
    
    /**
     * Maneja el evento de clic en el botón Ver Detalle.
     */
    @FXML
    private void btnVerDetalleClic(ActionEvent event) {
        VentaTabla ventaSeleccionada = tvVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada != null) {
            try {
                // Obtener la venta completa con sus detalles
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
                    mostrarAlerta("Error", "No se encontró la venta seleccionada", Alert.AlertType.ERROR);
                }
                
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo abrir la ventana de detalles: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Selección requerida", "Debe seleccionar una venta para ver sus detalles", Alert.AlertType.WARNING);
        }
    }
    
    /**
     * Maneja el evento de clic en el botón Generar Reporte.
     */
    @FXML
    private void btnGenerarReporteClic(ActionEvent event) {
        VentaTabla ventaSeleccionada = tvVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada != null) {
            try {
                // Aquí iría el código para generar el reporte PDF
                // Puedes usar librerías como iText, JasperReports, etc.
                
                mostrarAlerta("Reporte generado", 
                        "El reporte de la venta #" + ventaSeleccionada.getIdVenta() + 
                        " ha sido generado correctamente.", 
                        Alert.AlertType.INFORMATION);
                
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo generar el reporte: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Selección requerida", "Debe seleccionar una venta para generar su reporte", Alert.AlertType.WARNING);
        }
    }
    
    /**
     * Maneja el evento de clic en el botón Exportar a Excel.
     */
    @FXML
    private void btnExportarExcelClic(ActionEvent event) {
        if (listaVentas == null || listaVentas.isEmpty()) {
            mostrarAlerta("Sin datos", "No hay ventas para exportar", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            // Aquí iría el código para exportar a Excel
            // Puedes usar librerías como Apache POI
            
            mostrarAlerta("Excel generado", 
                    "El archivo Excel con las ventas ha sido generado correctamente.", 
                    Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo exportar a Excel: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Muestra una alerta con el mensaje y tipo especificados.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}