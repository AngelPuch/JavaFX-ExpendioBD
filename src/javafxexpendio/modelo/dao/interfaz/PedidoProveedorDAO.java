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

/**
 *
 * @author Dell
 */
public interface PedidoProveedorDAO {
   
    Map<String, Object> registrarPedidoProveedor(LocalDate fecha, int idProveedor, 
            String observaciones, List<Map<String, Object>> detallesPedido) throws SQLException;
    
    ObservableList<Map<String, Object>> obtenerPedidosPendientes() throws SQLException;

    ObservableList<Map<String, Object>> obtenerDetallePedidoProveedor(int idPedidoProveedor) throws SQLException;
    

    ObservableList<Map<String, Object>> obtenerProductosStockMinimo() throws SQLException;
    
    boolean cancelarPedidoProveedor(int idPedidoProveedor) throws SQLException;
}
