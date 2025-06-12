package javafxexpendio.modelo.pojo;

import java.time.LocalDate;

public class ReporteVenta {
    private int idVenta;
    private LocalDate fecha;
    private String folioFactura;
    private String cliente;
    private String rfc;
    private double totalVenta;
    
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public String getFolioFactura() { return folioFactura; }
    public void setFolioFactura(String folioFactura) { this.folioFactura = folioFactura; }
    
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    
    public double getTotalVenta() { return totalVenta; }
    public void setTotalVenta(double totalVenta) { this.totalVenta = totalVenta; }
}