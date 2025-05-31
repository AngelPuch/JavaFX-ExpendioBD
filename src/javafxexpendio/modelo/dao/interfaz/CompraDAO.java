package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafxexpendio.modelo.pojo.Compra;
import javafxexpendio.modelo.pojo.DetalleCompra;

public interface CompraDAO{
    
    Compra crear(Compra compra) throws SQLException;
    Compra leer(Integer id) throws SQLException;
    List<Compra> leerTodo() throws SQLException;
    Map<String, Object> registrarCompra(Compra compra, List<DetalleCompra> detalles, int idPedidoProveedor) throws SQLException;
    
    List<DetalleCompra> obtenerDetalleCompra(int idCompra) throws SQLException;
}