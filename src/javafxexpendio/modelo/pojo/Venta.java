package javafxexpendio.modelo.pojo;

import java.util.Date;

public class Venta {
    private int idVenta;
    private Date fecha;
    private Cliente cliente;
    private String folioFactura;

    public Venta() {
    }

    public Venta(int idVenta, Date fecha, Cliente cliente, String folioFactura) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.cliente = cliente;
        this.folioFactura = folioFactura;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFolioFactura() {
        return folioFactura;
    }

    public void setFolioFactura(String folioFactura) {
        this.folioFactura = folioFactura;
    }
    
    
}
