/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Cliente;

/**
 *
 * @author Dell
 */
public class ClienteDAO implements InterfaceDAO<Cliente>{

    @Override
    public boolean crear(Cliente cliente) throws SQLException {
        Connection conexionBD = ConexionBD.abrirConexion();
        
        if (conexionBD != null ) {
            String sentencia = "INSERT INTO cliente (nombre, telefono, correo, direccion) "
                + "VALUES(?, ?, ?, ?)";
            PreparedStatement ps = conexionBD.prepareStatement(sentencia);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getDireccion());
        
            int filasAfectadas = ps.executeUpdate();
            conexionBD.close();
            ps.close();
            
            return filasAfectadas > 0;
        }else {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
    }

    @Override
    public Cliente leer(Integer id) throws SQLException {
        Cliente cliente = null;
        Connection conexionBD = ConexionBD.abrirConexion();
        
        if (conexionBD != null) {
            String consulta = "SELECT idCliente, nombre, telefono, correo, direccion "
                + "FROM cliente WHERE idCliente = ?";
            PreparedStatement ps = conexionBD.prepareStatement(consulta);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                cliente = convertirRegistroCliente(rs);
            }
            
            conexionBD.close();
            ps.close();
            rs.close();
        } else {
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
        
        return cliente;
    }

    @Override
    public boolean actualizar(Cliente cliente) throws SQLException {
        return true;
    }

    @Override
    public boolean eliminar(Integer id) throws SQLException {
        return true;
    }

    @Override
    public List<Cliente> leerTodo() throws SQLException {
        return null;
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
