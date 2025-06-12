package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafxexpendio.modelo.dao.ClienteDAOImpl;
import javafxexpendio.modelo.dao.ReporteDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.modelo.pojo.ReporteProducto;
import javafxexpendio.modelo.pojo.ProductoStockMinimo;
import javafxexpendio.modelo.pojo.ReporteVenta;
import javafxexpendio.utilidades.GeneradorReportesPDF;
import javafxexpendio.utilidades.Utilidad;

public class FXMLAdminReporteController implements Initializable {

    @FXML
    private RadioButton rbVentasPeriodo;
    @FXML
    private RadioButton rbVentasProducto;
    @FXML
    private RadioButton rbStockMinimo;
    @FXML
    private RadioButton rbProductoMasVendido;
    @FXML
    private RadioButton rbProductoMenosVendido;
    @FXML
    private RadioButton rbProductoNoVendido;
    @FXML
    private RadioButton rbProductoMasVendidoCliente;
    @FXML
    private ToggleGroup tgReportes;
    @FXML
    private VBox vbFiltroPeriodo;
    @FXML
    private ComboBox<String> cbPeriodo;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;
    @FXML
    private VBox vbFiltroCliente;
    @FXML
    private ComboBox<Cliente> cbCliente;
    @FXML
    private Button btnGenerar;
    @FXML
    private Button btnExportar;
    @FXML
    private TableView tvReporte;
    
    private ObservableList<Cliente> listaClientes;
    private String reporteActual;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarToggleGroup();
        cargarClientes();
        
        dpFechaInicio.setValue(LocalDate.now().minusDays(7));
        dpFechaFin.setValue(LocalDate.now());

        tvReporte.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        btnExportar.setDisable(true);
    }
    
    private void configurarToggleGroup() {
        tgReportes.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                
                vbFiltroPeriodo.setVisible(false);
                vbFiltroCliente.setVisible(false);
                
                if (newValue == rbVentasPeriodo) {
                    vbFiltroPeriodo.setVisible(true);
                    reporteActual = "ventasPeriodo";
                } else if (newValue == rbVentasProducto) {
                    reporteActual = "ventasProducto";
                } else if (newValue == rbStockMinimo) {
                    reporteActual = "stockMinimo";
                } else if (newValue == rbProductoMasVendido) {
                    reporteActual = "productoMasVendido";
                } else if (newValue == rbProductoMenosVendido) {
                    reporteActual = "productoMenosVendido";
                } else if (newValue == rbProductoNoVendido) {
                    vbFiltroCliente.setVisible(true);
                    reporteActual = "productoNoVendido";
                } else if (newValue == rbProductoMasVendidoCliente) {
                    vbFiltroCliente.setVisible(true);
                    reporteActual = "productoMasVendidoCliente";
                }
                
                tvReporte.getItems().clear();
                tvReporte.getColumns().clear();
                btnExportar.setDisable(true);
            }
        });
        
        rbVentasPeriodo.setSelected(true);
    }
    
    @FXML
    private void btnGenerarReporte(ActionEvent event) {
        try {
            switch (reporteActual) {
                case "ventasPeriodo":
                    generarReporteVentasPeriodo();
                    break;
                case "ventasProducto":
                    generarReporteVentasProducto();
                    break;
                case "stockMinimo":
                    generarReporteStockMinimo();
                    break;
                case "productoMasVendido":
                    generarReporteProductoMasVendido();
                    break;
                case "productoMenosVendido":
                    generarReporteProductoMenosVendido();
                    break;
                case "productoNoVendido":
                    generarReporteProductoNoVendido();
                    break;
                case "productoMasVendidoCliente":
                    generarReporteProductoMasVendidoCliente();
                    break;
            }
            btnExportar.setDisable(false);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                    "Error", 
                    "Error al generar el reporte: " + ex.getMessage());
            btnExportar.setDisable(true);
        }
    }
    
    private void cargarClientes() {
        try {
            ClienteDAOImpl clienteDAOImpl = new ClienteDAOImpl();
            listaClientes = FXCollections.observableArrayList();
            listaClientes.setAll(clienteDAOImpl.leerTodo());
            cbCliente.setItems(listaClientes);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void generarReporteVentasPeriodo() throws SQLException {
        if (isFechaValida()) {
            ReporteDAOImpl reporteDAO = new ReporteDAOImpl();
            List<ReporteVenta> ventas = reporteDAO.obtenerVentasPorPeriodo(
                    dpFechaInicio.getValue(), dpFechaFin.getValue());
            
            if (ventas.isEmpty()) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Sin datos", 
                        "No hay ventas registradas en el periodo seleccionado");
                return;
            }
            configurarTablaReporteVentasPeriodo(ventas);
        }
    }
            
    private void generarReporteVentasProducto() throws SQLException {
        ReporteDAOImpl reporteDAO = new ReporteDAOImpl();
        List<ReporteProducto> productos = reporteDAO.obtenerVentasPorProducto();
        
        if (productos.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay ventas registradas");
            return;
        }
        configurarTablaMasMenosVendido(productos);
    }
   
    private void generarReporteProductoMasVendido() throws SQLException {
        ReporteDAOImpl reporteDAO = new ReporteDAOImpl();
        ReporteProducto producto = reporteDAO.obtenerProductoMasVendido();
        
        if (producto == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Sin datos", "No hay ventas registradas");
            return;
        }
  
        List<ReporteProducto> listaProducto = new ArrayList<>();
        listaProducto.add(producto);
        configurarTablaMasMenosVendido(listaProducto);
    }
    
    private void generarReporteProductoMenosVendido() throws SQLException {
        ReporteDAOImpl reporteDAO = new ReporteDAOImpl();
        ReporteProducto producto = reporteDAO.obtenerProductoMenosVendido();
        
        if (producto == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Sin datos", "No hay ventas registradas");
            return;
        }

        List<ReporteProducto> listaProducto = new ArrayList<>();
        listaProducto.add(producto);        
        configurarTablaMasMenosVendido(listaProducto);
    }
    
    private void generarReporteProductoMasVendidoCliente() throws SQLException {
        Cliente clienteSeleccionado = getClienteSeleccionado();
        if (clienteSeleccionado != null) {
            ReporteDAOImpl reporteDAO = new ReporteDAOImpl();
            ReporteProducto producto = reporteDAO.obtenerProductoMasVendidoACliente(clienteSeleccionado.getIdCliente());
        
            if (producto == null) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Sin datos", 
                        "No hay ventas registradas para este cliente");
                return;
            }

            List<ReporteProducto> listaProducto = new ArrayList<>();
            listaProducto.add(producto);
            configurarTablaMasMenosVendido(listaProducto);
        }   
    }
      
    private void generarReporteStockMinimo() throws SQLException {
        ReporteDAOImpl reporteDAO = new ReporteDAOImpl();
        List<ProductoStockMinimo> productos = reporteDAO.obtenerProductosStockMinimo();
        
        if (productos.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay productos con stock mínimo");
            return;
        }
        configurarTablaReporteStockMinimo(productos);
    }
        
    private void generarReporteProductoNoVendido() throws SQLException {
        Cliente clienteSeleccionado = getClienteSeleccionado();
        if (clienteSeleccionado != null) {
            ReporteDAOImpl reporteDAO = new ReporteDAOImpl();
            List<Bebida> productos = reporteDAO.obtenerProductosNoVendidosACliente(clienteSeleccionado.getIdCliente());
        
            if (productos.isEmpty()) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Sin datos", 
                        "No hay productos que no se hayan vendido a este cliente");
                return;
            }
            configurarTablaProductoNoVendido(productos);
        }       
    }
        
    @FXML
    private void btnExportarReporte(ActionEvent event) {
        try {
            String rutaArchivo = "";

            switch (reporteActual) {
                case "ventasPeriodo":
                    if (isFechaValida()) {
                        @SuppressWarnings("unchecked")
                        List<ReporteVenta> ventas = (List<ReporteVenta>) tvReporte.getItems();
                        if (!ventas.isEmpty()) {
                            rutaArchivo = GeneradorReportesPDF.generarReporteVentasPorPeriodo(
                                    ventas, dpFechaInicio.getValue(), dpFechaFin.getValue());
                        }
                    }
                    break;

                case "ventasProducto":
                case "productoMasVendido":
                case "productoMenosVendido":
                case "productoMasVendidoCliente":
                    @SuppressWarnings("unchecked")
                    List<ReporteProducto> productos = (List<ReporteProducto>) tvReporte.getItems();
                    if (!productos.isEmpty()) {
                        String titulo = obtenerTituloReporte();
                        rutaArchivo = GeneradorReportesPDF.generarReporteVentasProducto(productos, titulo);
                    }
                    break;

                case "stockMinimo":
                    @SuppressWarnings("unchecked")
                    List<ProductoStockMinimo> productosStock = (List<ProductoStockMinimo>) tvReporte.getItems();
                    if (!productosStock.isEmpty()) {
                        rutaArchivo = GeneradorReportesPDF.generarReporteStockMinimo(productosStock);
                    }
                    break;

                case "productoNoVendido":
                    Cliente clienteSeleccionado = cbCliente.getSelectionModel().getSelectedItem();
                    if (clienteSeleccionado != null) {
                        @SuppressWarnings("unchecked")
                        List<Bebida> productosNoVendidos = (List<Bebida>) tvReporte.getItems();
                        if (!productosNoVendidos.isEmpty()) {
                            rutaArchivo = GeneradorReportesPDF.generarReporteProductosNoVendidos(
                                    productosNoVendidos, clienteSeleccionado.getNombre());
                        }
                    }
                    break;
            }

            if (!rutaArchivo.isEmpty()) {
                GeneradorReportesPDF.abrirArchivoPDF(rutaArchivo);
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Reporte exportado", 
                        "El reporte ha sido generado correctamente.");
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                        "Sin datos", 
                        "No hay datos para generar el reporte.");
            }

        } catch (Exception ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                    "Error", 
                    "Error al exportar el reporte: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String obtenerTituloReporte() {
        if (reporteActual.equals("ventasProducto")) {
            return "Ventas por Producto";
        } else if (reporteActual.equals("productoMasVendido")) {
            return "Producto Más Vendido";
        } else if (reporteActual.equals("productoMenosVendido")) {
            return "Producto Menos Vendido";
        } else if (reporteActual.equals("productoMasVendidoCliente")) {
            Cliente cliente = cbCliente.getSelectionModel().getSelectedItem();
            return "Producto Más Vendido a " + cliente.getNombre();
        }
        return "Reporte";
    }
    
    private boolean isFechaValida() {
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", 
                    "Debe seleccionar las fechas de inicio y fin");
            return false;
        }
        
        if (dpFechaInicio.getValue().isAfter(dpFechaFin.getValue())) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Fechas inválidas", 
                    "La fecha de inicio debe ser anterior a la fecha de fin");
            return false;
        }
        return true;
    }
    
    private Cliente getClienteSeleccionado() {
        Cliente cliente = cbCliente.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Datos incompletos", 
                    "Debe seleccionar un cliente");
            return null;
        }
        return cliente;
    }
    
    private void configurarTablaReporteVentasPeriodo(List<ReporteVenta> ventas) {
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteVenta, Integer> colIdVenta = new TableColumn<>("Número venta");
        colIdVenta.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdVenta()).asObject());
        
        TableColumn<ReporteVenta, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFecha().toString()));
        
        TableColumn<ReporteVenta, String> colFolio = new TableColumn<>("Folio");
        colFolio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFolioFactura()));
        
        TableColumn<ReporteVenta, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCliente()));
        TableColumn<ReporteVenta, String> colRfc = new TableColumn<>("RFC");
        colRfc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRfc() != null ? data.getValue().getRfc() : "N/A"));

        TableColumn<ReporteVenta, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalVenta()).asObject());
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");

        tvReporte.getColumns().addAll(colIdVenta, colFecha, colFolio, colCliente, colRfc, colTotal);
        tvReporte.setItems(FXCollections.observableArrayList(ventas));
    }
    
    private void configurarTablaMasMenosVendido(List<ReporteProducto> listaProducto) {
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteProducto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreBebida()));
        
        TableColumn<ReporteProducto, Integer> colCantidad = new TableColumn<>("Cantidad vendida");
        colCantidad.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCantidadVendida()).asObject());
        colCantidad.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteProducto, Double> colTotal = new TableColumn<>("Total recaudado");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalRecaudado()).asObject());
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colNombre, colCantidad, colTotal);
        tvReporte.setItems(FXCollections.observableArrayList(listaProducto));
    }
    
    private void configurarTablaReporteStockMinimo(List<ProductoStockMinimo> productos) {
        tvReporte.getColumns().clear();
        
        TableColumn<ProductoStockMinimo, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreBebida()));
        
        TableColumn<ProductoStockMinimo, Integer> colStock = new TableColumn<>("Stock actual");
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colStock.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ProductoStockMinimo, Integer> colStockMin = new TableColumn<>("Stock mínimo");
        colStockMin.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockMinimo()).asObject());
        colStockMin.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ProductoStockMinimo, Integer> colDiferencia = new TableColumn<>("Diferencia");
        colDiferencia.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDiferencia()).asObject());
        colDiferencia.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ProductoStockMinimo, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colNombre, colStock, colStockMin, colDiferencia, colPrecio);
        tvReporte.setItems(FXCollections.observableArrayList(productos));
    }
    
    private void configurarTablaProductoNoVendido(List<Bebida> productos) {
        tvReporte.getColumns().clear();
        
        TableColumn<Bebida, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().toString()));
        
        TableColumn<Bebida, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colStock.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<Bebida, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colNombre, colStock, colPrecio);
        tvReporte.setItems(FXCollections.observableArrayList(productos));
    }
    
}