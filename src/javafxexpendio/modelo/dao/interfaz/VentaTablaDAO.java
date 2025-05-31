/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.modelo.pojo.Venta;
import javafxexpendio.modelo.pojo.VentaTabla;

/**
 *
 * @author Dell
 */
public interface VentaTablaDAO {
    
    ArrayList<VentaTabla> obtenerTodasLasVentasTabla() throws SQLException;
    Venta obtenerVentaPorId(Integer idVenta) throws SQLException;
    ArrayList<VentaTabla> obtenerVentasPorRangoFechasTabla(Date fechaInicio, Date fechaFin) throws SQLException;
    String obtenerProductoMasVendido() throws SQLException;
    ArrayList<DetalleVenta> obtenerDetallesVenta(Integer idVenta) throws SQLException;
    
}
