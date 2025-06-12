/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.dao;

import javafxexpendio.modelo.dao.interfaz.CrudDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Proveedor;

/**
 *
 * @author Dell
 */
public class ProveedorDAOImpl implements CrudDAO<Proveedor>{
    
    @Override
    public boolean crear(Proveedor proveedor) throws SQLException {
        String sentencia = "INSERT INTO proveedor (razon_social, telefono, correo) "
                + "VALUES (?, ?, ?)";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosProveedor(ps, proveedor);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
    }

    @Override
    public Proveedor leer(Integer id) throws SQLException {
        Proveedor proveedor = null;
        String consulta = "SELECT idProveedor, razon_social, telefono, correo "
                        + "FROM proveedor WHERE idProveedor = ?";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    proveedor = convertirRegistroProveedor(rs);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return proveedor;
    }

    @Override
    public boolean actualizar(Proveedor proveedor) throws SQLException {
        String sentencia = "UPDATE proveedor SET razon_social = ?, telefono = ?, correo = ? "
                         + "WHERE idProveedor = ?";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosProveedor(ps, proveedor);
            ps.setInt(4, proveedor.getIdProveedor());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
    }

    @Override
    public boolean eliminar(Integer id) throws SQLException {
        String sentencia = "DELETE FROM proveedor WHERE idProveedor = ?";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23000")) { // Código de estado SQL para violación de restricción
            throw new SQLException("No se puede eliminar el proveedor porque está relacionado con otros registros.");
            } else {
            throw new SQLException("Error en la base de datos: " + ex.getMessage());
            }
        }
    }

    @Override
    public List<Proveedor> leerTodo() throws SQLException {
        List<Proveedor> listaProveedores = new ArrayList<>();
        String consulta = "SELECT idProveedor, razon_social, telefono, correo FROM proveedor";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaProveedores.add(convertirRegistroProveedor(rs));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return listaProveedores;
    }

    private void asignarParametrosProveedor(PreparedStatement ps, Proveedor proveedor) throws SQLException {
        ps.setString(1, proveedor.getRazonSocial());
        ps.setString(2, proveedor.getTelefono());
        ps.setString(3, proveedor.getCorreo());
    }

    private Proveedor convertirRegistroProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(rs.getInt("idProveedor"));
        proveedor.setRazonSocial(rs.getString("razon_social"));
        proveedor.setTelefono(rs.getString("telefono"));
        proveedor.setCorreo(rs.getString("correo"));
        return proveedor;
    }
    
}
