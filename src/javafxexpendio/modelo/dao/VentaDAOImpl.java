package javafxexpendio.modelo.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.dao.interfaz.VentaDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class VentaDAOImpl implements VentaDAO {
    
    @Override
    public Map<String, Object> registrarVenta(Integer idCliente, LocalDate fecha, String folioFactura, 
            List<Map<String, Object>> detallesVenta) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        
        // Convertir la lista de detalles a formato JSON
        JSONArray detallesJSON = new JSONArray();
        
        for (Map<String, Object> detalle : detallesVenta) {
            JSONObject detalleJSON = new JSONObject();
            detalleJSON.put("idBebida", detalle.get("idBebida"));
            detalleJSON.put("cantidad", detalle.get("cantidad"));
            
            // La promoción es opcional
            if (detalle.containsKey("idPromocion")) {
                detalleJSON.put("idPromocion", detalle.get("idPromocion"));
            }
            
            detallesJSON.add(detalleJSON);
        }
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             CallableStatement cs = conexionBD.prepareCall("{CALL sp_registrar_venta_completa(?, ?, ?, ?, ?, ?)}")) {
            if (idCliente != null) {
                cs.setInt(1, idCliente);
            } else {
                cs.setNull(1, java.sql.Types.INTEGER);
            }
            cs.setDate(2, Date.valueOf(fecha));
            cs.setString(3, folioFactura);
            cs.setString(4, detallesJSON.toJSONString());
            cs.registerOutParameter(5, Types.INTEGER); // idVenta
            cs.registerOutParameter(6, Types.VARCHAR); // mensaje
            cs.execute();
            
            // Obtener los resultados
            Integer idVenta = cs.getInt(5);
            String mensaje = cs.getString(6);
            
            resultado.put("exito", idVenta != null && idVenta > 0);
            resultado.put("idVenta", idVenta);
            resultado.put("mensaje", mensaje);
            
        } catch (SQLException ex) {
            throw new SQLException("Error al registrar la venta: " + ex.getMessage());
        }
        
        return resultado;
    }
    
    @Override
    public List<Map<String, Object>> obtenerPromocionesDisponibles(int idBebida) throws SQLException {
        List<Map<String, Object>> promociones = new ArrayList<>();
        String consulta = "{CALL sp_obtener_promociones_bebida(?)}";
        
        try (Connection conexionBD = ConexionBD.getInstancia().abrirConexion();
             CallableStatement cs = conexionBD.prepareCall(consulta)) {
            
            cs.setInt(1, idBebida);
            
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> promocion = new HashMap<>();
                    promocion.put("idPromocion", rs.getInt("idPromocion"));
                    promocion.put("descuento", rs.getDouble("descuento"));
                    promocion.put("fechaInicio", rs.getDate("fecha_inicio").toLocalDate());
                    promocion.put("fechaFin", rs.getDate("fecha_fin").toLocalDate());
                    promocion.put("descripcion", rs.getString("descripcion"));
                    
                    promociones.add(promocion);
                }
            }
            
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener promociones disponibles: " + ex.getMessage());
        }
        
        return promociones;
    }
    
    // Método para calcular el precio con descuento (útil para mostrar en la interfaz antes de guardar)
    public double calcularPrecioConDescuento(double precioOriginal, double porcentajeDescuento) {
        return precioOriginal - (precioOriginal * porcentajeDescuento / 100);
    }
}