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
    private Compra compra;
    private Bebida bebida;
    private int cantidad;
    private int total;
    private double precioBebida;

    public DetalleCompra() {
    }

    public DetalleCompra(Compra compra, Bebida bebida, int cantidad, int total, double precioBebida) {
        this.compra = compra;
        this.bebida = bebida;
        this.cantidad = cantidad;
        this.total = total;
        this.precioBebida = precioBebida;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getPrecioBebida() {
        return precioBebida;
    }

    public void setPrecioBebida(double precioBebida) {
        this.precioBebida = precioBebida;
    }
    
}
