/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

/**
 *
 * @author zenbook i5
 */
public class DetalleCompra {
    private int idCompra;
    private int idBebida;
    private String bebida;
    private int cantidad;
    private double precioBebida;
    private double total;
    
    public DetalleCompra() {
    }
    
    public DetalleCompra(int idBebida, String bebida, int cantidad, double precioBebida) {
        this.idBebida = idBebida;
        this.bebida = bebida;
        this.cantidad = cantidad;
        this.precioBebida = precioBebida;
        this.total = cantidad * precioBebida;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdBebida() {
        return idBebida;
    }

    public void setIdBebida(int idBebida) {
        this.idBebida = idBebida;
    }

    public String getBebida() {
        return bebida;
    }

    public void setBebida(String bebida) {
        this.bebida = bebida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.total = cantidad * precioBebida;
    }

    public double getPrecioBebida() {
        return precioBebida;
    }

    public void setPrecioBebida(double precioBebida) {
        this.precioBebida = precioBebida;
        this.total = cantidad * precioBebida;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
