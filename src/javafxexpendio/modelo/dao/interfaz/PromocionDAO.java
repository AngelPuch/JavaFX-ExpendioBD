/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.util.List;
import javafxexpendio.modelo.pojo.Promocion;

/**
 *
 * @author Dell
 */
public interface PromocionDAO {
    boolean crear(Promocion promocion) throws SQLException;
    boolean actualizar(Promocion promcion) throws SQLException;
    List<Promocion> leerTodo() throws SQLException;
    
}
