/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.utilidades;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.modelo.pojo.ProductoStockMinimo;
import javafxexpendio.modelo.pojo.ReporteProducto;
import javafxexpendio.modelo.pojo.ReporteVenta;
import javafxexpendio.modelo.pojo.Venta;
import javafxexpendio.modelo.pojo.VentaTabla;

/**
 *
 * @author Dell
 */
public class GeneradorReportesPDF {
    
    // Fuentes comunes
    private static final Font FONT_TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font FONT_SUBTITULO = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 12);
    private static final Font FONT_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_PIE_PAGINA = new Font(Font.FontFamily.HELVETICA, 8);
    
    /**
     * Genera un reporte PDF para una venta específica
     * @param venta Objeto Venta con los datos de la venta
     * @param detalles Lista de detalles de la venta
     * @param ventaTabla Objeto VentaTabla con información adicional
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteVenta(Venta venta, List<DetalleVenta> detalles, VentaTabla ventaTabla) throws Exception {
        String fileName = "Reporte_Venta_" + venta.getIdVenta() + ".pdf";
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(fileName));
        documento.open();
        
        // Agregar logo (opcional)
        agregarLogo(documento);
        
        // Título del reporte
        agregarTitulo(documento, "REPORTE DE VENTA");
        
        // Datos de la venta
        documento.add(new Paragraph("DATOS DE LA VENTA", FONT_SUBTITULO));
        documento.add(new Paragraph("Folio: " + (venta.getFolioFactura() != null ? venta.getFolioFactura() : "N/A"), FONT_NORMAL));
        documento.add(new Paragraph("Fecha: " + venta.getFecha(), FONT_NORMAL));
        documento.add(new Paragraph("ID Venta: " + venta.getIdVenta(), FONT_NORMAL));
        documento.add(new Paragraph("Total: $" + String.format("%.2f", ventaTabla.getTotal()), FONT_NEGRITA));
        documento.add(new Paragraph("Número de productos: " + ventaTabla.getNumProductos(), FONT_NORMAL));
        documento.add(new Paragraph(" "));
        
        // Datos del cliente
        documento.add(new Paragraph("DATOS DEL CLIENTE", FONT_SUBTITULO));
        if (venta.getCliente() != null) {
            documento.add(new Paragraph("Nombre: " + venta.getCliente().getNombre(), FONT_NORMAL));
            documento.add(new Paragraph("Teléfono: " + venta.getCliente().getTelefono(), FONT_NORMAL));
            documento.add(new Paragraph("Correo: " + venta.getCliente().getCorreo(), FONT_NORMAL));
            documento.add(new Paragraph("Dirección: " + venta.getCliente().getDireccion(), FONT_NORMAL));
        } else {
            documento.add(new Paragraph("Cliente: Venta sin cliente registrado", FONT_NORMAL));
        }
        documento.add(new Paragraph(" "));
        
        // Tabla de detalles de venta
        documento.add(new Paragraph("DETALLE DE PRODUCTOS", FONT_SUBTITULO));
        
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setSpacingAfter(10f);
        
        // Establecer anchos relativos de las columnas
        float[] anchos = {1f, 3f, 1f, 1.5f, 1.5f};
        tabla.setWidths(anchos);
        
        // Encabezados de la tabla
        agregarEncabezadoTabla(tabla, new String[]{"ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal"});
        
        // Datos de la tabla
        double totalVenta = 0.0;
        for (DetalleVenta detalle : detalles) {
            tabla.addCell(String.valueOf(detalle.getBebida().getIdBebida()));
            tabla.addCell(detalle.getBebida().getBebida());
            tabla.addCell(String.valueOf(detalle.getCantidad()));
            tabla.addCell("$" + String.format("%.2f", detalle.getPrecioBebida()));
            tabla.addCell("$" + String.format("%.2f", detalle.getTotal()));
            
            totalVenta += detalle.getTotal();
        }
        
        documento.add(tabla);
        
        // Total
        Paragraph parrafoTotal = new Paragraph("TOTAL: $" + String.format("%.2f", totalVenta), FONT_NEGRITA);
        parrafoTotal.setAlignment(Element.ALIGN_RIGHT);
        documento.add(parrafoTotal);
        
        // Pie de página
        agregarPiePagina(documento);
        
        documento.close();
        
        return fileName;
    }
    
    /**
     * Genera un reporte PDF para ventas por periodo
     * @param ventas Lista de ventas en el periodo
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteVentasPorPeriodo(List<ReporteVenta> ventas, 
            java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) throws Exception {
        String fileName = "Reporte_Ventas_Periodo_" + fechaInicio + "_a_" + fechaFin + ".pdf";
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(fileName));
        documento.open();
        
        // Agregar logo (opcional)
        agregarLogo(documento);
        
        // Título del reporte
        agregarTitulo(documento, "REPORTE DE VENTAS POR PERIODO");
        
        // Información del periodo
        documento.add(new Paragraph("Periodo: " + fechaInicio + " a " + fechaFin, FONT_NORMAL));
        documento.add(new Paragraph("Total de ventas: " + ventas.size(), FONT_NORMAL));
        
        // Calcular total de ingresos
        double totalIngresos = ventas.stream().mapToDouble(ReporteVenta::getTotalVenta).sum();
        documento.add(new Paragraph("Total de ingresos: $" + String.format("%.2f", totalIngresos), FONT_NEGRITA));
        documento.add(new Paragraph(" "));
        
        // Tabla de ventas
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setSpacingAfter(10f);
        
        // Establecer anchos relativos de las columnas
        float[] anchos = {1f, 2f, 2f, 3f, 2f};
        tabla.setWidths(anchos);
        
        // Encabezados de la tabla
        agregarEncabezadoTabla(tabla, new String[]{"ID", "Fecha", "Folio", "Cliente", "Total"});
        
        // Datos de la tabla
        for (ReporteVenta venta : ventas) {
            tabla.addCell(String.valueOf(venta.getIdVenta()));
            tabla.addCell(venta.getFecha().toString());
            tabla.addCell(venta.getFolioFactura() != null ? venta.getFolioFactura() : "N/A");
            tabla.addCell(venta.getCliente() != null ? venta.getCliente() : "Sin cliente");
            tabla.addCell("$" + String.format("%.2f", venta.getTotalVenta()));
        }
        
        documento.add(tabla);
        
        // Pie de página
        agregarPiePagina(documento);
        
        documento.close();
        
        return fileName;
    }
    
    /**
     * Genera un reporte PDF para ventas por producto
     * @param productos Lista de productos con sus ventas
     * @param titulo Título del reporte
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteVentasProducto(List<ReporteProducto> productos, String titulo) throws Exception {
        String fileName = "Reporte_" + titulo.replace(" ", "_") + ".pdf";
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(fileName));
        documento.open();
        
        // Agregar logo (opcional)
        agregarLogo(documento);
        
        // Título del reporte
        agregarTitulo(documento, titulo.toUpperCase());
        
        // Información general
        documento.add(new Paragraph("Total de productos: " + productos.size(), FONT_NORMAL));
        
        // Calcular total de ingresos
        double totalIngresos = productos.stream().mapToDouble(ReporteProducto::getTotalRecaudado).sum();
        documento.add(new Paragraph("Total recaudado: $" + String.format("%.2f", totalIngresos), FONT_NEGRITA));
        documento.add(new Paragraph(" "));
        
        // Tabla de productos
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setSpacingAfter(10f);
        
        // Establecer anchos relativos de las columnas
        float[] anchos = {1f, 3f, 2f, 2f};
        tabla.setWidths(anchos);
        
        // Encabezados de la tabla
        agregarEncabezadoTabla(tabla, new String[]{"ID", "Producto", "Cantidad vendida", "Total recaudado"});
        
        // Datos de la tabla
        for (ReporteProducto producto : productos) {
            tabla.addCell(String.valueOf(producto.getIdBebida()));
            tabla.addCell(producto.getNombreBebida());
            tabla.addCell(String.valueOf(producto.getCantidadVendida()));
            tabla.addCell("$" + String.format("%.2f", producto.getTotalRecaudado()));
        }
        
        documento.add(tabla);
        
        // Pie de página
        agregarPiePagina(documento);
        
        documento.close();
        
        return fileName;
    }
    
    /**
     * Genera un reporte PDF para productos con stock mínimo
     * @param productos Lista de productos con stock mínimo
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteStockMinimo(List<ProductoStockMinimo> productos) throws Exception {
        String fileName = "Reporte_Stock_Minimo.pdf";
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(fileName));
        documento.open();
        
        // Agregar logo (opcional)
        agregarLogo(documento);
        
        // Título del reporte
        agregarTitulo(documento, "REPORTE DE PRODUCTOS CON STOCK MÍNIMO");
        
        // Información general
        documento.add(new Paragraph("Total de productos con stock mínimo: " + productos.size(), FONT_NORMAL));
        documento.add(new Paragraph(" "));
        
        // Tabla de productos
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setSpacingAfter(10f);
        
        // Establecer anchos relativos de las columnas
        float[] anchos = {1f, 3f, 1.5f, 1.5f, 1.5f, 1.5f};
        tabla.setWidths(anchos);
        
        // Encabezados de la tabla
        agregarEncabezadoTabla(tabla, new String[]{"ID", "Producto", "Stock actual", "Stock mínimo", "Diferencia", "Precio"});
        
        // Datos de la tabla
        for (ProductoStockMinimo producto : productos) {
            tabla.addCell(String.valueOf(producto.getIdBebida()));
            tabla.addCell(producto.getNombreBebida());
            tabla.addCell(String.valueOf(producto.getStock()));
            tabla.addCell(String.valueOf(producto.getStockMinimo()));
            tabla.addCell(String.valueOf(producto.getDiferencia()));
            tabla.addCell("$" + String.format("%.2f", producto.getPrecio()));
        }
        
        documento.add(tabla);
        
        // Pie de página
        agregarPiePagina(documento);
        
        documento.close();
        
        return fileName;
    }
    
    /**
     * Genera un reporte PDF para productos no vendidos a un cliente
     * @param productos Lista de productos no vendidos
     * @param nombreCliente Nombre del cliente
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteProductosNoVendidos(List<Bebida> productos, String nombreCliente) throws Exception {
        String fileName = "Reporte_Productos_No_Vendidos_" + nombreCliente.replace(" ", "_") + ".pdf";
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(fileName));
        documento.open();
        
        // Agregar logo (opcional)
        agregarLogo(documento);
        
        // Título del reporte
        agregarTitulo(documento, "REPORTE DE PRODUCTOS NO VENDIDOS");
        
        // Información general
        documento.add(new Paragraph("Cliente: " + nombreCliente, FONT_NORMAL));
        documento.add(new Paragraph("Total de productos no vendidos: " + productos.size(), FONT_NORMAL));
        documento.add(new Paragraph(" "));
        
        // Tabla de productos
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setSpacingAfter(10f);
        
        // Establecer anchos relativos de las columnas
        float[] anchos = {1f, 3f, 1.5f, 1.5f};
        tabla.setWidths(anchos);
        
        // Encabezados de la tabla
        agregarEncabezadoTabla(tabla, new String[]{"ID", "Producto", "Stock", "Precio"});
        
        // Datos de la tabla
        for (Bebida producto : productos) {
            tabla.addCell(String.valueOf(producto.getIdBebida()));
            tabla.addCell(producto.getBebida());
            tabla.addCell(String.valueOf(producto.getStock()));
            tabla.addCell("$" + String.format("%.2f", producto.getPrecio()));
        }
        
        documento.add(tabla);
        
        // Pie de página
        agregarPiePagina(documento);
        
        documento.close();
        
        return fileName;
    }
    
    // Métodos auxiliares
    
    private static void agregarLogo(Document documento) {
        try {
            Image logo = Image.getInstance(GeneradorReportesPDF.class.getResource("/javafxexpendio/recursos/logo.png"));
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            documento.add(logo);
        } catch (Exception e) {
            // Si no hay logo, continuar sin él
        }
    }
    
    private static void agregarTitulo(Document documento, String titulo) throws DocumentException {
        Paragraph parrafoTitulo = new Paragraph(titulo, FONT_TITULO);
        parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
        parrafoTitulo.setSpacingAfter(20);
        documento.add(parrafoTitulo);
    }
    
    private static void agregarEncabezadoTabla(PdfPTable tabla, String[] encabezados) {
        PdfPCell celdaEncabezado = new PdfPCell();
        celdaEncabezado.setBackgroundColor(BaseColor.LIGHT_GRAY);
        celdaEncabezado.setPadding(5);
        
        Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        
        for (String encabezado : encabezados) {
            celdaEncabezado.setPhrase(new Phrase(encabezado, fontEncabezado));
            tabla.addCell(celdaEncabezado);
        }
    }
    
    private static void agregarPiePagina(Document documento) throws DocumentException {
        Paragraph piePagina = new Paragraph("Reporte generado el " + 
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), FONT_PIE_PAGINA);
        piePagina.setAlignment(Element.ALIGN_CENTER);
        documento.add(piePagina);
    }
    
    /**
     * Abre el archivo PDF generado
     * @param rutaArchivo Ruta del archivo PDF
     */
    public static void abrirArchivoPDF(String rutaArchivo) {
        try {
            File pdfFile = new File(rutaArchivo);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                            "Reporte generado", 
                            "El reporte se ha guardado como: " + pdfFile.getAbsolutePath());
                }
            }
        } catch (IOException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                    "Reporte generado", 
                    "El reporte se ha guardado como: " + rutaArchivo);
        }
    }
}