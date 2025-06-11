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
    
    // --- INICIO DE CAMBIOS ---
    
    // Constante para el nombre del directorio de reportes
    private static final String DIRECTORIO_REPORTES = "reportes";
    
    // --- FIN DE CAMBIOS ---

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
        String nombreBase = "Reporte_Venta_" + venta.getIdVenta() + ".pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase); // <-- CAMBIO
        
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF)); // <-- CAMBIO
        documento.open();

        // ... (el resto del contenido del método es igual)
        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE VENTA");
        documento.add(new Paragraph("DATOS DE LA VENTA", FONT_SUBTITULO));
        documento.add(new Paragraph("Folio: " + (venta.getFolioFactura() != null ? venta.getFolioFactura() : "N/A"), FONT_NORMAL));
        documento.add(new Paragraph("Fecha: " + venta.getFecha(), FONT_NORMAL));
        // ... (demás párrafos y tablas)
        
        // --- Contenido de la tabla (sin cambios) ---
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setSpacingAfter(10f);
        float[] anchosVenta = {0.8f, 2.8f, 1f, 1.5f, 1.5f, 1.5f};
        tabla.setWidths(anchosVenta);
        agregarEncabezadoTabla(tabla, new String[]{"ID", "Producto", "Cantidad", "Precio Unit.", "Precio c/Desc.", "Subtotal"});
        double totalVenta = 0.0;
        for (DetalleVenta detalle : detalles) {
            tabla.addCell(String.valueOf(detalle.getBebida().getIdBebida()));
            tabla.addCell(detalle.getBebida().getBebida());
            tabla.addCell(String.valueOf(detalle.getCantidad()));
            tabla.addCell("$" + String.format("%.2f", detalle.getPrecioBebida()));
            tabla.addCell("$" + String.format("%.2f", detalle.getPrecioConDescuento()));
            tabla.addCell("$" + String.format("%.2f", detalle.getTotal()));
            totalVenta += detalle.getTotal();
        }
        documento.add(tabla);
        Paragraph parrafoTotal = new Paragraph("TOTAL: $" + String.format("%.2f", totalVenta), FONT_NEGRITA);
        parrafoTotal.setAlignment(Element.ALIGN_RIGHT);
        documento.add(parrafoTotal);
        // --- Fin del contenido de la tabla ---

        agregarPiePagina(documento);
        documento.close();

        return archivoPDF.getAbsolutePath(); // <-- CAMBIO
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
        String nombreBase = "Reporte_Ventas_Periodo_" + fechaInicio + "_a_" + fechaFin + ".pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase); // <-- CAMBIO

        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF)); // <-- CAMBIO
        documento.open();
        
        // ... (el resto del contenido del método es igual)
        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE VENTAS POR PERIODO");
        // ... (demás párrafos y tablas)
        
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        float[] anchos = {2f, 2f, 3f, 2f};
        tabla.setWidths(anchos);
        agregarEncabezadoTabla(tabla, new String[]{"Fecha", "Folio", "Cliente", "Total"});
        for (ReporteVenta venta : ventas) {
            tabla.addCell(venta.getFecha().toString());
            tabla.addCell(venta.getFolioFactura() != null ? venta.getFolioFactura() : "N/A");
            tabla.addCell(venta.getCliente() != null ? venta.getCliente() : "Sin cliente");
            tabla.addCell("$" + String.format("%.2f", venta.getTotalVenta()));
        }
        documento.add(tabla);
        
        agregarPiePagina(documento);
        documento.close();
        
        return archivoPDF.getAbsolutePath(); // <-- CAMBIO
    }
    
    /**
     * Genera un reporte PDF para ventas por producto
     * @param productos Lista de productos con sus ventas
     * @param titulo Título del reporte
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteVentasProducto(List<ReporteProducto> productos, String titulo) throws Exception {
        String nombreBase = "Reporte_" + titulo.replace(" ", "_") + ".pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase); // <-- CAMBIO
        
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF)); // <-- CAMBIO
        documento.open();
        
        // ... (el resto del contenido del método es igual)
        agregarLogo(documento);
        agregarTitulo(documento, titulo.toUpperCase());
        // ... (demás párrafos y tablas)
        
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        float[] anchos = {3f, 2f, 2f};
        tabla.setWidths(anchos);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Cantidad vendida", "Total recaudado"});
        for (ReporteProducto producto : productos) {
            tabla.addCell(producto.getNombreBebida());
            tabla.addCell(String.valueOf(producto.getCantidadVendida()));
            tabla.addCell("$" + String.format("%.2f", producto.getTotalRecaudado()));
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        
        return archivoPDF.getAbsolutePath(); // <-- CAMBIO
    }
    
    /**
     * Genera un reporte PDF para productos con stock mínimo
     * @param productos Lista de productos con stock mínimo
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteStockMinimo(List<ProductoStockMinimo> productos) throws Exception {
        String nombreBase = "Reporte_Stock_Minimo.pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase); // <-- CAMBIO
        
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF)); // <-- CAMBIO
        documento.open();
        
        // ... (el resto del contenido del método es igual)
        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE PRODUCTOS CON STOCK MÍNIMO");
        // ... (demás párrafos y tablas)
        
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        float[] anchos = {3f, 1.5f, 1.5f, 1.5f, 1.5f};
        tabla.setWidths(anchos);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Stock actual", "Stock mínimo", "Diferencia", "Precio"});
        for (ProductoStockMinimo producto : productos) {
            tabla.addCell(producto.getNombreBebida());
            tabla.addCell(String.valueOf(producto.getStock()));
            tabla.addCell(String.valueOf(producto.getStockMinimo()));
            tabla.addCell(String.valueOf(producto.getDiferencia()));
            tabla.addCell("$" + String.format("%.2f", producto.getPrecio()));
        }
        documento.add(tabla);
        
        agregarPiePagina(documento);
        documento.close();
        
        return archivoPDF.getAbsolutePath(); // <-- CAMBIO
    }
    
    /**
     * Genera un reporte PDF para productos no vendidos a un cliente
     * @param productos Lista de productos no vendidos
     * @param nombreCliente Nombre del cliente
     * @return Ruta del archivo PDF generado
     * @throws Exception Si ocurre un error al generar el PDF
     */
    public static String generarReporteProductosNoVendidos(List<Bebida> productos, String nombreCliente) throws Exception {
        String nombreBase = "Reporte_Productos_No_Vendidos_" + nombreCliente.replace(" ", "_") + ".pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase); // <-- CAMBIO
        
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF)); // <-- CAMBIO
        documento.open();
        
        // ... (el resto del contenido del método es igual)
        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE PRODUCTOS NO VENDIDOS");
        // ... (demás párrafos y tablas)
        
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        float[] anchos = {3f, 1.5f, 1.5f};
        tabla.setWidths(anchos);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Stock", "Precio"});
        for (Bebida producto : productos) {
            tabla.addCell(producto.getBebida());
            tabla.addCell(String.valueOf(producto.getStock()));
            tabla.addCell("$" + String.format("%.2f", producto.getPrecio()));
        }
        documento.add(tabla);
        
        agregarPiePagina(documento);
        documento.close();
        
        return archivoPDF.getAbsolutePath(); // <-- CAMBIO
    }
    
    // --- INICIO DE CAMBIOS ---
    
    /**
     * Construye la ruta completa para un archivo de reporte, creando el directorio
     * de reportes si no existe.
     * @param nombreBaseArchivo El nombre del archivo PDF.
     * @return Un objeto File apuntando a la ruta completa del archivo.
     */
    private static File obtenerRutaCompleta(String nombreBaseArchivo) {
        File directorio = new File(DIRECTORIO_REPORTES);
        
        // Si el directorio no existe, lo crea.
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        
        return new File(directorio, nombreBaseArchivo);
    }
    
    // --- FIN DE CAMBIOS ---
    
    // Métodos auxiliares (sin cambios)
    
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
                    System.out.println("Abriendo reporte: " + pdfFile.getAbsolutePath());
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