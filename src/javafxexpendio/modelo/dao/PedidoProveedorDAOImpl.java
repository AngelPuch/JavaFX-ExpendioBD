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
import javafxexpendio.modelo.pojo.DetallePedidoProveedor;
import javafxexpendio.modelo.pojo.PedidoProveedor;
import javafxexpendio.modelo.pojo.ProductoStockMinimo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Dell
 */
public class PedidoProveedorDAOImpl implements PedidoProveedorDAO {
    
    @Override
    public boolean registrarPedidoProveedor(LocalDate fecha, int idProveedor, 
            String observaciones, List<Map<String, Object>> detallesPedido, PedidoProveedor pedidoResultado) throws SQLException {

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

            if (idPedidoProveedor != null && idPedidoProveedor > 0) {
                // Si se proporcionó un objeto para recibir el resultado, actualizarlo
                if (pedidoResultado != null) {
                    pedidoResultado.setIdPedidoProveedor(idPedidoProveedor);
                    pedidoResultado.setFecha(fecha);
                    pedidoResultado.setIdProveedor(idProveedor);
                    pedidoResultado.setObservaciones(observaciones);
                    // Otros campos que necesites establecer
                }
                return true;
            } else {
                throw new SQLException("No se pudo registrar el pedido: " + mensaje);
            }

        } catch (SQLException ex) {
            throw new SQLException("Error al registrar el pedido a proveedor: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public ObservableList<PedidoProveedor> obtenerPedidosPendientes() throws SQLException {
        ObservableList<PedidoProveedor> pedidos = FXCollections.observableArrayList();
        String consulta = "SELECT * FROM vista_pedidos_pendientes";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PedidoProveedor pedido = new PedidoProveedor();
                pedido.setIdPedidoProveedor(rs.getInt("idPedidoProveedor"));
                pedido.setFecha(rs.getDate("fecha").toLocalDate());
                pedido.setProveedor(rs.getString("proveedor"));
                pedido.setEstado(rs.getString("estado"));
                pedido.setObservaciones(rs.getString("observaciones"));
                pedido.setTotalProductos(rs.getInt("total_productos"));
                pedido.setTotalUnidades(rs.getInt("total_unidades"));
                pedido.setTotalEstimado(rs.getDouble("total_estimado"));
                
                pedidos.add(pedido);
            }
            
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener pedidos pendientes: " + ex.getMessage());
        }
        
        return pedidos;
    }
    
    @Override
    public ObservableList<DetallePedidoProveedor> obtenerDetallePedidoProveedor(int idPedidoProveedor) throws SQLException {
        ObservableList<DetallePedidoProveedor> detalles = FXCollections.observableArrayList();
        String consulta = "SELECT * FROM vista_detalle_pedidos_proveedor WHERE idPedidoProveedor = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {

            ps.setInt(1, idPedidoProveedor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedidoProveedor detalle = new DetallePedidoProveedor();
                    detalle.setIdPedidoProveedor(rs.getInt("idPedidoProveedor"));
                    detalle.setIdBebida(rs.getInt("idBebida"));
                    detalle.setBebida(rs.getString("bebida"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioEstimado(rs.getDouble("precio_estimado"));
                    detalle.setSubtotal(rs.getDouble("subtotal"));

                    detalles.add(detalle);
                }
            }

        } catch (SQLException ex) {
            throw new SQLException("Error al obtener detalle del pedido: " + ex.getMessage());
        }

        return detalles;
    }
    
    @Override
    public List<ProductoStockMinimo> obtenerProductosStockMinimo() throws SQLException {
        List<ProductoStockMinimo> listaProductos = new ArrayList<>();
        String consulta = "SELECT * FROM vista_productos_stock_minimo";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ProductoStockMinimo producto = new ProductoStockMinimo();
                producto.setIdBebida(rs.getInt("idBebida"));
                producto.setNombreBebida(rs.getString("bebida") + " " + rs.getString("contenido_neto"));
                producto.setStock(rs.getInt("stock"));
                producto.setStockMinimo(rs.getInt("stock_minimo"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setDiferencia(rs.getInt("diferencia"));
                listaProductos.add(producto);
            }
            return listaProductos;
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener los productos con stock mínimo: " + ex.getMessage());
        }
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