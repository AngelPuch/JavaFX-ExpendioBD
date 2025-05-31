package javafxexpendio.modelo.dao;

import javafxexpendio.modelo.dao.interfaz.CompraDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafxexpendio.modelo.ConexionBD;
import javafxexpendio.modelo.pojo.Compra;
import javafxexpendio.modelo.pojo.DetalleCompra;

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
    public Map<String, Object> registrarCompra(Compra compra, List<DetalleCompra> detalles, int idPedidoProveedor) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("exito", false);
        
        Connection conexion = null;
        
        try {
            conexion = ConexionBD.abrirConexion();
            conexion.setAutoCommit(false);
            
            // Insertar compra
            String consulta = "INSERT INTO compra (fecha, idProveedor, folio_factura) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, compra.getFecha());
                ps.setInt(2, compra.getIdProveedor());
                ps.setString(3, compra.getFolioFactura());
                
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas == 0) {
                    throw new SQLException("No se pudo registrar la compra, no se insertó ninguna fila.");
                }
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        compra.setIdCompra(rs.getInt(1));
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la compra.");
                    }
                }
            }
            
            // Insertar detalles de compra
            consulta = "INSERT INTO detalle_compra (idCompra, idBebida, cantidad, precio_bebida, total) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
                for (DetalleCompra detalle : detalles) {
                    ps.setInt(1, compra.getIdCompra());
                    ps.setInt(2, detalle.getIdBebida());
                    ps.setInt(3, detalle.getCantidad());
                    ps.setDouble(4, detalle.getPrecioBebida());
                    ps.setDouble(5, detalle.getTotal());
                    
                    ps.addBatch();
                }
                
                ps.executeBatch();
            }
            
            // Actualizar stock de bebidas
            consulta = "UPDATE bebida SET stock = stock + ? WHERE idBebida = ?";
            try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
                for (DetalleCompra detalle : detalles) {
                    ps.setInt(1, detalle.getCantidad());
                    ps.setInt(2, detalle.getIdBebida());
                    
                    ps.addBatch();
                }
                
                ps.executeBatch();
            }
            
            // Relacionar compra con pedido
            if (idPedidoProveedor > 0) {
                consulta = "INSERT INTO compra_has_pedido (idCompra, idPedidoProveedor) VALUES (?, ?)";
                try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
                    ps.setInt(1, compra.getIdCompra());
                    ps.setInt(2, idPedidoProveedor);
                    
                    ps.executeUpdate();
                }
                
                // Actualizar estado de pedido a "Realizado"
                consulta = "UPDATE pedido_proveedor SET idEstadoPedido = 2 WHERE idPedidoProveedor = ?";
                try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
                    ps.setInt(1, idPedidoProveedor);
                    ps.executeUpdate();
                }
            }
            
            conexion.commit();
            
            resultado.put("exito", true);
            resultado.put("idCompra", compra.getIdCompra());
            resultado.put("mensaje", "Compra registrada correctamente.");
            
        } catch (SQLException ex) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            resultado.put("mensaje", ex.getMessage());
            throw new SQLException("Error al registrar la compra: " + ex.getMessage());
        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        return resultado;
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