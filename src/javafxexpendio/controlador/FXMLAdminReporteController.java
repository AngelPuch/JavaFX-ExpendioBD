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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafxexpendio.modelo.dao.ReporteDAO;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.modelo.pojo.ReporteProducto;
import javafxexpendio.modelo.pojo.ReporteStockMinimo;
import javafxexpendio.modelo.pojo.ReporteVenta;
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
    @FXML
    private StackPane spGrafico;
    
    private ReporteDAO reporteDAO;
    private ObservableList<Cliente> listaClientes;
    private String reporteActual;
    @FXML
    private VBox vbFiltros;
    @FXML
    private TabPane tpVisualizacion;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reporteDAO = new ReporteDAO();
        configurarToggleGroup();
        cargarClientes();
        
        // Configurar fechas por defecto
        dpFechaInicio.setValue(LocalDate.now().minusDays(7));
        dpFechaFin.setValue(LocalDate.now());
        
        // Deshabilitar botón exportar hasta que haya datos
        btnExportar.setDisable(true);
    }
    
    private void configurarToggleGroup() {
        tgReportes.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Ocultar todos los filtros
                vbFiltroPeriodo.setVisible(false);
                vbFiltroCliente.setVisible(false);
                
                // Mostrar filtros según el reporte seleccionado
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
                
                // Limpiar tabla y gráfico
                tvReporte.getItems().clear();
                tvReporte.getColumns().clear();
                spGrafico.getChildren().clear();
                btnExportar.setDisable(true);
            }
        });
        
        // Seleccionar el primer radio button por defecto
        rbVentasPeriodo.setSelected(true);
    }
    
    private void cargarClientes() {
        try {
            listaClientes = FXCollections.observableArrayList(reporteDAO.obtenerClientes());
            cbCliente.setItems(listaClientes);
            
            // Configurar cómo se muestran los clientes en el ComboBox
            cbCliente.setCellFactory(param -> new javafx.scene.control.ListCell<Cliente>() {
                @Override
                protected void updateItem(Cliente cliente, boolean empty) {
                    super.updateItem(cliente, empty);
                    if (empty || cliente == null) {
                        setText(null);
                    } else {
                        setText(cliente.getNombre());
                    }
                }
            });
            
            cbCliente.setButtonCell(new javafx.scene.control.ListCell<Cliente>() {
                @Override
                protected void updateItem(Cliente cliente, boolean empty) {
                    super.updateItem(cliente, empty);
                    if (empty || cliente == null) {
                        setText(null);
                    } else {
                        setText(cliente.getNombre());
                    }
                }
            });
            
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                    "Error", 
                    "Error al cargar la lista de clientes: " + ex.getMessage());
        }
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
    
    private void generarReporteVentasPeriodo() throws SQLException {
        // Validar fechas
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                    "Datos incompletos", 
                    "Debe seleccionar las fechas de inicio y fin");
            return;
        }
        
        if (dpFechaInicio.getValue().isAfter(dpFechaFin.getValue())) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                    "Fechas inválidas", 
                    "La fecha de inicio debe ser anterior a la fecha de fin");
            return;
        }
        
        // Obtener datos
        List<ReporteVenta> ventas = reporteDAO.obtenerVentasPorPeriodo(
                dpFechaInicio.getValue(), dpFechaFin.getValue());
        
        if (ventas.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay ventas registradas en el periodo seleccionado");
            return;
        }
        
        // Configurar tabla
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteVenta, Integer> colIdVenta = new TableColumn<>("ID");
        colIdVenta.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdVenta()).asObject());
        
        TableColumn<ReporteVenta, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFecha().toString()));
        
        TableColumn<ReporteVenta, String> colFolio = new TableColumn<>("Folio");
        colFolio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFolioFactura()));
        
        TableColumn<ReporteVenta, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCliente()));
        
        TableColumn<ReporteVenta, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalVenta()).asObject());
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colIdVenta, colFecha, colFolio, colCliente, colTotal);
        tvReporte.setItems(FXCollections.observableArrayList(ventas));
        
        // Generar gráfico
        generarGraficoVentasPeriodo(ventas);
    }
    
    private void generarGraficoVentasPeriodo(List<ReporteVenta> ventas) {
        spGrafico.getChildren().clear();
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Fecha");
        yAxis.setLabel("Total ($)");
        barChart.setTitle("Ventas por periodo");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total de ventas");
        
        for (ReporteVenta venta : ventas) {
            series.getData().add(new XYChart.Data<>(venta.getFecha().toString(), venta.getTotalVenta()));
        }
        
        barChart.getData().add(series);
        spGrafico.getChildren().add(barChart);
    }
    
    private void generarReporteVentasProducto() throws SQLException {
        // Obtener datos
        List<ReporteProducto> productos = reporteDAO.obtenerVentasPorProducto();
        
        if (productos.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay ventas registradas");
            return;
        }
        
        // Configurar tabla
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteProducto, Integer> colIdBebida = new TableColumn<>("ID");
        colIdBebida.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBebida()).asObject());
        
        TableColumn<ReporteProducto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreBebida()));
        
        TableColumn<ReporteProducto, Integer> colCantidad = new TableColumn<>("Cantidad vendida");
        colCantidad.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCantidadVendida()).asObject());
        colCantidad.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteProducto, Double> colTotal = new TableColumn<>("Total recaudado");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalRecaudado()).asObject());
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colIdBebida, colNombre, colCantidad, colTotal);
        tvReporte.setItems(FXCollections.observableArrayList(productos));
        
        // Generar gráfico
        generarGraficoVentasProducto(productos);
    }
    
    private void generarGraficoVentasProducto(List<ReporteProducto> productos) {
        spGrafico.getChildren().clear();
        
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Ventas por producto");
        
        for (ReporteProducto producto : productos) {
            pieChart.getData().add(new PieChart.Data(
                    producto.getNombreBebida() + " (" + producto.getCantidadVendida() + ")", 
                    producto.getCantidadVendida()));
        }
        
        spGrafico.getChildren().add(pieChart);
    }
    
    private void generarReporteStockMinimo() throws SQLException {
        // Obtener datos
        List<ReporteStockMinimo> productos = reporteDAO.obtenerProductosStockMinimo();
        
        if (productos.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay productos con stock mínimo");
            return;
        }
        
        // Configurar tabla
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteStockMinimo, Integer> colIdBebida = new TableColumn<>("ID");
        colIdBebida.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBebida()).asObject());
        
        TableColumn<ReporteStockMinimo, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreBebida()));
        
        TableColumn<ReporteStockMinimo, Integer> colStock = new TableColumn<>("Stock actual");
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colStock.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteStockMinimo, Integer> colStockMin = new TableColumn<>("Stock mínimo");
        colStockMin.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockMinimo()).asObject());
        colStockMin.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteStockMinimo, Integer> colDiferencia = new TableColumn<>("Diferencia");
        colDiferencia.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDiferencia()).asObject());
        colDiferencia.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteStockMinimo, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colIdBebida, colNombre, colStock, colStockMin, colDiferencia, colPrecio);
        tvReporte.setItems(FXCollections.observableArrayList(productos));
        
        // Generar gráfico
        generarGraficoStockMinimo(productos);
    }
    
    private void generarGraficoStockMinimo(List<ReporteStockMinimo> productos) {
        spGrafico.getChildren().clear();
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Producto");
        yAxis.setLabel("Cantidad");
        barChart.setTitle("Productos con stock mínimo");
        
        XYChart.Series<String, Number> seriesStock = new XYChart.Series<>();
        seriesStock.setName("Stock actual");
        
        XYChart.Series<String, Number> seriesMinimo = new XYChart.Series<>();
        seriesMinimo.setName("Stock mínimo");
        
        for (ReporteStockMinimo producto : productos) {
            seriesStock.getData().add(new XYChart.Data<>(producto.getNombreBebida(), producto.getStock()));
            seriesMinimo.getData().add(new XYChart.Data<>(producto.getNombreBebida(), producto.getStockMinimo()));
        }
        
        barChart.getData().addAll(seriesStock, seriesMinimo);
        spGrafico.getChildren().add(barChart);
    }
    
    private void generarReporteProductoMasVendido() throws SQLException {
        // Obtener datos
        ReporteProducto producto = reporteDAO.obtenerProductoMasVendido();
        
        if (producto == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay ventas registradas");
            return;
        }
        
        // Crear lista con un solo elemento para la tabla
        List<ReporteProducto> listaProducto = new ArrayList<>();
        listaProducto.add(producto);
        
        // Configurar tabla
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteProducto, Integer> colIdBebida = new TableColumn<>("ID");
        colIdBebida.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBebida()).asObject());
        
        TableColumn<ReporteProducto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreBebida()));
        
        TableColumn<ReporteProducto, Integer> colCantidad = new TableColumn<>("Cantidad vendida");
        colCantidad.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCantidadVendida()).asObject());
        colCantidad.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteProducto, Double> colTotal = new TableColumn<>("Total recaudado");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalRecaudado()).asObject());
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colIdBebida, colNombre, colCantidad, colTotal);
        tvReporte.setItems(FXCollections.observableArrayList(listaProducto));
        
        // Generar gráfico
        generarGraficoProductoMasVendido(producto);
    }
    
    private void generarGraficoProductoMasVendido(ReporteProducto producto) {
        spGrafico.getChildren().clear();
        
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Producto más vendido");
        
        pieChart.getData().add(new PieChart.Data(
                producto.getNombreBebida() + " (" + producto.getCantidadVendida() + ")", 
                producto.getCantidadVendida()));
        
        spGrafico.getChildren().add(pieChart);
    }
    
    private void generarReporteProductoMenosVendido() throws SQLException {
        // Obtener datos
        ReporteProducto producto = reporteDAO.obtenerProductoMenosVendido();
        
        if (producto == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay ventas registradas");
            return;
        }
        
        // Crear lista con un solo elemento para la tabla
        List<ReporteProducto> listaProducto = new ArrayList<>();
        listaProducto.add(producto);
        
        // Configurar tabla
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteProducto, Integer> colIdBebida = new TableColumn<>("ID");
        colIdBebida.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBebida()).asObject());
        
        TableColumn<ReporteProducto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreBebida()));
        
        TableColumn<ReporteProducto, Integer> colCantidad = new TableColumn<>("Cantidad vendida");
        colCantidad.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCantidadVendida()).asObject());
        colCantidad.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteProducto, Double> colTotal = new TableColumn<>("Total recaudado");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalRecaudado()).asObject());
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colIdBebida, colNombre, colCantidad, colTotal);
        tvReporte.setItems(FXCollections.observableArrayList(listaProducto));
        
        // Generar gráfico
        generarGraficoProductoMenosVendido(producto);
    }
    
    private void generarGraficoProductoMenosVendido(ReporteProducto producto) {
        spGrafico.getChildren().clear();
        
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Producto menos vendido");
        
        pieChart.getData().add(new PieChart.Data(
                producto.getNombreBebida() + " (" + producto.getCantidadVendida() + ")", 
                producto.getCantidadVendida()));
        
        spGrafico.getChildren().add(pieChart);
    }
    
    private void generarReporteProductoNoVendido() throws SQLException {
        // Validar cliente seleccionado
        if (cbCliente.getSelectionModel().getSelectedItem() == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                    "Datos incompletos", 
                    "Debe seleccionar un cliente");
            return;
        }
        
        // Obtener datos
        Cliente clienteSeleccionado = cbCliente.getSelectionModel().getSelectedItem();
        List<Bebida> productos = reporteDAO.obtenerProductosNoVendidosACliente(clienteSeleccionado.getIdCliente());
        
        if (productos.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay productos que no se hayan vendido a este cliente");
            return;
        }
        
        // Configurar tabla
        tvReporte.getColumns().clear();
        
        TableColumn<Bebida, Integer> colIdBebida = new TableColumn<>("ID");
        colIdBebida.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBebida()).asObject());
        
        TableColumn<Bebida, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBebida()));
        
        TableColumn<Bebida, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colStock.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<Bebida, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colIdBebida, colNombre, colStock, colPrecio);
        tvReporte.setItems(FXCollections.observableArrayList(productos));
        
        // Generar gráfico
        generarGraficoProductoNoVendido(productos, clienteSeleccionado.getNombre());
    }
    
    private void generarGraficoProductoNoVendido(List<Bebida> productos, String nombreCliente) {
        spGrafico.getChildren().clear();
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Producto");
        yAxis.setLabel("Precio ($)");
        barChart.setTitle("Productos no vendidos a " + nombreCliente);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Precio");
        
        for (Bebida producto : productos) {
            series.getData().add(new XYChart.Data<>(producto.getBebida(), producto.getPrecio()));
        }
        
        barChart.getData().add(series);
        spGrafico.getChildren().add(barChart);
    }
    
    private void generarReporteProductoMasVendidoCliente() throws SQLException {
        // Validar cliente seleccionado
        if (cbCliente.getSelectionModel().getSelectedItem() == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                    "Datos incompletos", 
                    "Debe seleccionar un cliente");
            return;
        }
        
        // Obtener datos
        Cliente clienteSeleccionado = cbCliente.getSelectionModel().getSelectedItem();
        ReporteProducto producto = reporteDAO.obtenerProductoMasVendidoACliente(clienteSeleccionado.getIdCliente());
        
        if (producto == null) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Sin datos", 
                    "No hay ventas registradas para este cliente");
            return;
        }
        
        // Crear lista con un solo elemento para la tabla
        List<ReporteProducto> listaProducto = new ArrayList<>();
        listaProducto.add(producto);
        
        // Configurar tabla
        tvReporte.getColumns().clear();
        
        TableColumn<ReporteProducto, Integer> colIdBebida = new TableColumn<>("ID");
        colIdBebida.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdBebida()).asObject());
        
        TableColumn<ReporteProducto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreBebida()));
        
        TableColumn<ReporteProducto, Integer> colCantidad = new TableColumn<>("Cantidad vendida");
        colCantidad.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCantidadVendida()).asObject());
        colCantidad.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        TableColumn<ReporteProducto, Double> colTotal = new TableColumn<>("Total recaudado");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalRecaudado()).asObject());
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        tvReporte.getColumns().addAll(colIdBebida, colNombre, colCantidad, colTotal);
        tvReporte.setItems(FXCollections.observableArrayList(listaProducto));
        
        // Generar gráfico
        generarGraficoProductoMasVendidoCliente(producto, clienteSeleccionado.getNombre());
    }
    
    private void generarGraficoProductoMasVendidoCliente(ReporteProducto producto, String nombreCliente) {
        spGrafico.getChildren().clear();
        
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Producto más vendido a " + nombreCliente);
        
        pieChart.getData().add(new PieChart.Data(
                producto.getNombreBebida() + " (" + producto.getCantidadVendida() + ")", 
                producto.getCantidadVendida()));
        
        spGrafico.getChildren().add(pieChart);
    }
    
    @FXML
    private void btnExportarReporte(ActionEvent event) {
        // Este método se implementará más adelante si se decide incluir la funcionalidad
        Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                "Exportar a PDF", 
                "Funcionalidad en desarrollo");
    }
}