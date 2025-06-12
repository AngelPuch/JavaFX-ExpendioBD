package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import javafxexpendio.modelo.pojo.Usuario;

public interface InicioSesionDAO {
    
    Usuario verificarCredenciales(String username, String password) throws SQLException;
    
    
}
