/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javafxexpendio.modelo.dao.interfaz;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javafxexpendio.modelo.pojo.Venta;

/**
 *
 * @author Dell
 */
public interface VentaDAO {

    /**
     * Registra una venta completa en el sistema.
     *
     * @param idCliente      ID del cliente que realiza la compra.
     * @param fecha          Fecha de la venta.
     * @param folioFactura   Folio de la factura de la venta.
     * @param detallesVenta  Lista con los detalles de cada producto vendido (idBebida, cantidad, idPromocion opcional).
     * @return               Un mapa con el resultado (exito, idVenta, mensaje).
     * @throws SQLException  Si ocurre un error al registrar la venta.
     */
    Map<String, Object> registrarVenta(Integer idCliente, LocalDate fecha, String folioFactura,
                                       List<Map<String, Object>> detallesVenta) throws SQLException;

    /**
     * Obtiene las promociones disponibles para una bebida específica.
     *
     * @param idBebida       ID de la bebida.
     * @return               Lista de mapas con la información de cada promoción.
     * @throws SQLException  Si ocurre un error al obtener las promociones.
     */
    List<Map<String, Object>> obtenerPromocionesDisponibles(int idBebida) throws SQLException;
}
