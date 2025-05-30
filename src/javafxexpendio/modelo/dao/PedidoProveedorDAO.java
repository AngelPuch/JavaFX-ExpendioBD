/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;

/**
 *
 * @author Dell
 */
public interface PedidoProveedorDAO {
    
    /**
     * Registra un pedido a proveedor completo con sus detalles
     * @param fecha Fecha del pedido
     * @param idProveedor ID del proveedor
     * @param observaciones Observaciones del pedido
     * @param detallesPedido Lista de detalles del pedido (idBebida, cantidad, precioEstimado)
     * @return Mapa con los resultados (exito, idPedidoProveedor, mensaje)
     * @throws SQLException Si ocurre un error en la base de datos
     */
    Map<String, Object> registrarPedidoProveedor(LocalDate fecha, int idProveedor, 
            String observaciones, List<Map<String, Object>> detallesPedido) throws SQLException;
    
    /**
     * Obtiene todos los pedidos pendientes (en estado "En espera")
     * @return Lista observable de pedidos pendientes
     * @throws SQLException Si ocurre un error en la base de datos
     */
    ObservableList<Map<String, Object>> obtenerPedidosPendientes() throws SQLException;
    
    /**
     * Obtiene el detalle de un pedido a proveedor específico
     * @param idPedidoProveedor ID del pedido a proveedor
     * @return Lista observable de detalles del pedido
     * @throws SQLException Si ocurre un error en la base de datos
     */
    ObservableList<Map<String, Object>> obtenerDetallePedidoProveedor(int idPedidoProveedor) throws SQLException;
    
    /**
     * Obtiene todos los productos con stock mínimo
     * @return Lista observable de productos con stock mínimo
     * @throws SQLException Si ocurre un error en la base de datos
     */
    ObservableList<Map<String, Object>> obtenerProductosStockMinimo() throws SQLException;
    
    /**
     * Cancela un pedido a proveedor
     * @param idPedidoProveedor ID del pedido a proveedor
     * @return true si se canceló correctamente, false en caso contrario
     * @throws SQLException Si ocurre un error en la base de datos
     */
    boolean cancelarPedidoProveedor(int idPedidoProveedor) throws SQLException;
}
