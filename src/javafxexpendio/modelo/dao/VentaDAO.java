/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao;

import java.sql.SQLException;
import java.util.List;
import javafxexpendio.modelo.pojo.Venta;

/**
 *
 * @author Dell
 */
public interface VentaDAO {
    boolean crear (Venta venta) throws SQLException;    
    
}
