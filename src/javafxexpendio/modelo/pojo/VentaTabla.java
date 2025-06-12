package javafxexpendio.modelo.pojo;

import java.util.Date;

public class VentaTabla {
    private int idVenta;
    private Date fecha;
    private String nombreCliente;
    private String folioFactura;
    private double total;
    private int numProductos;
    
    public VentaTabla() {
    }
    
    public VentaTabla(int idVenta, Date fecha, String nombreCliente, String folioFactura, double total, int numProductos) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.nombreCliente = nombreCliente;
        this.folioFactura = folioFactura;
        this.total = total;
        this.numProductos = numProductos;
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

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFolioFactura() {
        return folioFactura;
    }

    public void setFolioFactura(String folioFactura) {
        this.folioFactura = folioFactura;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getNumProductos() {
        return numProductos;
    }

    public void setNumProductos(int numProductos) {
        this.numProductos = numProductos;
    }
}