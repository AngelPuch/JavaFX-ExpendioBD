/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

import java.util.Date;

/**
 *
 * @author zenbook i5
 */
public class Compra {
    private int idCompra;
    private Date fecha;
    private String folioFactura;
    private Proveedor proveedor;

    public Compra() {
    }

    public Compra(int idCompra, Date fecha, String folioFactura, Proveedor proveedor) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.folioFactura = folioFactura;
        this.proveedor = proveedor;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFolioFactura() {
        return folioFactura;
    }

    public void setFolioFactura(String folioFactura) {
        this.folioFactura = folioFactura;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    
    
}
