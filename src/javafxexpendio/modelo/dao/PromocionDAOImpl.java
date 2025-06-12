
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.dao;

import javafxexpendio.modelo.dao.interfaz.PromocionDAO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Promocion;

/**
 *
 * @author Dell
 */
public class PromocionDAOImpl implements PromocionDAO{

    @Override
    public boolean crear(Promocion promocion) throws SQLException {
        String sentencia = "INSERT INTO promocion (descuento, fecha_inicio, fecha_fin, descripcion, idBebida) "
                + "VALUES(?, ?, ?, ?, ?)";
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosPromocion(ps, promocion);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos" + ex.getMessage());
        }
        
    }

    @Override
    public boolean actualizar(Promocion promocion) throws SQLException {
        String sentencia = "UPDATE promocion SET descuento = ?, fecha_inicio = ?, fecha_fin = ?, descripcion = ?, idBebida = ? "
                + "WHERE idPromocion = ?";

        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {

            asignarParametrosPromocion(ps, promocion);
            ps.setInt(6, promocion.getIdPromocion());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos." + ex.getMessage());
        }
    }

    @Override
    public List<Promocion> leerTodo() throws SQLException {
        List<Promocion> promociones = new ArrayList<>();
        String consulta = "SELECT p.idPromocion, p.descuento, p.fecha_inicio, p.fecha_fin, p.descripcion, " +
                          "b.idBebida, b.bebida, b.stock, b.stock_minimo, b.precio, b.contenido_neto " +
                          "FROM promocion p " +
                          "JOIN bebida b ON p.idBebida = b.idBebida";

        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                promociones.add(convertirRegistroPromocion(rs));
            }

        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos.");
        }

        return promociones;
    }
    
    private void asignarParametrosPromocion(PreparedStatement ps, Promocion promocion) throws SQLException {
        ps.setDouble(1, promocion.getDescuento());
        ps.setDate(2, new Date(promocion.getFechaInicio().getTime()));
        ps.setDate(3, new Date(promocion.getFechaFin().getTime()));
        ps.setString(4, promocion.getDescripcion());
        ps.setInt(5, promocion.getBebida().getIdBebida());       
    }
    
    private Promocion convertirRegistroPromocion(ResultSet rs) throws SQLException {
        Bebida bebida = new Bebida();
        bebida.setIdBebida(rs.getInt("idBebida"));
        bebida.setBebida(rs.getString("bebida"));
        bebida.setStock(rs.getInt("stock"));
        bebida.setStockMinimo(rs.getInt("stock_minimo"));
        bebida.setPrecio(rs.getDouble("precio"));
        bebida.setContenidoNeto(rs.getString("contenido_neto"));

        Promocion promocion = new Promocion();
        promocion.setIdPromocion(rs.getInt("idPromocion"));
        promocion.setDescuento(rs.getDouble("descuento"));
        promocion.setFechaInicio(rs.getDate("fecha_inicio"));
        promocion.setFechaFin(rs.getDate("fecha_fin"));
        promocion.setDescripcion(rs.getString("descripcion"));
        promocion.setBebida(bebida);

        return promocion;
    }

    
}
