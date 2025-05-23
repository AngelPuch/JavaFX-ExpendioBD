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
import java.util.List;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Bebida;

/**
 *
 * @author Dell
 */
public class BebidaDAOImpl implements DAO<Bebida> {
    
    @Override
    public boolean crear(Bebida bebida) throws SQLException {
        String sentencia = "INSERT INTO bebida (bebida, stock, stock_minimo, precio) "
                + "VALUES(?, ?, ?, ?)";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosBebida(ps, bebida);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        } 
    }

    @Override
    public Bebida leer(Integer id) throws SQLException {
        Bebida bebida = null;
        String consulta = "SELECT idBebida, bebida, stock, stock_minimo, precio "
                + "FROM bebida WHERE idBebida = ?";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bebida = convertirRegistroBebida(rs);                    
                }
            }
        } catch (SQLException ex ){
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
        
        return bebida;
    }

    @Override
    public boolean actualizar(Bebida bebida) throws SQLException {
        String sentencia = "UPDATE bebida SET bebida = ?, stock = ?, stock_minimo = ?, precio = ? "
                + "WHERE idBebida = ?";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosBebida(ps, bebida);
            ps.setInt(5, bebida.getIdBebida());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
    }

    @Override
    public boolean eliminar(Integer id) throws SQLException {
        String sentencia = "DELETE FROM bebida WHERE idBebida = ?";
        
        try (Connection conexionBD = ConexionBD.abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
    }

    @Override
    public List<Bebida> leerTodo() throws SQLException {
        List<Bebida> listaBebidas = new ArrayList<>();
        String consulta = "SELECT idBebida, bebida, stock, stock_minimo, precio FROM bebida";
        
        try(Connection conexionBD = ConexionBD.abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(consulta);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {                
                listaBebidas.add(convertirRegistroBebida(rs));
                
            }
            return listaBebidas;
        } catch(SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
    }
    
    private void asignarParametrosBebida(PreparedStatement ps, Bebida bebida) throws SQLException {
        ps.setString(1, bebida.getBebida());
        ps.setInt(2, bebida.getStock());
        ps.setInt(3, bebida.getStockMinimo());
        ps.setDouble(4, bebida.getPrecio());
    }
    
    private Bebida convertirRegistroBebida(ResultSet rs) throws SQLException {
        Bebida bebida = new Bebida();
        bebida.setIdBebida(rs.getInt("idBebida"));
        bebida.setBebida(rs.getString("bebida"));
        bebida.setStock(rs.getInt("stock"));
        bebida.setStockMinimo(rs.getInt("stock_minimo"));
        bebida.setPrecio(rs.getDouble("precio"));
        
        return bebida;
    }
}
