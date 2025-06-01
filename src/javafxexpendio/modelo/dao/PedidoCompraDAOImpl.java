/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.dao;
 import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.dao.interfaz.PedidoCompraDAO;
import javafxexpendio.modelo.pojo.DetallePedidoProveedor;
import javafxexpendio.modelo.pojo.PedidoProveedor; 

/**
 *
 * @author Dell
 */
public class PedidoCompraDAOImpl implements PedidoCompraDAO{

    @Override
    public PedidoProveedor crear(PedidoProveedor pedido) throws SQLException {
        String consulta = "INSERT INTO pedido_proveedor (fecha, idProveedor, idEstadoPedido, observaciones) VALUES (?, ?, ?, ?)";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setObject(1, pedido.getFecha());
            ps.setInt(2, pedido.getIdProveedor());
            ps.setInt(3, pedido.getIdEstadoPedido());
            ps.setString(4, pedido.getObservaciones());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedido.setIdPedidoProveedor(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el pedido: " + ex.getMessage());
        }
        
        return pedido;
    }

    @Override
    public PedidoProveedor leer(Integer id) throws SQLException {
        PedidoProveedor pedido = null;
        String consulta = "SELECT pp.idPedidoProveedor, pp.fecha, pp.idProveedor, p.razon_social AS proveedor, "
                + "pp.idEstadoPedido, ep.estado, pp.observaciones "
                + "FROM pedido_proveedor pp "
                + "JOIN proveedor p ON pp.idProveedor = p.idProveedor "
                + "JOIN estado_pedido ep ON pp.idEstadoPedido = ep.idEstadoPedido "
                + "WHERE pp.idPedidoProveedor = ?";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedido = convertirRegistroPedido(rs);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return pedido;
    }

    @Override
    public List<PedidoProveedor> leerTodo() throws SQLException {
        List<PedidoProveedor> pedidos = new ArrayList<>();
        String consulta = "SELECT pp.idPedidoProveedor, pp.fecha, pp.idProveedor, p.razon_social AS proveedor, "
                + "pp.idEstadoPedido, ep.estado, pp.observaciones "
                + "FROM pedido_proveedor pp "
                + "JOIN proveedor p ON pp.idProveedor = p.idProveedor "
                + "JOIN estado_pedido ep ON pp.idEstadoPedido = ep.idEstadoPedido "
                + "ORDER BY pp.fecha DESC";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta);
                ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PedidoProveedor pedido = convertirRegistroPedido(rs);
                pedidos.add(pedido);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return pedidos;
    }

    @Override
    public List<PedidoProveedor> obtenerPedidosPendientesPorProveedor(int idProveedor) throws SQLException {
        List<PedidoProveedor> pedidos = new ArrayList<>();
        String consulta = "SELECT pp.idPedidoProveedor, pp.fecha, pp.idProveedor, p.razon_social AS proveedor, "
                + "pp.idEstadoPedido, ep.estado, pp.observaciones, "
                + "COUNT(DISTINCT dpp.idBebida) AS totalProductos, "
                + "SUM(dpp.cantidad) AS totalUnidades, "
                + "SUM(dpp.cantidad * dpp.precio_estimado) AS totalEstimado "
                + "FROM pedido_proveedor pp "
                + "JOIN proveedor p ON pp.idProveedor = p.idProveedor "
                + "JOIN estado_pedido ep ON pp.idEstadoPedido = ep.idEstadoPedido "
                + "JOIN detalle_pedido_proveedor dpp ON pp.idPedidoProveedor = dpp.idPedidoProveedor "
                + "WHERE pp.idProveedor = ? AND pp.idEstadoPedido = 1 " // En espera
                + "GROUP BY pp.idPedidoProveedor";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta)) {
            
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PedidoProveedor pedido = convertirRegistroPedidoCompleto(rs);
                    pedidos.add(pedido);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return pedidos;
    }

    @Override
    public List<DetallePedidoProveedor> obtenerDetallePedidoProveedor(int idPedidoProveedor) throws SQLException {
        List<DetallePedidoProveedor> detalles = new ArrayList<>();
        String consulta = "SELECT dpp.idPedidoProveedor, dpp.idBebida, b.bebida, dpp.cantidad, dpp.precio_estimado "
                + "FROM detalle_pedido_proveedor dpp "
                + "JOIN bebida b ON dpp.idBebida = b.idBebida "
                + "WHERE dpp.idPedidoProveedor = ?";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta)) {
            
            ps.setInt(1, idPedidoProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedidoProveedor detalle = convertirRegistroDetallePedido(rs);
                    detalles.add(detalle);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return detalles;
    }

    @Override
    public boolean actualizarEstadoPedido(int idPedidoProveedor, int idEstadoPedido) throws SQLException {
        String consulta = "UPDATE pedido_proveedor SET idEstadoPedido = ? WHERE idPedidoProveedor = ?";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta)) {
            
            ps.setInt(1, idEstadoPedido);
            ps.setInt(2, idPedidoProveedor);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar el estado del pedido: " + ex.getMessage());
        }
    }
    
    private PedidoProveedor convertirRegistroPedido(ResultSet rs) throws SQLException {
        PedidoProveedor pedido = new PedidoProveedor();
        pedido.setIdPedidoProveedor(rs.getInt("idPedidoProveedor"));
        pedido.setFecha(rs.getDate("fecha").toLocalDate());
        pedido.setIdProveedor(rs.getInt("idProveedor"));
        pedido.setProveedor(rs.getString("proveedor"));
        pedido.setIdEstadoPedido(rs.getInt("idEstadoPedido"));
        pedido.setEstado(rs.getString("estado"));
        pedido.setObservaciones(rs.getString("observaciones"));
        return pedido;
    }
    
    private PedidoProveedor convertirRegistroPedidoCompleto(ResultSet rs) throws SQLException {
        PedidoProveedor pedido = convertirRegistroPedido(rs);
        pedido.setTotalProductos(rs.getInt("totalProductos"));
        pedido.setTotalUnidades(rs.getInt("totalUnidades"));
        pedido.setTotalEstimado(rs.getDouble("totalEstimado"));
        return pedido;
    }
    
    private DetallePedidoProveedor convertirRegistroDetallePedido(ResultSet rs) throws SQLException {
        DetallePedidoProveedor detalle = new DetallePedidoProveedor();
        detalle.setIdPedidoProveedor(rs.getInt("idPedidoProveedor"));
        detalle.setIdBebida(rs.getInt("idBebida"));
        detalle.setBebida(rs.getString("bebida"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioEstimado(rs.getDouble("precio_estimado"));
        return detalle;
    }
}
