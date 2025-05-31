/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package javafxexpendio.modelo.dao;

import javafxexpendio.modelo.dao.interfaz.PedidoProveedorDAO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafxexpendio.modelo.ConexionBD;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Dell
 */
public class PedidoProveedorDAOImpl implements PedidoProveedorDAO {
    
    @Override
    public Map<String, Object> registrarPedidoProveedor(LocalDate fecha, int idProveedor, 
            String observaciones, List<Map<String, Object>> detallesPedido) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        
        // Convertir la lista de detalles a formato JSON
        JSONArray detallesJSON = new JSONArray();
        
        for (Map<String, Object> detalle : detallesPedido) {
            JSONObject detalleJSON = new JSONObject();
            detalleJSON.put("idBebida", detalle.get("idBebida"));
            detalleJSON.put("cantidad", detalle.get("cantidad"));
            
            // El precio estimado es opcional
            if (detalle.containsKey("precioEstimado")) {
                detalleJSON.put("precio_estimado", detalle.get("precioEstimado"));
            }
            
            detallesJSON.add(detalleJSON);
        }
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             CallableStatement cs = conexionBD.prepareCall("{CALL sp_registrar_pedido_proveedor_completo(?, ?, ?, ?, ?, ?)}")) {
            
            cs.setDate(1, Date.valueOf(fecha));
            cs.setInt(2, idProveedor);
            cs.setString(3, observaciones);
            cs.setString(4, detallesJSON.toJSONString());
            cs.registerOutParameter(5, Types.INTEGER); // idPedidoProveedor
            cs.registerOutParameter(6, Types.VARCHAR); // mensaje
            
            cs.execute();
            
            // Obtener los resultados
            Integer idPedidoProveedor = cs.getInt(5);
            String mensaje = cs.getString(6);
            
            resultado.put("exito", idPedidoProveedor != null && idPedidoProveedor > 0);
            resultado.put("idPedidoProveedor", idPedidoProveedor);
            resultado.put("mensaje", mensaje);
            
        } catch (SQLException ex) {
            throw new SQLException("Error al registrar el pedido a proveedor: " + ex.getMessage());
        }
        
        return resultado;
    }
    
    @Override
    public ObservableList<Map<String, Object>> obtenerPedidosPendientes() throws SQLException {
        ObservableList<Map<String, Object>> pedidos = FXCollections.observableArrayList();
        String consulta = "SELECT * FROM vista_pedidos_pendientes";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> pedido = new HashMap<>();
                pedido.put("idPedidoProveedor", rs.getInt("idPedidoProveedor"));
                pedido.put("fecha", rs.getDate("fecha").toLocalDate());
                pedido.put("proveedor", rs.getString("proveedor"));
                pedido.put("estado", rs.getString("estado"));
                pedido.put("observaciones", rs.getString("observaciones"));
                pedido.put("totalProductos", rs.getInt("total_productos"));
                pedido.put("totalUnidades", rs.getInt("total_unidades"));
                pedido.put("totalEstimado", rs.getDouble("total_estimado"));
                
                pedidos.add(pedido);
            }
            
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener pedidos pendientes: " + ex.getMessage());
        }
        
        return pedidos;
    }
    
    @Override
    public ObservableList<Map<String, Object>> obtenerDetallePedidoProveedor(int idPedidoProveedor) throws SQLException {
        ObservableList<Map<String, Object>> detalles = FXCollections.observableArrayList();
        String consulta = "SELECT * FROM vista_detalle_pedidos_proveedor WHERE idPedidoProveedor = ?";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            
            ps.setInt(1, idPedidoProveedor);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("idPedidoProveedor", rs.getInt("idPedidoProveedor"));
                    detalle.put("idBebida", rs.getInt("idBebida"));
                    detalle.put("bebida", rs.getString("bebida"));
                    detalle.put("cantidad", rs.getInt("cantidad"));
                    detalle.put("precioEstimado", rs.getDouble("precio_estimado"));
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
    public ObservableList<Map<String, Object>> obtenerProductosStockMinimo() throws SQLException {
        ObservableList<Map<String, Object>> productos = FXCollections.observableArrayList();
        String consulta = "SELECT * FROM vista_productos_stock_minimo";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("idBebida", rs.getInt("idBebida"));
                producto.put("nombreBebida", rs.getString("bebida"));
                producto.put("stock", rs.getInt("stock"));
                producto.put("stockMinimo", rs.getInt("stock_minimo"));
                producto.put("precio", rs.getDouble("precio"));
                producto.put("diferencia", rs.getInt("diferencia"));
                
                productos.add(producto);
            }
            
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener productos con stock mínimo: " + ex.getMessage());
        }
        
        return productos;
    }
    
    @Override
    public boolean cancelarPedidoProveedor(int idPedidoProveedor) throws SQLException {
        String consulta = "{CALL sp_cancelar_pedido_proveedor(?)}";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             CallableStatement cs = conexionBD.prepareCall(consulta)) {
            
            cs.setInt(1, idPedidoProveedor);
            cs.execute();
            return true;
            
        } catch (SQLException ex) {
            throw new SQLException("Error al cancelar el pedido: " + ex.getMessage());
        }
    }
}