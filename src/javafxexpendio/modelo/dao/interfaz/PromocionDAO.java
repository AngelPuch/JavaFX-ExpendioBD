package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.util.List;
import javafxexpendio.modelo.pojo.Promocion;

public interface PromocionDAO {
    boolean crear(Promocion promocion) throws SQLException;
    boolean actualizar(Promocion promcion) throws SQLException;
    List<Promocion> leerTodo() throws SQLException;
    
}
