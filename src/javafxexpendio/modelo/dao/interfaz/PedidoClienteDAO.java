package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PedidoClienteDAO {

    Map<String, Object> registrarPedidoCliente(int idCliente, LocalDate fechaLimite, List<Map<String, Object>> detallesPedido) throws SQLException;

    boolean cancelarPedidoCliente(int idPedido) throws SQLException;

    List<Map<String, Object>> obtenerPedidosPendientes() throws SQLException;

    List<Map<String, Object>> obtenerDetallePedido(int idPedido) throws SQLException;

    Map<String, Object> generarVentaDesdePedido(int idPedido, LocalDate fechaVenta, String folioFactura) throws SQLException;
}