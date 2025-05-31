/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import javafxexpendio.modelo.pojo.Usuario;

/**
 *
 * @author Dell
 */
public interface InicioSesionDAO {
    
    Usuario verificarCredenciales(String username, String password) throws SQLException;
    
    
}
