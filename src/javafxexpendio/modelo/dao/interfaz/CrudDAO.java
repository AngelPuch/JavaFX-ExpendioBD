package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO <T>{
    
    boolean crear(T t) throws SQLException;
    T leer(Integer id) throws SQLException;
    boolean actualizar(T t) throws SQLException;
    boolean eliminar(Integer id) throws SQLException;
    List<T> leerTodo() throws SQLException;
}
