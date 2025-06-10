package javafxexpendio.modelo.dao;

import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.dao.interfaz.PedidoClienteDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.sql.Date;


public class PedidoClienteDAOImpl implements PedidoClienteDAO {

    @Override
    public Map<String, Object> registrarPedidoCliente(int idCliente, LocalDate fechaLimite, List<Map<String, Object>> detallesPedido) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();

        org.json.simple.JSONArray detallesJSON = new org.json.simple.JSONArray();
        for (Map<String, Object> detalle : detallesPedido) {
            org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
            obj.put("idBebida", detalle.get("idBebida"));
            obj.put("cantidad", detalle.get("cantidad"));
            detallesJSON.add(obj);
        }

        try (Connection con = ConexionBD.abrirConexion();
             CallableStatement cs = con.prepareCall("{CALL sp_registrar_pedido_cliente(?, ?, ?, ?, ?)}")) {

            cs.setInt(1, idCliente);
            cs.setDate(2, Date.valueOf(fechaLimite));
            cs.setString(3, detallesJSON.toJSONString());
            cs.registerOutParameter(4, Types.INTEGER);
            cs.registerOutParameter(5, Types.VARCHAR);

            cs.execute();

            Integer idPedido = cs.getInt(4);
            String mensaje = cs.getString(5);

            resultado.put("exito", idPedido != null && idPedido > 0);
            resultado.put("idPedido", idPedido);
            resultado.put("mensaje", mensaje);

        } catch (SQLException ex) {
            throw new SQLException("Error al registrar pedido: " + ex.getMessage());
        }

        return resultado;
    }

    @Override
    public Map<String, Object> cancelarPedidoCliente(int idPedido) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();

        try (Connection con = ConexionBD.abrirConexion();
             CallableStatement cs = con.prepareCall("{CALL sp_cancelar_pedido_cliente(?, ?)}")) {

            cs.setInt(1, idPedido);
            cs.registerOutParameter(2, Types.VARCHAR);

            cs.execute();

            String mensaje = cs.getString(2);

            resultado.put("exito", mensaje.toLowerCase().contains("cancelado"));
            resultado.put("mensaje", mensaje);

        } catch (SQLException ex) {
            throw new SQLException("Error al cancelar pedido: " + ex.getMessage());
        }

        return resultado;
    }

    @Override
    public List<Map<String, Object>> obtenerPedidosPendientes() throws SQLException {
        List<Map<String, Object>> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM vista_pedidos_pendientes ORDER BY fechaCreacion DESC";

        try (Connection con = ConexionBD.abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> pedido = new HashMap<>();
                pedido.put("idPedidoCliente", rs.getInt("idPedidoCliente"));
                pedido.put("fechaCreacion", rs.getDate("fechaCreacion").toLocalDate());
                pedido.put("cliente", rs.getString("cliente"));
                pedido.put("fecha_limite", rs.getDate("fecha_limite").toLocalDate());
                pedido.put("estado", rs.getString("estado"));
                pedido.put("total_productos", rs.getInt("total_productos"));
                pedido.put("total_unidades", rs.getInt("total_unidades"));
                pedido.put("total_estimado", rs.getDouble("total_estimado"));
                pedidos.add(pedido);
            }

        } catch (SQLException ex) {
            throw new SQLException("Error al obtener pedidos pendientes: " + ex.getMessage());
        }

        return pedidos;
    }

    @Override
    public List<Map<String, Object>> obtenerDetallePedido(int idPedido) throws SQLException {
        List<Map<String, Object>> detalles = new ArrayList<>();

        String sql = "SELECT * FROM vista_detalle_pedido_cliente WHERE idPedidoCliente = ?";

        try (Connection con = ConexionBD.abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("idBebida", rs.getInt("idBebida"));
                    detalle.put("bebida", rs.getString("bebida"));
                    detalle.put("cantidad", rs.getInt("cantidad"));
                    detalle.put("precio_bebida", rs.getDouble("precio_bebida"));
                    detalle.put("subtotal", rs.getDouble("subtotal"));
                    detalles.add(detalle);
                }
            }

        } catch (SQLException ex) {
            throw new SQLException("Error al obtener detalle del pedido: " + ex.getMessage());
        }

        return detalles;
    }

    @Override
    public Map<String, Object> generarVentaDesdePedido(int idPedido, LocalDate fechaVenta, String folioFactura) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();

        List<Map<String, Object>> detallesPedido = obtenerDetallePedido(idPedido);

        int idCliente = 0;
        String sqlCliente = "SELECT idCliente FROM pedido_cliente WHERE idPedidoCliente = ?";

        try (Connection con = ConexionBD.abrirConexion();
             PreparedStatement ps = con.prepareStatement(sqlCliente)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idCliente = rs.getInt("idCliente");
                }
            }

        } catch (SQLException ex) {
            throw new SQLException("Error al obtener cliente del pedido: " + ex.getMessage());
        }

        VentaDAOImpl ventaDAO = new VentaDAOImpl();

        List<Map<String, Object>> detallesVenta = new ArrayList<>();
        for (Map<String, Object> d : detallesPedido) {
            Map<String, Object> detalleVenta = new HashMap<>();
            detalleVenta.put("idBebida", d.get("idBebida"));
            detalleVenta.put("cantidad", d.get("cantidad"));
            detallesVenta.add(detalleVenta);
        }

        resultado = ventaDAO.registrarVenta(idCliente, fechaVenta, folioFactura, detallesVenta);
        
        // Obtener el idVenta generado
        Integer idVenta = (Integer) resultado.get("idVenta");
        if (idVenta != null && idVenta > 0) {
            // Actualizar el pedido con el idVenta
            boolean actualizado = actualizarIdVentaEnPedido(idPedido, idVenta);
            if (!actualizado) {
                throw new SQLException("No se pudo actualizar el idVenta en el pedido.");
            }
        } else {
            throw new SQLException("No se obtuvo el idVenta generado.");
        }
        
        return resultado;
    }
    
    public boolean actualizarEstadoPedido(int idPedido, int nuevoEstado) throws SQLException {
        String sql = "UPDATE pedido_cliente SET idEstadoPedido = ? WHERE idPedidoCliente = ?";
        try (Connection con = ConexionBD.abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idPedido);
            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }
    
    public boolean actualizarIdVentaEnPedido(int idPedido, int idVenta) throws SQLException {
        String sql = "UPDATE pedido_cliente SET venta_idVenta = ? WHERE idPedidoCliente = ?";
        try (Connection con = ConexionBD.abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idPedido);
            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }
}