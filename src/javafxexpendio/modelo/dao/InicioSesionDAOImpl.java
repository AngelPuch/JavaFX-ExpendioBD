package javafxexpendio.modelo.dao;

import javafxexpendio.modelo.dao.interfaz.InicioSesionDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

public class InicioSesionDAOImpl implements InicioSesionDAO{

    @Override
    public Usuario verificarCredenciales(String username, String password) throws SQLException {
        Usuario usuarioSesion = null;
        String consulta = "SELECT idUsuario, username, nombre, apellidoPaterno, apellidoMaterno, t.tipo, password "
                + "FROM usuario u "
                + "JOIN tipoUsuario t ON u.idTipoUsuario = t.idTipoUsuario "
                + "WHERE username = ?";

        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             PreparedStatement ps = conexionBD.prepareStatement(consulta)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashGuardado = rs.getString("password");
                    if (Utilidad.verificarPassword(password, hashGuardado)) {
                        usuarioSesion = convertirRegistroUsuario(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos" + ex.getMessage());
        }
        return usuarioSesion;
    }
    
    private Usuario convertirRegistroUsuario(ResultSet rs) throws SQLException{
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("idUsuario"));
        usuario.setUsername(rs.getString("username"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidoPaterno(rs.getString("apellidoPaterno"));
        usuario.setApellidoMaterno(rs.getString("apellidoMaterno") != null ? rs.getString("apellidoMaterno") : "");
        usuario.setTipoUsuario(rs.getString("tipo"));
        usuario.setPassword(rs.getString("password"));
        
        return usuario;
        
    }
    
}
