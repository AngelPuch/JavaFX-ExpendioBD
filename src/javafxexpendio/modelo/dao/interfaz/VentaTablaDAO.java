package javafxexpendio.modelo.dao.interfaz;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.modelo.pojo.Venta;
import javafxexpendio.modelo.pojo.VentaTabla;

public interface VentaTablaDAO {
    
    ArrayList<VentaTabla> obtenerTodasLasVentasTabla() throws SQLException;
    Venta obtenerVentaPorId(Integer idVenta) throws SQLException;
    ArrayList<VentaTabla> obtenerVentasPorRangoFechasTabla(Date fechaInicio, Date fechaFin) throws SQLException;
    String obtenerProductoMasVendido() throws SQLException;
    ArrayList<DetalleVenta> obtenerDetallesVenta(Integer idVenta) throws SQLException;
    
}
