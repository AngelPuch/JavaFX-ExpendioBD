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
import javafxexpendio.modelo.pojo.Cliente;

/**
 *
 * @author Dell
 */
public class ClienteDAOImpl implements CrudDAO<Cliente>{

    @Override
    public boolean crear(Cliente cliente) throws SQLException {
        String sentencia = "INSERT INTO cliente (nombre, telefono, correo, direccion) "
                + "VALUES(?, ?, ?, ?)";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosCliente(ps, cliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos" + ex.getMessage());
        } 
    }

    @Override
    public Cliente leer(Integer id) throws SQLException {
        Cliente cliente = null;
        String consulta = "SELECT idCliente, nombre, telefono, correo, direccion "
                + "FROM cliente WHERE idCliente = ?";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cliente = convertirRegistroCliente(rs);                    
                }
            }
        } catch (SQLException ex ){
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
        
        return cliente;
    }

    @Override
    public boolean actualizar(Cliente cliente) throws SQLException {
        String sentencia = "UPDATE cliente SET nombre = ?, telefono = ?, correo = ?, direccion = ? "
                + "WHERE idCliente = ?";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosCliente(ps, cliente);
            ps.setInt(5, cliente.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
    }

    @Override
    public boolean eliminar(Integer id) throws SQLException {
        String sentencia = "DELETE FROM cliente WHERE idCliente = ?";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23000")) { // Código de estado SQL para violación de restricción
            throw new SQLException("No se puede eliminar el cliente porque está relacionada con otros registros.");
            } else {
            throw new SQLException("Error en la base de datos: " + ex.getMessage());
            }
        }
    }

    @Override
    public List<Cliente> leerTodo() throws SQLException {
        List<Cliente> listaClientes = new ArrayList<>();
        String consulta = "SELECT idCliente, nombre, telefono, correo, direccion FROM cliente";
        
        try(Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(consulta);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {                
                listaClientes.add(convertirRegistroCliente(rs));
                
            }
            return listaClientes;
        } catch(SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
    }
    
    private void asignarParametrosCliente(PreparedStatement ps, Cliente cliente) throws SQLException {
        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getTelefono());
        ps.setString(3, cliente.getCorreo());
        ps.setString(4, cliente.getDireccion());
    }
    
    private Cliente convertirRegistroCliente(ResultSet rs) throws SQLException{
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("idCliente"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setDireccion(rs.getString("direccion"));
        
        return cliente;
    }
    
}
