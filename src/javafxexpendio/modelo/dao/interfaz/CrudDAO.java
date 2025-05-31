/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Dell
 */
public interface CrudDAO <T>{
    
    boolean crear(T t) throws SQLException;
    T leer(Integer id) throws SQLException;
    boolean actualizar(T t) throws SQLException;
    boolean eliminar(Integer id) throws SQLException;
    List<T> leerTodo() throws SQLException;
}
