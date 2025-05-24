package javafxexpendio.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.TipoUsuario;
import javafxexpendio.modelo.pojo.Usuario;

public class UsuarioDAOImpl implements DAO<Usuario> {

    @Override
    public boolean crear(Usuario usuario) throws SQLException {
        String sentencia = "INSERT INTO usuario (username, nombre, apellidoPaterno, apellidoMaterno, idTipoUsuario, password) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosUsuario(ps, usuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
    }

    @Override
    public Usuario leer(Integer id) throws SQLException {
        Usuario usuario = null;
        String consulta = "SELECT u.idUsuario, u.username, u.nombre, u.apellidoPaterno, u.apellidoMaterno, "
                + "u.idTipoUsuario, t.tipo AS tipoUsuario, u.password "
                + "FROM usuario u "
                + "JOIN tipoUsuario t ON u.idTipoUsuario = t.idTipoUsuario "
                + "WHERE u.idUsuario = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = convertirRegistroUsuario(rs);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }

        return usuario;
    }

    @Override
    public boolean actualizar(Usuario usuario) throws SQLException {
        String sentencia = "UPDATE usuario SET username = ?, nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, idTipoUsuario = ?, password = ? "
                + "WHERE idUsuario = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            asignarParametrosUsuario(ps, usuario);
            ps.setInt(7, usuario.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
    }

    @Override
    public boolean eliminar(Integer id) throws SQLException {
        String sentencia = "DELETE FROM usuario WHERE idUsuario = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(sentencia)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
    }

    @Override
    public List<Usuario> leerTodo() throws SQLException {
        List<Usuario> listaUsuarios = new ArrayList<>();
        String consulta = "SELECT u.idUsuario, u.username, u.nombre, u.apellidoPaterno, u.apellidoMaterno, "
                + "u.idTipoUsuario, t.tipo AS tipoUsuario, u.password "
                + "FROM usuario u "
                + "JOIN tipoUsuario t ON u.idTipoUsuario = t.idTipoUsuario";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaUsuarios.add(convertirRegistroUsuario(rs));
            }
            return listaUsuarios;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
    }
    
    public List<TipoUsuario> leerTipoUsuario() throws SQLException {
        List<TipoUsuario> listaTipoUsuarios = new ArrayList<>();
        String consulta = "SELECT idTipoUsuario, tipo FROM tipoUsuario";
        
        try(Connection conexionBD = ConexionBD.abrirConexion();
                PreparedStatement ps = conexionBD.prepareStatement(consulta);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                listaTipoUsuarios.add(new TipoUsuario(rs.getInt("idTipoUsuario"), rs.getString("tipo")));
            }
            return listaTipoUsuarios;
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos"); 
        }
    }

    private void asignarParametrosUsuario(PreparedStatement ps, Usuario usuario) throws SQLException {
        ps.setString(1, usuario.getUsername());
        ps.setString(2, usuario.getNombre());
        ps.setString(3, usuario.getApellidoPaterno());
        ps.setString(4, usuario.getApellidoMaterno());
        ps.setInt(5, usuario.getIdTipoUsuario());
        ps.setString(6, usuario.getPassword());
    }

    private Usuario convertirRegistroUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("idUsuario"));
        usuario.setUsername(rs.getString("username"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidoPaterno(rs.getString("apellidoPaterno"));
        usuario.setApellidoMaterno(rs.getString("apellidoMaterno"));
        usuario.setIdTipoUsuario(rs.getInt("idTipoUsuario"));
        usuario.setTipoUsuario(rs.getString("tipoUsuario"));
        usuario.setPassword(rs.getString("password"));
        return usuario;
    }
}
