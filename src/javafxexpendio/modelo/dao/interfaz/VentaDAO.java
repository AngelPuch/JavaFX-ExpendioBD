package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javafxexpendio.modelo.pojo.Venta;

public interface VentaDAO {
    
    Map<String, Object> registrarVenta(Integer idCliente, LocalDate fecha, String folioFactura,
                                       List<Map<String, Object>> detallesVenta) throws SQLException;

    List<Map<String, Object>> obtenerPromocionesDisponibles(int idBebida) throws SQLException;
}
