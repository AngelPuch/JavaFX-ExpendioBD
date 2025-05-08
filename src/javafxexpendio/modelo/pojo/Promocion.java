/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

import java.util.Date;

/**
 *
 * @author rodrigoluna
 */
public class Promocion {
    private int idPromocion;
    private double descuento;
    private Date fechaInicio;
    private Date fechaFin;
    private Bebida bebida;
    private Venta venta;

    public Promocion() {
    }

    public Promocion(int idPromocion, double descuento, Date fechaInicio, Date fechaFin, Bebida bebida, Venta venta) {
        this.idPromocion = idPromocion;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.bebida = bebida;
        this.venta = venta;
    }

    public int getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(int idPromocion) {
        this.idPromocion = idPromocion;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }
    
    
}
