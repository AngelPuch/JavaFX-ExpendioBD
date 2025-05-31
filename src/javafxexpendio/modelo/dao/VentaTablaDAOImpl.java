/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.dao.interfaz.VentaTablaDAO;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.modelo.pojo.DetalleVenta;
import javafxexpendio.modelo.pojo.Venta;
import javafxexpendio.modelo.pojo.VentaTabla;

/**
 *
 * @author Dell
 */
public class VentaTablaDAOImpl implements VentaTablaDAO{
    
    @Override
    public ArrayList<VentaTabla> obtenerTodasLasVentasTabla() throws SQLException {
        ArrayList<VentaTabla> ventasTabla = new ArrayList<>();
        String consulta = "SELECT v.idVenta, v.fecha, v.idCliente, c.nombre as nombreCliente, " +
                          "v.folio_factura, SUM(dv.total) as total, COUNT(dv.idBebida) as numProductos " +
                          "FROM venta v " +
                          "LEFT JOIN cliente c ON v.idCliente = c.idCliente " +
                          "LEFT JOIN detalle_venta dv ON v.idVenta = dv.idVenta " +
                          "GROUP BY v.idVenta " +
                          "ORDER BY v.fecha DESC";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ventasTabla.add(convertirRegistroVentaTabla(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return ventasTabla;
    }
    
    @Override
    public Venta obtenerVentaPorId(Integer idVenta) throws SQLException {
        Venta venta = null;
        String consulta = "SELECT v.idVenta, v.fecha, v.idCliente, v.folio_factura, " +
                          "c.nombre, c.telefono, c.correo, c.direccion " +
                          "FROM venta v " +
                          "LEFT JOIN cliente c ON v.idCliente = c.idCliente " +
                          "WHERE v.idVenta = ?";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setInt(1, idVenta);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    venta = convertirRegistroVenta(rs);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return venta;
    }
    
    @Override
    public ArrayList<VentaTabla> obtenerVentasPorRangoFechasTabla(Date fechaInicio, Date fechaFin) throws SQLException {
        ArrayList<VentaTabla> ventasTabla = new ArrayList<>();
        String consulta = "SELECT v.idVenta, v.fecha, v.idCliente, c.nombre as nombreCliente, " +
                          "v.folio_factura, SUM(dv.total) as total, COUNT(dv.idBebida) as numProductos " +
                          "FROM venta v " +
                          "LEFT JOIN cliente c ON v.idCliente = c.idCliente " +
                          "LEFT JOIN detalle_venta dv ON v.idVenta = dv.idVenta " +
                          "WHERE v.fecha BETWEEN ? AND ? " +
                          "GROUP BY v.idVenta " +
                          "ORDER BY v.fecha DESC";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventasTabla.add(convertirRegistroVentaTabla(rs));
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return ventasTabla;
    }
    
    @Override
    public String obtenerProductoMasVendido() throws SQLException {
        String producto = "N/A";
        String consulta = "SELECT b.bebida, SUM(dv.cantidad) as cantidad " +
                          "FROM detalle_venta dv " +
                          "JOIN bebida b ON dv.idBebida = b.idBebida " +
                          "GROUP BY b.idBebida " +
                          "ORDER BY cantidad DESC " +
                          "LIMIT 1";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                producto = rs.getString("bebida") + " (" + rs.getInt("cantidad") + " unidades)";
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return producto;
    }

    @Override
    public ArrayList<DetalleVenta> obtenerDetallesVenta(Integer idVenta) throws SQLException {
        ArrayList<DetalleVenta> detalles = new ArrayList<>();
        String consulta = "SELECT dv.idVenta, dv.idBebida, b.bebida, dv.cantidad, dv.precio_bebida, dv.total " +
                          "FROM detalle_venta dv " +
                          "JOIN bebida b ON dv.idBebida = b.idBebida " +
                          "WHERE dv.idVenta = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setInt(1, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(convertirRegistroDetalleVenta(rs));
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }

        return detalles;
    }
    
    private DetalleVenta convertirRegistroDetalleVenta(ResultSet rs) throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdVenta(rs.getInt("idVenta"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioBebida(rs.getDouble("precio_bebida"));
        detalle.setTotal(rs.getDouble("total"));

        // Crear objeto Bebida
        Bebida bebida = new Bebida();
        bebida.setIdBebida(rs.getInt("idBebida"));
        bebida.setBebida(rs.getString("bebida"));
        // Si necesitas más campos de bebida, deberías modificar la consulta SQL
        // para incluir esos campos adicionales

        detalle.setBebida(bebida);

        return detalle;
    }
    
    private VentaTabla convertirRegistroVentaTabla(ResultSet rs) throws SQLException {
        VentaTabla ventaTabla = new VentaTabla();
        ventaTabla.setIdVenta(rs.getInt("idVenta"));
        ventaTabla.setFecha(rs.getDate("fecha"));
        ventaTabla.setNombreCliente(rs.getString("nombreCliente"));
        ventaTabla.setFolioFactura(rs.getString("folio_factura"));
        ventaTabla.setTotal(rs.getDouble("total"));
        ventaTabla.setNumProductos(rs.getInt("numProductos"));
        return ventaTabla;
    }
    
    private Venta convertirRegistroVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVenta(rs.getInt("idVenta"));
        venta.setFecha(rs.getDate("fecha"));
        venta.setFolioFactura(rs.getString("folio_factura"));
        
        // Si hay un cliente asociado, crearlo
        int idCliente = rs.getInt("idCliente");
        if (idCliente > 0) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(idCliente);
            cliente.setNombre(rs.getString("nombre"));
            cliente.setTelefono(rs.getString("telefono"));
            cliente.setCorreo(rs.getString("correo"));
            cliente.setDireccion(rs.getString("direccion"));
            venta.setCliente(cliente);
        }
        
        return venta;
    }
}
