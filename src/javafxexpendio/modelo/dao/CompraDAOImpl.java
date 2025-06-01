package javafxexpendio.modelo.dao;

import java.sql.CallableStatement;
import javafxexpendio.modelo.dao.interfaz.CompraDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Compra;
import javafxexpendio.modelo.pojo.DetalleCompra;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class CompraDAOImpl implements CompraDAO {

    @Override
    public Compra crear(Compra compra) throws SQLException {
        String consulta = "INSERT INTO compra (fecha, idProveedor, folio_factura) VALUES (?, ?, ?)";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setObject(1, compra.getFecha());
            ps.setInt(2, compra.getIdProveedor());
            ps.setString(3, compra.getFolioFactura());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        compra.setIdCompra(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear la compra: " + ex.getMessage());
        }
        
        return compra;
    }

    @Override
    public Compra leer(Integer id) throws SQLException {
        Compra compra = null;
        String consulta = "SELECT c.idCompra, c.fecha, c.folio_factura, c.idProveedor, p.razon_social AS proveedor "
                + "FROM compra c "
                + "JOIN proveedor p ON c.idProveedor = p.idProveedor "
                + "WHERE c.idCompra = ?";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    compra = convertirRegistroCompra(rs);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return compra;
    }


    @Override
    public List<Compra> leerTodo() throws SQLException {
        List<Compra> compras = new ArrayList<>();
        String consulta = "SELECT c.idCompra, c.fecha, c.folio_factura, c.idProveedor, p.razon_social AS proveedor "
                + "FROM compra c "
                + "JOIN proveedor p ON c.idProveedor = p.idProveedor "
                + "ORDER BY c.fecha DESC";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta);
                ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Compra compra = convertirRegistroCompra(rs);
                compras.add(compra);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return compras;
    }

  @Override
    public boolean registrarCompra(Compra compra, List<DetalleCompra> detalles, int idPedidoProveedor) throws SQLException {
        try (Connection conexion = ConexionBD.abrirConexion()) {

            // Convertir detalles a JSON
            JSONArray detallesJson = new JSONArray();
            for (DetalleCompra detalle : detalles) {
                JSONObject detalleJson = new JSONObject();
                detalleJson.put("idBebida", detalle.getIdBebida());
                detalleJson.put("cantidad", detalle.getCantidad());
                detalleJson.put("precio_bebida", detalle.getPrecioBebida());
                detallesJson.add(detalleJson); // Correcto para json-simple
            }

            // Convertir pedidos relacionados a JSON
            JSONArray pedidosJson = new JSONArray();
            if (idPedidoProveedor > 0) {
                pedidosJson.add(idPedidoProveedor); // Correcto para json-simple
            }

            // Llamar al procedimiento almacenado
            String sql = "{CALL sp_registrar_compra_completa(?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conexion.prepareCall(sql)) {
                cs.setObject(1, compra.getFecha());
                cs.setInt(2, compra.getIdProveedor());
                cs.setString(3, compra.getFolioFactura());
                cs.setString(4, detallesJson.toString());
                cs.setString(5, pedidosJson.toString());
                cs.registerOutParameter(6, Types.INTEGER);
                cs.registerOutParameter(7, Types.VARCHAR);

                cs.execute();

                Integer idCompra = cs.getInt(6);
                String mensaje = cs.getString(7);

                if (idCompra != null && idCompra > 0) {
                    compra.setIdCompra(idCompra);
                    return true;
                } else {
                    // Si llegamos aquí, el procedimiento no generó un ID válido
                    // pero tampoco lanzó una excepción
                    throw new SQLException("No se pudo registrar la compra: " + mensaje);
                }
            }

        } catch (Exception ex) {
            throw new SQLException("Error al registrar la compra: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<DetalleCompra> obtenerDetalleCompra(int idCompra) throws SQLException {
        List<DetalleCompra> detalles = new ArrayList<>();
        String consulta = "SELECT dc.idCompra, dc.idBebida, b.bebida, dc.cantidad, dc.precio_bebida, dc.total "
                + "FROM detalle_compra dc "
                + "JOIN bebida b ON dc.idBebida = b.idBebida "
                + "WHERE dc.idCompra = ?";
        
        try (Connection conexion = ConexionBD.abrirConexion();
                PreparedStatement ps = conexion.prepareStatement(consulta)) {
            
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleCompra detalle = convertirRegistroDetalleCompra(rs);
                    detalles.add(detalle);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error: Sin conexión a la base de datos");
        }
        
        return detalles;
    }
    
    private Compra convertirRegistroCompra(ResultSet rs) throws SQLException {
        Compra compra = new Compra();
        compra.setIdCompra(rs.getInt("idCompra"));
        compra.setFecha(rs.getDate("fecha").toLocalDate());
        compra.setFolioFactura(rs.getString("folio_factura"));
        compra.setIdProveedor(rs.getInt("idProveedor"));
        compra.setProveedor(rs.getString("proveedor"));
        return compra;
    }
    
    private DetalleCompra convertirRegistroDetalleCompra(ResultSet rs) throws SQLException {
        DetalleCompra detalle = new DetalleCompra();
        detalle.setIdCompra(rs.getInt("idCompra"));
        detalle.setIdBebida(rs.getInt("idBebida"));
        detalle.setBebida(rs.getString("bebida"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioBebida(rs.getDouble("precio_bebida"));
        detalle.setTotal(rs.getDouble("total"));
        return detalle;
    }
}