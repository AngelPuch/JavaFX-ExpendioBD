/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafxexpendio.modelo.pojo.DetallePedidoProveedor;
import javafxexpendio.modelo.pojo.PedidoProveedor;

/**
 *
 * @author Dell
 */
public interface PedidoCompraDAO {
    
    PedidoProveedor crear(PedidoProveedor pedido) throws SQLException;
    PedidoProveedor leer(Integer id) throws SQLException;
    List<PedidoProveedor> leerTodo() throws SQLException;
    List<PedidoProveedor> obtenerPedidosPendientesPorProveedor(int idProveedor) throws SQLException;
    List<DetallePedidoProveedor> obtenerDetallePedidoProveedor(int idPedidoProveedor) throws SQLException;
    boolean actualizarEstadoPedido(int idPedidoProveedor, int idEstadoPedido) throws SQLException;
}
