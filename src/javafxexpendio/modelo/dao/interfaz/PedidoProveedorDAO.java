/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafxexpendio.modelo.pojo.DetallePedidoProveedor;
import javafxexpendio.modelo.pojo.PedidoProveedor;
import javafxexpendio.modelo.pojo.ProductoStockMinimo;

/**
 *
 * @author Dell
 */
public interface PedidoProveedorDAO {
   
    boolean registrarPedidoProveedor(LocalDate fecha, int idProveedor, 
            String observaciones, List<Map<String, Object>> detallesPedido, PedidoProveedor pedidoResultado) throws SQLException;
    
    ObservableList<PedidoProveedor> obtenerPedidosPendientes() throws SQLException;

    ObservableList<DetallePedidoProveedor> obtenerDetallePedidoProveedor(int idPedidoProveedor) throws SQLException;
    

    List<ProductoStockMinimo> obtenerProductosStockMinimo() throws SQLException;
    
    boolean cancelarPedidoProveedor(int idPedidoProveedor) throws SQLException;
}
