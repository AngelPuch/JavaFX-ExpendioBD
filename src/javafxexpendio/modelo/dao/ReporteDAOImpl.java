/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.ReporteProducto;
import javafxexpendio.modelo.pojo.ReporteStockMinimo;
import javafxexpendio.modelo.pojo.ReporteVenta;

/**
 *
 * @author Dell
 */
public class ReporteDAOImpl {
 
    public List<ReporteVenta> obtenerVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        List<ReporteVenta> listaVentas = new ArrayList<>();
        String consulta = "SELECT v.idVenta, v.fecha, v.folio_factura, c.nombre as cliente, " +
                          "SUM(dv.total) as total_venta " +
                          "FROM venta v " +
                          "LEFT JOIN cliente c ON v.idCliente = c.idCliente " +
                          "JOIN detalle_venta dv ON v.idVenta = dv.idVenta " +
                          "WHERE v.fecha BETWEEN ? AND ? " +
                          "GROUP BY v.idVenta " +
                          "ORDER BY v.fecha DESC";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteVenta venta = new ReporteVenta();
                    venta.setIdVenta(rs.getInt("idVenta"));
                    venta.setFecha(rs.getDate("fecha").toLocalDate());
                    venta.setFolioFactura(rs.getString("folio_factura"));
                    venta.setCliente(rs.getString("cliente"));
                    venta.setTotalVenta(rs.getDouble("total_venta"));
                    listaVentas.add(venta);
                }
            }
            return listaVentas;
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener las ventas por periodo: " + ex.getMessage());
        }
    }

    public List<ReporteProducto> obtenerVentasPorProducto() throws SQLException {
        List<ReporteProducto> listaProductos = new ArrayList<>();
        String consulta = "SELECT * FROM vista_ventas_por_producto ORDER BY cantidad_vendida DESC";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ReporteProducto producto = new ReporteProducto();
                producto.setIdBebida(rs.getInt("idBebida"));
                producto.setNombreBebida(rs.getString("bebida"));
                producto.setCantidadVendida(rs.getInt("cantidad_vendida"));
                producto.setTotalRecaudado(rs.getDouble("total_recaudado"));
                listaProductos.add(producto);
            }
            return listaProductos;
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener las ventas por producto: " + ex.getMessage());
        }
    }
    
    public List<ReporteStockMinimo> obtenerProductosStockMinimo() throws SQLException {
        List<ReporteStockMinimo> listaProductos = new ArrayList<>();
        String consulta = "SELECT * FROM vista_productos_stock_minimo ORDER BY diferencia DESC";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ReporteStockMinimo producto = new ReporteStockMinimo();
                producto.setIdBebida(rs.getInt("idBebida"));
                producto.setNombreBebida(rs.getString("bebida"));
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
    
    public ReporteProducto obtenerProductoMasVendido() throws SQLException {
        ReporteProducto producto = null;
        String consulta = "SELECT * FROM vista_producto_mas_vendido";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                producto = new ReporteProducto();
                producto.setIdBebida(rs.getInt("idBebida"));
                producto.setNombreBebida(rs.getString("bebida"));
                producto.setCantidadVendida(rs.getInt("cantidad_vendida"));
                producto.setTotalRecaudado(rs.getDouble("total_recaudado"));
            }
            return producto;
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener el producto más vendido: " + ex.getMessage());
        }
    }
    
    public ReporteProducto obtenerProductoMenosVendido() throws SQLException {
        ReporteProducto producto = null;
        String consulta = "SELECT * FROM vista_producto_menos_vendido";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                producto = new ReporteProducto();
                producto.setIdBebida(rs.getInt("idBebida"));
                producto.setNombreBebida(rs.getString("bebida"));
                producto.setCantidadVendida(rs.getInt("cantidad_vendida"));
                producto.setTotalRecaudado(rs.getDouble("total_recaudado"));
            }
            return producto;
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener el producto menos vendido: " + ex.getMessage());
        }
    }
    
    public List<Bebida> obtenerProductosNoVendidosACliente(int idCliente) throws SQLException {
        List<Bebida> listaProductos = new ArrayList<>();
        String consulta = "SELECT b.idBebida, b.bebida, b.stock, b.stock_minimo, b.precio " +
                          "FROM bebida b " +
                          "WHERE NOT EXISTS ( " +
                          "    SELECT 1 FROM detalle_venta dv " +
                          "    JOIN venta v ON dv.idVenta = v.idVenta " +
                          "    WHERE dv.idBebida = b.idBebida AND v.idCliente = ? " +
                          ")";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bebida bebida = new Bebida();
                    bebida.setIdBebida(rs.getInt("idBebida"));
                    bebida.setBebida(rs.getString("bebida"));
                    bebida.setStock(rs.getInt("stock"));
                    bebida.setStockMinimo(rs.getInt("stock_minimo"));
                    bebida.setPrecio(rs.getDouble("precio"));
                    listaProductos.add(bebida);
                }
            }
            return listaProductos;
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener los productos no vendidos al cliente: " + ex.getMessage());
        }
    }
    
    public ReporteProducto obtenerProductoMasVendidoACliente(int idCliente) throws SQLException {
        ReporteProducto producto = null;
        String consulta = "SELECT b.idBebida, b.bebida, SUM(dv.cantidad) AS cantidad_vendida, " +
                          "SUM(dv.total) AS total_recaudado " +
                          "FROM detalle_venta dv " +
                          "JOIN venta v ON dv.idVenta = v.idVenta " +
                          "JOIN bebida b ON dv.idBebida = b.idBebida " +
                          "WHERE v.idCliente = ? " +
                          "GROUP BY b.idBebida " +
                          "ORDER BY cantidad_vendida DESC " +
                          "LIMIT 1";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = new ReporteProducto();
                    producto.setIdBebida(rs.getInt("idBebida"));
                    producto.setNombreBebida(rs.getString("bebida"));
                    producto.setCantidadVendida(rs.getInt("cantidad_vendida"));
                    producto.setTotalRecaudado(rs.getDouble("total_recaudado"));
                }
            }
            return producto;
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener el producto más vendido al cliente: " + ex.getMessage());
        }
    }
}
