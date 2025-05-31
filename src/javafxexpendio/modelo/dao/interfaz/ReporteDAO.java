/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.ProductoStockMinimo;
import javafxexpendio.modelo.pojo.ReporteProducto;
import javafxexpendio.modelo.pojo.ReporteVenta;

/**
 *
 * @author Dell
 */
public interface ReporteDAO {
    
    List<ReporteVenta> obtenerVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException;
    List<ReporteProducto> obtenerVentasPorProducto() throws SQLException;
    List<ProductoStockMinimo> obtenerProductosStockMinimo() throws SQLException;
    ReporteProducto obtenerProductoMasVendido() throws SQLException;
    ReporteProducto obtenerProductoMenosVendido() throws SQLException;
    List<Bebida> obtenerProductosNoVendidosACliente(int idCliente) throws SQLException;
    ReporteProducto obtenerProductoMasVendidoACliente(int idCliente) throws SQLException;
    
}
